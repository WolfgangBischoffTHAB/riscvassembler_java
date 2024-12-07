package com.mycompany.app;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.Register;

import riscvasm.RISCVASMParser;
import riscvasm.RISCVASMParser.Csv_numeric_listContext;
import riscvasm.RISCVASMParser.ExprContext;
import riscvasm.RISCVASMParser.ModifierContext;
import riscvasm.RISCVASMParser.OffsetContext;
import riscvasm.RISCVASMParser.ParamContext;
import riscvasm.RISCVASMParser.RegisterContext;
import riscvasm.RISCVASMParserBaseListener;

public class ExtractingOutputListener extends RISCVASMParserBaseListener {

    public List<AsmLine> asmLines;

    private AsmLine asmLine = new AsmLine();

    @Override
    public void exitAsm_line(RISCVASMParser.Asm_lineContext ctx) {
        asmLines.add(asmLine);
        asmLine = new AsmLine();
    }

    @Override
    public void exitMnemonic(RISCVASMParser.MnemonicContext ctx) {
        asmLine.mnemonic = Mnemonic.fromString(ctx.getText());
    }

    @Override
    public void enterParams(RISCVASMParser.ParamsContext ctx) {

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
                                    asmLine.offset_0 = Long.parseLong(numeric);
                                    break;
                                case 1:
                                    asmLine.offset_1 = Long.parseLong(numeric);
                                    break;
                                case 2:
                                    asmLine.offset_2 = Long.parseLong(numeric);
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
                        String offsetLabel = offsetExprContext.getChild(0).toString();
                        switch (index) {
                            case 0:
                                asmLine.offsetLabel_0 = offsetLabel;
                                break;
                            case 1:
                                asmLine.offsetLabel_1 = offsetLabel;
                                break;
                            case 2:
                                asmLine.offsetLabel_2 = offsetLabel;
                                break;
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
                                asmLine.register_0 = Register.fromString(registerContext.getText());
                                break;
                            case 1:
                                asmLine.register_1 = Register.fromString(registerContext.getText());
                                break;
                            case 2:
                                asmLine.register_2 = Register.fromString(registerContext.getText());
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
                                    asmLine.numeric_0 = Long.parseLong(numeric);
                                    break;
                                case 1:
                                    asmLine.numeric_1 = Long.parseLong(numeric);
                                    break;
                                case 2:
                                    asmLine.numeric_2 = Long.parseLong(numeric);
                                    break;
                            }
                        }
                    }

                    numericTerminalNode = exprContext.HEX_NUMERIC();
                    if (numericTerminalNode != null) {

                        isHexNumeric = true;

                        String numeric = numericTerminalNode.toString();
                        if (numeric.startsWith("0x")) {
                            numeric = numeric.substring("0x".length());
                        }
                        if (numeric != null) {
                            switch (index) {
                                case 0:
                                    asmLine.numeric_0 = Long.parseLong(numeric, 16);
                                    break;
                                case 1:
                                    asmLine.numeric_1 = Long.parseLong(numeric, 16);
                                    break;
                                case 2:
                                    asmLine.numeric_2 = Long.parseLong(numeric, 16);
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

                    ExprContext lhs = (ExprContext) exprContext.getChild(0);
                    String operator = exprContext.getChild(1).getText();
                    ExprContext rhs = (ExprContext) exprContext.getChild(2);

                    switch (index) {
                        case 0:
                            asmLine.exprContext_0 = exprContext;
                            break;
                        case 1:
                            asmLine.exprContext_1 = exprContext;
                            break;
                        case 2:
                            asmLine.exprContext_2 = exprContext;
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
    public void exitLabel(RISCVASMParser.LabelContext ctx) {
        asmLine.label = ctx.getText().toString();
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
    public void exitAsciz_assembler_instruction(RISCVASMParser.Asciz_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.ASCIZ;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val.substring(1, val.length() - 1);
    }

    @Override
    public void exitSection_text_assembler_instruction(RISCVASMParser.Section_text_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.SECTION;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val;
    }

    @Override
    public void exitData_assembler_instruction(RISCVASMParser.Data_assembler_instructionContext ctx) {
        asmLine.asmInstruction = AsmInstruction.DATA;
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
    public void exitSection_rodata_assembler_instruction(
            RISCVASMParser.Section_rodata_assembler_instructionContext ctx) {

        asmLine.asmInstruction = AsmInstruction.SECTION;

        String val = ctx.getChild(1).getText().toString();
        asmLine.stringValue = val;
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
            asmLine.numeric_1 = Long.parseLong(numericAsString,16);
        } else {
            throw new RuntimeException();
        }
    }

}
