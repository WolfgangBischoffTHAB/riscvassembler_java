package com.mycompany.pseudo.resolve;

import java.util.List;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

public class MvResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_MV) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_ADDI;
            asmLine.register_2 = Register.REG_UNKNOWN;
            asmLine.numeric_2 = 0L;
        }
    }

}
