package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

/**
 * BGT is a pseudo instruction that does not exist in the RISC V instruction
 * set!
 * Implement BGT by BLT
 *
 * BGT a > b
 * BLT b < a
 *
 * BLE is not the correct instruction to use! BLT is correct including swapping
 * the parameters
 */
public class BgtResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_BGT) {
                continue;
            }

            // replace BGT by BLT
            asmLine.mnemonic = Mnemonic.I_BLT;

            // swap registers
            Register temp = asmLine.register_0;
            asmLine.register_0 = asmLine.register_1;
            asmLine.register_1 = temp;
        }
    }

}
