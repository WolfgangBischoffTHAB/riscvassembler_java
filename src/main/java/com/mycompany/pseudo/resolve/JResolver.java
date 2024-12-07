package com.mycompany.pseudo.resolve;

import java.util.List;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

public class JResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_J) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_JAL;
            asmLine.register_0 = Register.REG_ZERO;

            asmLine.offset_0 = null;
            asmLine.identifier_1 = asmLine.identifier_0;

            asmLine.identifier_0 = null;

            System.out.println(asmLine);

        }
    }

}
