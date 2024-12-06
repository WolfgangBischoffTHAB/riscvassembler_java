package com.mycompany.app;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

import riscvasm.RISCVASMParser;
import riscvasm.RISCVASMParser.Csv_numeric_listContext;
import riscvasm.RISCVASMParser.ExprContext;
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

        try {

            int index = 0;
            for (ParamContext paramContext : ctx.param()) {

                ExprContext exprContext = paramContext.expr().get(0);

                RegisterContext registerContext = exprContext.register();
                if (registerContext != null) {
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
                    String numeric = numericTerminalNode.toString();
                    if (numeric != null) {
                        switch (index) {
                            case 0:
                                asmLine.numeric_0 = Integer.parseInt(numeric);
                                break;
                            case 1:
                                asmLine.numeric_1 = Integer.parseInt(numeric);
                                break;
                            case 2:
                                asmLine.numeric_2 = Integer.parseInt(numeric);
                                break;
                        }
                    }
                }
                numericTerminalNode = exprContext.HEX_NUMERIC();
                if (numericTerminalNode != null) {
                    String numeric = numericTerminalNode.toString();
                    if (numeric.startsWith("0x")) {
                        numeric = numeric.substring("0x".length());
                    }
                    if (numeric != null) {
                        switch (index) {
                            case 0:
                                asmLine.numeric_0 = Integer.parseInt(numeric, 16);
                                break;
                            case 1:
                                asmLine.numeric_1 = Integer.parseInt(numeric, 16);
                                break;
                            case 2:
                                asmLine.numeric_2 = Integer.parseInt(numeric, 16);
                                break;
                        }
                    }
                }

                TerminalNode stringLiteralTerminalNode = exprContext.IDENTIFIER();
                if (stringLiteralTerminalNode != null) {
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

                OffsetContext offsetContext = paramContext.offset();
                if (offsetContext != null) {
                    String numeric = offsetContext.expr().NUMERIC().toString();
                    switch (index) {
                        case 0:
                            asmLine.offset_0 = Integer.parseInt(numeric);
                            break;
                        case 1:
                            asmLine.offset_1 = Integer.parseInt(numeric);
                            break;
                        case 2:
                            asmLine.offset_2 = Integer.parseInt(numeric);
                            break;
                    }
                }

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
    public void exitWord_assembler_instruction(RISCVASMParser.Word_assembler_instructionContext ctx) {
        Csv_numeric_listContext csv_numeric_list = ctx.csv_numeric_list();
        asmLine.asm_instruction = AsmInstruction.WORD;
        List<String> list = new ArrayList<>();
        recurseList(csv_numeric_list, list);
        asmLine.csvList = list;
    }

    private void recurseList(Csv_numeric_listContext csv_numeric_list, List<String> list) {
        for (int i = 0; i < csv_numeric_list.getChildCount(); i++) {
            if (csv_numeric_list.getChild(i) instanceof Csv_numeric_listContext) {
                recurseList((Csv_numeric_listContext) csv_numeric_list.getChild(i), list);
            }
            if ((!csv_numeric_list.getChild(i).toString().equalsIgnoreCase(",")) && !(csv_numeric_list.getChild(i) instanceof Csv_numeric_listContext)) {
                list.add(csv_numeric_list.getChild(i).toString());
            }
        }
    }

}
