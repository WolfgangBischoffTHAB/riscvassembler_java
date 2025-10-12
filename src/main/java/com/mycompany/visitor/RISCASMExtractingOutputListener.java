package com.mycompany.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.ASTNode;
import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

import riscvasm.RISCVASMParser;
import riscvasm.RISCVASMParser.Csv_numeric_listContext;
import riscvasm.RISCVASMParser.ExprContext;
import riscvasm.RISCVASMParser.ModifierContext;
import riscvasm.RISCVASMParser.OffsetContext;
import riscvasm.RISCVASMParser.ParamContext;
import riscvasm.RISCVASMParser.RegisterContext;
import riscvasm.RISCVASMParserBaseListener;

/**
 * Listener for AST traversal visitor pattern.
 * This listener assembles AsmLineS from all informations it
 * finds inside the AST while visiting.
 */
@SuppressWarnings("unchecked")
public class RISCASMExtractingOutputListener extends RISCVASMParserBaseListener {

    private static final Logger logger = LoggerFactory.getLogger(RISCASMExtractingOutputListener.class);

    public Map<String, Section> sectionMap;

    public Section currentSection;

    public List<AsmLine<RISCVRegister>> asmLines;

    @SuppressWarnings("rawtypes")
    private AsmLine asmLine = new AsmLine();

    private int sourceLine = 1;

    public Section dummySection;

    public Stack<ASTNode> exprStack = new Stack<>();

    //
    // RISC V Extension (RVV Vector extension)
    //

    @Override
    public void exitRvv_type(RISCVASMParser.Rvv_typeContext ctx) {
        logger.info("exitRvv_type: " + ctx.getText());
    }

    @Override
    public void exitRvv_sew(RISCVASMParser.Rvv_sewContext ctx) {
        logger.info("exitRvv_sew: " + ctx.getText());
        asmLine.rvvSew = ctx.getText();
    }

    @Override
    public void exitRvv_lmul(RISCVASMParser.Rvv_lmulContext ctx) {
        logger.info("exitRvv_lmul: " + ctx.getText());
        asmLine.rvvLmul = ctx.getText();
    }

    @Override
    public void exitRvv_tail(RISCVASMParser.Rvv_tailContext ctx) {
        logger.info("exitRvv_tail: " + ctx.getText());
        asmLine.rvvTail = ctx.getText();
    }

    @Override
    public void exitRvv_mask(RISCVASMParser.Rvv_maskContext ctx) {
        logger.info("exitRvv_mask: " + ctx.getText());
        asmLine.rvvMask = ctx.getText();
    }

    @Override
    public void exitExpr(RISCVASMParser.ExprContext ctx) {

        ASTNode astNode = new ASTNode();

        // case: a single node expression
        // push a node onto the stack
        if (ctx.children.size() == 1) {

            RegisterContext registerContext = ctx.register();
            if (registerContext != null) {
                astNode.isRegister = true;
                astNode.register = RISCVRegister.fromString(registerContext.getText());
            }

            TerminalNode numericTerminalNode = ctx.NUMERIC();
            if (numericTerminalNode != null) {
                astNode.isNumeric = true;
                astNode.numeric = NumberParseUtil.parseLong(numericTerminalNode.toString());
            }

            numericTerminalNode = ctx.HEX_NUMERIC();
            if (numericTerminalNode != null) {
                astNode.isHexNumeric = true;
                String numeric = numericTerminalNode.toString();
                astNode.hexNumeric = NumberParseUtil.parseLong(numeric);
            }

            TerminalNode stringLiteralTerminalNode = ctx.IDENTIFIER();
            if (stringLiteralTerminalNode != null) {
                astNode.isStringLiteral = true;
                astNode.identifier = stringLiteralTerminalNode.getText();
            }

            exprStack.push(astNode);

            return;

        } else if (ctx.children.size() == 3) {

            ASTNode rhs = exprStack.pop();
            ASTNode lhs = exprStack.pop();

            astNode.operatorAsString = ctx.children.get(1).getText();

            astNode.rhs = rhs;
            rhs.parent = astNode;

            astNode.lhs = lhs;
            lhs.parent = astNode;

            exprStack.push(astNode);

            return;

        }

        throw new RuntimeException("Not implemented yet!");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void exitAsm_line(RISCVASMParser.Asm_lineContext ctx) {

        asmLine.sourceLine = sourceLine;
        asmLine.section = currentSection;
        asmLines.add(asmLine);

        asmLine = new AsmLine();
        sourceLine++;
    }

    @Override
    public void exitMnemonic(RISCVASMParser.MnemonicContext ctx) {
        asmLine.mnemonic = Mnemonic.fromString(ctx.getText());
    }

    /**
     * (Almos) every assembler line that contains a mnemonic has a params
     * grammar element which has the individual parameters as children.
     */
    @Override
    public void exitParams(RISCVASMParser.ParamsContext ctx) {

        boolean isOffset = false;
        boolean isRegister = false;
        boolean isNumeric = false;
        boolean isHexNumeric = false;
        boolean isStringLiteral = false;

        boolean skipNode = false;

        try {

            System.out.println(ctx.getText());

            // ignore commas between parameters
            int index = ctx.children.size();
            //index = (index / 2) + 1;
            //index = (index + 1) / 2;
            index /= 2;

            // check if this is a RVV extension command by looking for the type subnode
            RuleContext rContext = (RuleContext) ctx.getChild(ctx.getChildCount() - 1);
            if (rContext.getRuleIndex() == RISCVASMParser.RULE_rvv_type) {
                logger.info(rContext.getText());
                skipNode = true;
            }

            // iterate over all parameters in reverse direction

            @SuppressWarnings("rawtypes")
            ListIterator listIterator = ctx.param().listIterator(ctx.param().size());
            while (listIterator.hasPrevious()) {

                if (skipNode) {
                    skipNode = false;
                    index--;
                    continue;
                }

                ParamContext paramContext = (ParamContext) listIterator.previous();

                OffsetContext offsetContext = paramContext.offset();
                if (offsetContext != null) {

                    isOffset = true;

                    if (offsetContext != null) {
                        ExprContext expr = offsetContext.expr();
                        TerminalNode numeric2 = expr.NUMERIC();
                        if (numeric2 != null) {
                            String numeric = numeric2.toString();
                            switch (index) {
                                case 0:
                                    asmLine.offset_0 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 1:
                                    asmLine.offset_1 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 2:
                                    asmLine.offset_2 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 3:
                                    asmLine.offset_3 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 4:
                                    asmLine.offset_4 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 5:
                                    asmLine.offset_5 = NumberParseUtil.parseLong(numeric);
                                    break;
                            }
                        }
                    }

                    ModifierContext modifier = offsetContext.modifier();
                    if (modifier != null) {
                        switch (index) {
                            case 0:
                                asmLine.modifier_0 = Modifier.fromString(modifier.getText());
                                break;
                            case 1:
                                asmLine.modifier_1 = Modifier.fromString(modifier.getText());
                                break;
                            case 2:
                                asmLine.modifier_2 = Modifier.fromString(modifier.getText());
                                break;
                            case 3:
                                asmLine.modifier_3 = Modifier.fromString(modifier.getText());
                                break;
                            case 4:
                                asmLine.modifier_4 = Modifier.fromString(modifier.getText());
                                break;
                            case 5:
                                asmLine.modifier_5 = Modifier.fromString(modifier.getText());
                                break;
                        }
                    }

                    ExprContext offsetExprContext = offsetContext.expr();
                    if (offsetExprContext != null) {

                        String offset = offsetExprContext.getChild(0).toString();

                        TerminalNode numericTerminalNode = offsetExprContext.NUMERIC();
                        TerminalNode hexNumericTerminalNode = offsetExprContext.HEX_NUMERIC();
                        if (numericTerminalNode != null || hexNumericTerminalNode != null) {

                            Long data = NumberParseUtil.parseLong(offset);

                            // Do I need this ?
                            isHexNumeric = true;

                            switch (index) {
                                case 0:
                                    asmLine.offset_0 = data;
                                    break;
                                case 1:
                                    asmLine.offset_1 = data;
                                    break;
                                case 2:
                                    asmLine.offset_2 = data;
                                    break;
                                case 3:
                                    asmLine.offset_3 = data;
                                    break;
                                case 4:
                                    asmLine.offset_4 = data;
                                    break;
                                case 5:
                                    asmLine.offset_5 = data;
                                    break;
                            }

                        } else {

                            switch (index) {
                                case 0:
                                    asmLine.offsetLabel_0 = offset;
                                    break;
                                case 1:
                                    asmLine.offsetLabel_1 = offset;
                                    break;
                                case 2:
                                    asmLine.offsetLabel_2 = offset;
                                    break;
                                case 3:
                                    asmLine.offsetLabel_3 = offset;
                                    break;
                                case 4:
                                    asmLine.offsetLabel_4 = offset;
                                    break;
                                case 5:
                                    asmLine.offsetLabel_5 = offset;
                                    break;
                            }

                        }
                    }
                }

                ExprContext exprContext = paramContext.expr();
                if (exprContext != null) {

                    RegisterContext registerContext = exprContext.register();
                    TerminalNode numericTerminalNode = exprContext.NUMERIC();
                    TerminalNode hexNumericTerminalNode = exprContext.HEX_NUMERIC();
                    TerminalNode stringLiteralTerminalNode = exprContext.IDENTIFIER();

                    if (registerContext != null) {

                        isRegister = true;

                        // remove the expression from the stack to consume it so that the stack remains
                        // in correct state
                        exprStack.pop();

                        switch (index) {
                            case 0:
                                asmLine.register_0 = RISCVRegister.fromString(registerContext.getText());
                                break;
                            case 1:
                                asmLine.register_1 = RISCVRegister.fromString(registerContext.getText());
                                break;
                            case 2:
                                asmLine.register_2 = RISCVRegister.fromString(registerContext.getText());
                                break;
                            case 3:
                                asmLine.register_3 = RISCVRegister.fromString(registerContext.getText());
                                break;
                            case 4:
                                asmLine.register_4 = RISCVRegister.fromString(registerContext.getText());
                                break;
                            case 5:
                                asmLine.register_5 = RISCVRegister.fromString(registerContext.getText());
                                break;
                        }

                    } else if (numericTerminalNode != null) {

                        isNumeric = true;

                        // remove the expression from the stack to consume it so that the stack remains
                        // in correct state
                        exprStack.pop();

                        String numeric = numericTerminalNode.toString();
                        if (numeric != null) {
                            switch (index) {
                                case 0:
                                    asmLine.numeric_0 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 1:
                                    asmLine.numeric_1 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 2:
                                    asmLine.numeric_2 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 3:
                                    asmLine.numeric_3 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 4:
                                    asmLine.numeric_4 = NumberParseUtil.parseLong(numeric);
                                    break;
                                case 5:
                                    asmLine.numeric_5 = NumberParseUtil.parseLong(numeric);
                                    break;
                            }
                        }

                    } else if (hexNumericTerminalNode != null) {

                        isHexNumeric = true;

                        // remove the expression from the stack to consume it so that the stack remains
                        // in correct state
                        exprStack.pop();

                        String numeric = hexNumericTerminalNode.toString();
                        long value = NumberParseUtil.parseLong(numeric);

                        if (numeric != null) {
                            switch (index) {
                                case 0:
                                    asmLine.numeric_0 = value;
                                    break;
                                case 1:
                                    asmLine.numeric_1 = value;
                                    break;
                                case 2:
                                    asmLine.numeric_2 = value;
                                    break;
                                case 3:
                                    asmLine.numeric_3 = value;
                                    break;
                                case 4:
                                    asmLine.numeric_4 = value;
                                    break;
                                case 5:
                                    asmLine.numeric_5 = value;
                                    break;
                            }
                        }

                    } else if (stringLiteralTerminalNode != null) {

                        isStringLiteral = true;

                        // remove the expression from the stack to consume it so that the stack remains
                        // in correct state
                        exprStack.pop();

                        String identifier = stringLiteralTerminalNode.getText();
                        switch (index) {
                            case 0:
                                asmLine.identifier_0 = identifier;
                                break;
                            case 1:
                                asmLine.identifier_1 = identifier;
                                break;
                            case 2:
                                asmLine.identifier_2 = identifier;
                                break;
                            case 3:
                                asmLine.identifier_3 = identifier;
                                break;
                            case 4:
                                asmLine.identifier_4 = identifier;
                                break;
                            case 5:
                                asmLine.identifier_5 = identifier;
                                break;
                        }

                    } else {

                        // last option is an expression located on the expression stack
                        ASTNode exprASTNode = exprStack.pop();

                        switch (index) {
                            case 0:
                                asmLine.expr_0 = exprASTNode;
                                break;
                            case 1:
                                asmLine.expr_1 = exprASTNode;
                                break;
                            case 2:
                                asmLine.expr_2 = exprASTNode;
                                break;
                            case 3:
                                asmLine.expr_3 = exprASTNode;
                                break;
                            case 4:
                                asmLine.expr_4 = exprASTNode;
                                break;
                            case 5:
                                asmLine.expr_5 = exprASTNode;
                                break;
                        }

                    }

                }

                if (!isOffset && !isRegister && !isNumeric && !isHexNumeric && !isStringLiteral) {

                    // DEBUG
                    // ExprContext lhs = (ExprContext) exprContext.getChild(0);
                    // String operator = exprContext.getChild(1).getText();
                    // ExprContext rhs = (ExprContext) exprContext.getChild(2);

                    switch (index) {
                        case 0:
                            // asmLine.exprContext_0 = exprContext;
                            break;
                        case 1:
                            // asmLine.exprContext_1 = exprContext;
                            break;
                        case 2:
                            // asmLine.exprContext_2 = exprContext;
                            break;
                        case 3:
                            // asmLine.exprContext_3 = exprContext;
                            break;
                        case 4:
                            // asmLine.exprContext_4 = exprContext;
                            break;
                        case 5:
                            // asmLine.exprContext_5 = exprContext;
                            break;
                    }
                }

                isOffset = false;
                isRegister = false;
                isNumeric = false;
                isHexNumeric = false;
                isStringLiteral = false;

                index--;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitLabel(RISCVASMParser.LabelContext ctx) {
        asmLine.label = ctx.getText().toString();
        asmLine.section = currentSection;
    }

    @Override
    public void exitString_assembler_instruction(RISCVASMParser.String_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.STRING;

        String val = ctx.getChild(1).toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitText_assembler_instruction(RISCVASMParser.Text_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.TEXT;
        String val = ".text";
        asmLine.stringValue = val;

        currentSection = enableTargetSection(val);

        asmLine.section = currentSection;
    }

    @Override
    public void exitFile_assembler_instruction(RISCVASMParser.File_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.FILE;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitGlobal_assembler_instruction(RISCVASMParser.Global_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.GLOBAL;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val;
    }

    @Override
    public void exitGlobl_assembler_instruction(RISCVASMParser.Globl_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.GLOBAL;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val;
    }

    @Override
    public void exitAscii_assembler_instruction(RISCVASMParser.Ascii_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.ASCII;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitAsciz_assembler_instruction(RISCVASMParser.Asciz_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.ASCIZ;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitData_assembler_instruction(RISCVASMParser.Data_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.DATA;

        String val = ".data";

        currentSection = enableTargetSection(val);

        asmLine.section = currentSection;
    }

    @Override
    public void exitSpace_assembler_instruction(RISCVASMParser.Space_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.SPACE;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitSection_definition_assembler_instruction(
            RISCVASMParser.Section_definition_assembler_instructionContext ctx) {

        asmLine.asmInstruction = AsmInstruction.SECTION;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val;

        currentSection = enableTargetSection(val);

        asmLine.section = currentSection;
    }

    /**
     * Look for the source section inside the target section and make
     * the target section current.
     * Sections (source and target) have to be defined inside the linker script!
     *
     * @param inputSectionName name of the input section to convert into a
     *                         target section! (linker script has to match!)
     */
    private Section enableTargetSection(String inputSectionName) {

        for (Map.Entry<String, Section> targetSectionEntry : sectionMap.entrySet()) {
            Section targetSection = targetSectionEntry.getValue();
            if (targetSection.inputSections.contains(inputSectionName)) {
                return targetSection;
            }
        }

        System.out.println("Section: \"" + inputSectionName + "\" is not defined! Double check the linker script!");

        return dummySection;
    }

    @Override
    public void exitByte_assembler_instruction(RISCVASMParser.Byte_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.BYTE;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitHalf_assembler_instruction(RISCVASMParser.Half_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.HALF;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitWord_assembler_instruction(RISCVASMParser.Word_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.WORD;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitDword_assembler_instruction(RISCVASMParser.Dword_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.DWORD;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    private void recurseList(Csv_numeric_listContext csv_numeric_list, List<String> list) {
        for (int i = 0; i < csv_numeric_list.getChildCount(); i++) {
            if (csv_numeric_list.getChild(i) instanceof Csv_numeric_listContext) {
                recurseList((Csv_numeric_listContext) csv_numeric_list.getChild(i), list);
            }
            if ((!csv_numeric_list.getChild(i).toString().equalsIgnoreCase(","))
                    && !(csv_numeric_list.getChild(i) instanceof Csv_numeric_listContext)) {
                list.add(csv_numeric_list.getChild(i).toString());
            }
        }
    }

    @Override
    public void exitEqu_assembler_instruction(RISCVASMParser.Equ_assembler_instructionContext ctx) {

        asmLine.asmInstruction = AsmInstruction.EQU;

        String identifier = ctx.IDENTIFIER().getText();
        asmLine.identifier_0 = identifier;

        ExprContext expr = ctx.expr();

        if (expr.NUMERIC() != null) {

            String numericAsString = expr.NUMERIC().getText();
            asmLine.numeric_1 = Long.parseLong(numericAsString);

        } else if (expr.HEX_NUMERIC() != null) {

            String numericAsString = expr.HEX_NUMERIC().getText();

            numericAsString = numericAsString.substring(2);
            asmLine.numeric_1 = Long.parseLong(numericAsString, 16);

        } else {

            throw new RuntimeException();

        }
    }

}
