package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * https://github.com/riscv-non-isa/riscv-asm-manual
 * 
 * Branch if ≤, unsigned
 * 
 * bleu rs, rt, offset --> bgeu rt, rs, offset
 */
public class BleuResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, Map<String, Section> sectionMap) {
        
        for (AsmLine<RISCVRegister> asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_BLEU) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_BGEU;
            RISCVRegister temp = asmLine.register_0;
            asmLine.register_0 = asmLine.register_1;
            asmLine.register_1 = temp;
        }
    }

}

