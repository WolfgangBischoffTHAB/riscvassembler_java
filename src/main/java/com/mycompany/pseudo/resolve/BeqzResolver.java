package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

public class BeqzResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine<RISCVRegister> asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_BEQZ) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_BEQ;
            asmLine.register_1 = RISCVRegister.REG_ZERO;
            asmLine.register_2 = asmLine.register_0;
            asmLine.identifier_2 = asmLine.identifier_1;
            asmLine.identifier_1 = null;
        }
    }

}
