package com.mycompany.app;

import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

import riscvasm.RISCVASMParser;
import riscvasm.RISCVASMParser.ExprContext;
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

        int index = 0;
        for (ParamContext paramContext : ctx.param()) {

            ExprContext exprContext = paramContext.expr().get(0);
            // System.out.println(exprContext);

            RegisterContext registerContext = exprContext.register();
            if (registerContext != null) {
                // System.out.println(registerContext.getText());
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
                    // System.out.println(registerContext.getText());
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

            index++;
        }
    }

}
