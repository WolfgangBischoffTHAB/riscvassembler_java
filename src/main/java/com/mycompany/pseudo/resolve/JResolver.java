package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * converts pseudo instruction j to jal
 */
public class JResolver implements AsmInstructionListModifier<RISCVRegister> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_J) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_JAL;
            asmLine.register_0 = RISCVRegister.REG_ZERO;

            // TODO: why was this used?
            //asmLine.offset_0 = null;
            
            // This is needed for e.g.:      j .L18
            asmLine.identifier_1 = asmLine.identifier_0;

            asmLine.identifier_0 = null;
        }
    }

}
