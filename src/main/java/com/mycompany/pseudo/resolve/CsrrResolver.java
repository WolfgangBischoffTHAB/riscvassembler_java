package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * pseudo instruction of CSRRS with rs1 = x0. Resolved to CSRRS rd, csr, x0
 */
public class CsrrResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {
        
        for (AsmLine<RISCVRegister> asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_CSRR) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_CSRRS;
            asmLine.register_2 = RISCVRegister.REG_X0;
        }
    }

}
