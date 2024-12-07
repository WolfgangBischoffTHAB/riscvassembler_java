package com.mycompany.pseudo.resolve;

import java.util.List;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

public class BnezResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_BNEZ) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_BNE;
            asmLine.register_1 = Register.REG_ZERO;
            asmLine.register_2 = asmLine.register_0;
            asmLine.identifier_2 = asmLine.identifier_1;
            asmLine.identifier_1 = null;
        }
    }

}
