package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * SEQZ - Set Equal Zero
 * SEQZ rd, rs
 * Sets a 1 into rd if rs1 is zero
 * 
 * SEQZ is implemented using: SLTIU rd, rs1, 1
 * The only unsigned number that is less than 1 is zero, 
 * so this sets a 1 into rd only if rs1 is zero
 */
public class SeqzResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, Map<String, Section> sectionMap) {

        for (AsmLine<?> asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_SEQZ) {
                continue;
            }

            //System.out.println(asmLine.toString());

            asmLine.mnemonic = Mnemonic.I_SLTIU;
            asmLine.numeric_2 = 1L;

            //System.out.println(asmLine.toString());
        }
    }
    
}
