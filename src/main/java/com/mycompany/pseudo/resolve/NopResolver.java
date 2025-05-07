package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

public class NopResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine<?>> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_NOP) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_ADDI;
            asmLine.register_0 = RISCVRegister.REG_ZERO;
            asmLine.register_1 = RISCVRegister.REG_ZERO;
            // asmLine.register_2 = RISCVRegister.REG_UNKNOWN;
            asmLine.register_2 = null;
            asmLine.numeric_2 = 0L;
        }
    }

}
