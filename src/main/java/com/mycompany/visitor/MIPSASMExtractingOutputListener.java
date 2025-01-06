package com.mycompany.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.MIPSRegister;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

import mipsasm.MIPSParser;
import mipsasm.MIPSParser.Csv_numeric_listContext;
import mipsasm.MIPSParser.ExprContext;
import mipsasm.MIPSParser.ModifierContext;
import mipsasm.MIPSParser.OffsetContext;
import mipsasm.MIPSParser.ParamContext;
import mipsasm.MIPSParser.RegisterContext;
import mipsasm.MIPSParserBaseListener;

public class MIPSASMExtractingOutputListener extends MIPSParserBaseListener {

    public Map<String, Section> sectionMap;

    public Section currentSection;

    public List<AsmLine<?>> asmLines;

    private AsmLine asmLine = new AsmLine();

    private int sourceLine = 1;

    public Section dummySection;

    @Override
    public void exitAsm_line(MIPSParser.Asm_lineContext ctx) {

        asmLine.sourceLine = sourceLine;
        asmLine.section = currentSection;
        asmLines.add(asmLine);

        asmLine = new AsmLine();
        sourceLine++;
    }

    @Override
    public void exitMnemonic(MIPSParser.MnemonicContext ctx) {
        asmLine.mnemonic = Mnemonic.fromString(ctx.getText());
    }

    @Override
    public void enterParams(MIPSParser.ParamsContext ctx) {

        boolean isOffset = false;
        boolean isRegister = false;
        boolean isNumeric = false;
        boolean isHexNumeric = false;
        boolean isStringLiteral = false;

        try {

            int index = 0;
            for (ParamContext paramContext : ctx.param()) {

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
                            }

                        }
                    }
                }

                ExprContext exprContext = paramContext.expr();
                if (exprContext != null) {

                    RegisterContext registerContext = exprContext.register();
                    if (registerContext != null) {

                        isRegister = true;

                        switch (index) {
                            case 0:
                                asmLine.register_0 = MIPSRegister.fromString(registerContext.getText());
                                break;
                            case 1:
                                asmLine.register_1 = MIPSRegister.fromString(registerContext.getText());
                                break;
                            case 2:
                                asmLine.register_2 = MIPSRegister.fromString(registerContext.getText());
                                break;
                        }
                    }

                    TerminalNode numericTerminalNode = exprContext.NUMERIC();
                    if (numericTerminalNode != null) {

                        isNumeric = true;

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
                            }
                        }
                    }

                    numericTerminalNode = exprContext.HEX_NUMERIC();
                    if (numericTerminalNode != null) {

                        isHexNumeric = true;

                        String numeric = numericTerminalNode.toString();
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
                            }
                        }
                    }

                    TerminalNode stringLiteralTerminalNode = exprContext.IDENTIFIER();
                    if (stringLiteralTerminalNode != null) {

                        isStringLiteral = true;

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
                            //asmLine.exprContext_0 = exprContext;
                            break;
                        case 1:
                            //asmLine.exprContext_1 = exprContext;
                            break;
                        case 2:
                            //asmLine.exprContext_2 = exprContext;
                            break;
                    }
                }

                isOffset = false;
                isRegister = false;
                isNumeric = false;
                isHexNumeric = false;
                isStringLiteral = false;

                index++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitLabel(MIPSParser.LabelContext ctx) {
        asmLine.label = ctx.getText().toString();
        asmLine.section = currentSection;
    }

    @Override
    public void exitString_assembler_instruction(MIPSParser.String_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.STRING;

        String val = ctx.getChild(1).toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitText_assembler_instruction(MIPSParser.Text_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.TEXT;
        String val = ".text";
        asmLine.stringValue = val;

        currentSection = enableTargetSection(val);

        asmLine.section = currentSection;
    }

    @Override
    public void exitFile_assembler_instruction(MIPSParser.File_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.FILE;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitGlobal_assembler_instruction(MIPSParser.Global_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.GLOBAL;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val;
    }

    @Override
    public void exitGlobl_assembler_instruction(MIPSParser.Globl_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.GLOBAL;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val;
    }

    @Override
    public void exitAscii_assembler_instruction(MIPSParser.Ascii_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.ASCII;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitAsciz_assembler_instruction(MIPSParser.Asciz_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.ASCIZ;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitData_assembler_instruction(MIPSParser.Data_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.DATA;

        String val = ".data";

        currentSection = enableTargetSection(val);

        asmLine.section = currentSection;
    }

    @Override
    public void exitSpace_assembler_instruction(MIPSParser.Space_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.SPACE;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitSection_definition_assembler_instruction(
            MIPSParser.Section_definition_assembler_instructionContext ctx) {

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
     * target section! (linker script has to match!)
     */
    private Section enableTargetSection(String inputSectionName) {

        for (Map.Entry<String, Section> targetSectionEntry : sectionMap.entrySet()) {
            Section targetSection = targetSectionEntry.getValue();
            if (targetSection.inputSections.contains(inputSectionName)) {
                return targetSection;
            }
        }

        //throw new RuntimeException("Section: \"" + inputSectionName + "\" is not defined! Double check the linker script!");
        System.out.println("Section: \"" + inputSectionName + "\" is not defined! Double check the linker script!");

        return dummySection;
    }

    @Override
    public void exitByte_assembler_instruction(MIPSParser.Byte_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.BYTE;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitHalf_assembler_instruction(MIPSParser.Half_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.HALF;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitWord_assembler_instruction(MIPSParser.Word_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.WORD;

        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    @Override
    public void exitDword_assembler_instruction(MIPSParser.Dword_assembler_instructionContext ctx) {
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
    public void exitEqu_assembler_instruction(MIPSParser.Equ_assembler_instructionContext ctx) {

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
