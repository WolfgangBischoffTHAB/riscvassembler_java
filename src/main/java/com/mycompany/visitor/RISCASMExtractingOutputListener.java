package com.mycompany.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

    private int paramIndex;

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
    public void exitOffset(RISCVASMParser.OffsetContext ctx) { 
        exprStack.get(0).isOffset = true;
    }

    @Override
    public void exitExpr(RISCVASMParser.ExprContext ctx) {
        
        logger.trace("exitExpr: " + ctx.getText());

        ASTNode astNode = new ASTNode();

        // case: a single node expression
        // push a node onto the stack
        if (ctx.children.size() == 1) {

            processExprContext(ctx, astNode);
            return;

        } else if (ctx.children.size() == 3) {

            ParseTree parseTree = ctx.getChild(1);

            if (parseTree instanceof TerminalNode) {

                // lw a1, dword+4

                astNode.operatorAsString = ctx.children.get(1).getText();

                ASTNode rhs = exprStack.pop();
                astNode.rhs = rhs;
                rhs.parent = astNode;

                ASTNode lhs = exprStack.pop();
                astNode.lhs = lhs;
                lhs.parent = astNode;

                exprStack.push(astNode);

            } else if (parseTree instanceof ExprContext) {

                // Assumption: This case is only hit for lines where the offset is omitted
                // before the offset bracket
                // e.g. vle64.v v0, (a0)

                // take down the register which is the target of the offset
                ASTNode rhs = exprStack.pop();
                ASTNode lhs = new ASTNode();
                lhs.isNumeric = true;
                lhs.numeric = 0;

                astNode.rhs = rhs;
                rhs.parent = astNode;
                astNode.lhs = lhs;
                lhs.parent = astNode;

                exprStack.push(astNode);

            }

            // RISCVASMParser.ExprContext exprCtx = (ExprContext) ctx.children.get(1);
            // processRegisterContext(exprCtx, astNode);
            // ASTNode rhs = exprStack.pop();
            // ASTNode rhs = exprStack.pop();
            // ASTNode lhs = exprStack.pop();
            // astNode.operatorAsString = ctx.children.get(1).getText();
            // astNode.rhs = rhs;
            // rhs.parent = astNode;
            // astNode.lhs = lhs;
            // lhs.parent = astNode;
            // exprStack.push(astNode);

            return;

        } else if (ctx.children.size() == 4) {

            // Assumption: This only happens if there is a modifier
            // e.g. lui a3, %hi(result)

            ASTNode rhs = exprStack.pop();

            ASTNode lhs = new ASTNode();
            lhs.isModifier = true;
            lhs.modifier = ctx.getChild(0).getText();

            astNode.rhs = rhs;
            rhs.parent = astNode;
            astNode.lhs = lhs;
            lhs.parent = astNode;

            exprStack.push(astNode);

            return;

        }

        throw new RuntimeException("Not implemented yet!");
    }

    private void processExprContext(RISCVASMParser.ExprContext ctx, ASTNode astNode) {

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

    @Override
    public void enterParams(RISCVASMParser.ParamsContext ctx) {
        // System.out.println(ctx.getText());
    }

    @Override
    public void exitParams(RISCVASMParser.ParamsContext ctx) {
        paramIndex = 0;
        // System.out.println(asmLine);
        // System.out.println(asmLine);
    }

    /**
     * For testing, parse src\test\resources\riscvasm\examples\scratchpad_2.s
     * <pre>
     * vle64.v      v0, 0(a0)
     * vle64.v      v0, (a0)
     * vsetvli      t0, x0, e8,m8,tu,mu
     * add          x1, x2, x3
     * lui          a3, %hi(result)
     * addi         a3, a3, %lo(result)
     * lui          a5, %hi(uart)
     * lw           a5, %lo(uart)(a5)
     * lui          a4, %hi(LSR.2)
     * lbu          a4, %lo(LSR.2)(a4)
     * lui          a5, %hi(LSR_RI.1)
     * lbu          a5, %lo(LSR_RI.1)(a5)
     * addi         a0, a0, %lo(.L.str)
     * auipc        gp, %pcrel_hi(__global_pointer$)
     * lw           t5, -4(t0)
     * li           t1, 0xFF00F007
     * li           a5, -2130706432
     * lw           a1, dword+4
     * li           a3, 0x00FF
     * call         putchar
     * jr           ra
     * j            .LBB1_2
     * nop
     * </pre>
     */
    @Override 
    public void exitParam(RISCVASMParser.ParamContext ctx) {

        logger.trace("--------------------------------------");
        logger.trace(paramIndex + ") " + ctx.getText());

        // OffsetContext offsetContext = ctx.offset();
        // if (offsetContext != null) {
        //     System.out.println("has offset");

        //     ExprContext offsetExprContext = offsetContext.expr();
        //     if (offsetExprContext != null) {
        //         System.out.println("offset has expr '" + offsetExprContext.getText() + "'");

        //         // addExpression(paramIndex);
        //         //addOffset(offsetExprContext, paramIndex);
        //     }
        // }

        ExprContext exprContext = ctx.expr();
        if (exprContext != null) {
            // System.out.println("has expr " + exprContext.getText());

            // ModifierContext modifierContext = exprContext.modifier();
            // if (modifierContext != null) {
            //     System.out.println("offset has modifier '" + modifierContext.getText() + "'");
            // }

            // RegisterContext registerContext = exprContext.register();
            // if (registerContext != null) {
            //     System.out.println("expr has register '" + registerContext.getText() + "'");
            //     //addRegister(registerContext.getText(), paramIndex);
            // }

            while (!exprStack.isEmpty()) {
            
                ASTNode exprASTNode = exprStack.pop();

                // System.out.println(exprASTNode);

                if (exprASTNode.isOffset) {
                    addOffsetExpression(exprASTNode, paramIndex);
                } else if ((exprASTNode.lhs == null) && (exprASTNode.rhs == null)) {
                    if (exprASTNode.isRegister) {
                        addRegister(exprASTNode.register.toString(), paramIndex);
                    } else if (exprASTNode.isNumeric) {
                        addNumeric(exprASTNode.numeric, paramIndex);
                    } else if (exprASTNode.isHexNumeric) {
                        addHexNumeric(exprASTNode.hexNumeric, paramIndex);
                    } else if (exprASTNode.isStringLiteral) {
                        addIdentifier(exprASTNode.identifier, paramIndex);
                    }
                } else if ((exprASTNode.lhs != null) && (exprASTNode.rhs != null)) {

                    ASTNode exprASTNodeRhs = exprASTNode.rhs;
                    ASTNode exprASTNodeLhs = exprASTNode.lhs;

                    if (exprASTNode.operatorAsString != null) {
                        addExpression(exprASTNode, paramIndex);
                    } else if (exprASTNodeRhs.isRegister && exprASTNodeLhs.isNumeric) {
                        addRegister(exprASTNodeRhs.register.toString(), paramIndex);
                        addOffset(exprASTNodeLhs.numeric, paramIndex);
                    } else if (exprASTNodeRhs.isRegister && exprASTNodeLhs.isModifier) {
                        addRegister(exprASTNodeRhs.register.toString(), paramIndex);
                        addModifier(exprASTNodeLhs.modifier, paramIndex);
                    } else if (exprASTNodeRhs.isStringLiteral && exprASTNodeLhs.isModifier) {
                        addIdentifier(exprASTNodeRhs.identifier, paramIndex);
                        addModifier(exprASTNodeLhs.modifier, paramIndex);
                    }
                }
            }
        }

        paramIndex++;
    }

    private void addNumeric(long value, int index) {
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

    private void addHexNumeric(long value, int index) {
        // long value = NumberParseUtil.parseLong(data);
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

    private void addModifier(String data, int index) {
        Modifier modifier = Modifier.fromString(data);
        switch (index) {
            case 0:
                asmLine.modifier_0 = modifier;
                break;
            case 1:
                asmLine.modifier_1 = modifier;
                break;
            case 2:
                asmLine.modifier_2 = modifier;
                break;
            case 3:
                asmLine.modifier_3 = modifier;
                break;
            case 4:
                asmLine.modifier_4 = modifier;
                break;
            case 5:
                asmLine.modifier_5 = modifier;
                break;
            default:
                throw new RuntimeException("Unknown value!");
        }
    }

    private void addIdentifier(String identifier, int index) {
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
    }

    private void addOffset(long data, int index) {
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
            default:
                throw new RuntimeException("Unknown value!");
        }
    }

    private void addOffsetExpression(ASTNode exprASTNode, int index) {
        switch (index) {
            case 0:
                asmLine.offset_expr_0 = exprASTNode;
                break;
            case 1:
                asmLine.offset_expr_1 = exprASTNode;
                break;
            case 2:
                asmLine.offset_expr_2 = exprASTNode;
                break;
            case 3:
                asmLine.offset_expr_3 = exprASTNode;
                break;
            case 4:
                asmLine.offset_expr_4 = exprASTNode;
                break;
            case 5:
                asmLine.offset_expr_5 = exprASTNode;
                break;
            default:
                throw new RuntimeException("Unknown value!");
        }
    }

    private void addOffset(ExprContext offsetExprContext, int index) {

        String offset = offsetExprContext.getChild(0).toString();

        TerminalNode numericTerminalNode = offsetExprContext.NUMERIC();
        TerminalNode hexNumericTerminalNode = offsetExprContext.HEX_NUMERIC();
        if (numericTerminalNode != null || hexNumericTerminalNode != null) {

            Long data = NumberParseUtil.parseLong(offset);

            // // Do I need this ?
            // boolean isHexNumeric = true;

            addOffset(data, index);

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
                default:
                    throw new RuntimeException("Unknown value!");
            }
        }
    }

    private void addExpression(ASTNode exprASTNode, int index) {
        // last option is an expression located on the expression stack
        // ASTNode exprASTNode = exprStack.pop();
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

    private void addRegister(String data, int index) {
        // // remove the expression from the stack to consume it so that the stack remains
        // // in correct state
        // exprStack.pop();
        switch (index) {
            case 0:
                asmLine.register_0 = RISCVRegister.fromString(data);
                break;
            case 1:
                asmLine.register_1 = RISCVRegister.fromString(data);
                break;
            case 2:
                asmLine.register_2 = RISCVRegister.fromString(data);
                break;
            case 3:
                asmLine.register_3 = RISCVRegister.fromString(data);
                break;
            case 4:
                asmLine.register_4 = RISCVRegister.fromString(data);
                break;
            case 5:
                asmLine.register_5 = RISCVRegister.fromString(data);
                break;
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
    public void exitZero_assembler_instruction(RISCVASMParser.Zero_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.ZERO;
        ExprContext expr = ctx.expr();
        asmLine.numeric_0 = convertExpressionToLongValue(expr);
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

    @Override
    public void exitQuad_assembler_instruction(RISCVASMParser.Quad_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.QUAD;

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

        asmLine.numeric_1 = convertExpressionToLongValue(expr);
    }

    private long convertExpressionToLongValue(ExprContext expr) {
        if (expr.NUMERIC() != null) {
            String numericAsString = expr.NUMERIC().getText();
            return Long.parseLong(numericAsString);
        } else if (expr.HEX_NUMERIC() != null) {
            String numericAsString = expr.HEX_NUMERIC().getText();
            numericAsString = numericAsString.substring(2);
            return Long.parseLong(numericAsString, 16);
        } else {
            throw new RuntimeException();
        }
    }

}
