package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * ret -> jalr zero, ra, 0x0
 */
public class RetResolver implements AsmInstructionListModifier {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void modify(List<AsmLine<?>> asmLines, Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_RET) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_JALR;

            asmLine.register_0 = RISCVRegister.REG_ZERO;
            asmLine.register_1 = RISCVRegister.REG_RA;
            asmLine.register_2 = null;

            asmLine.numeric_0 = 0L;
            asmLine.numeric_1 = 0L;
            asmLine.numeric_2 = 0L;
        }
    }

}
