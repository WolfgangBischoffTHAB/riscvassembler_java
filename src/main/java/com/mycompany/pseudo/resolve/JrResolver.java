package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * jr rs0    is resolved to     jalr x0, 0(rs0)
 */
public class JrResolver  implements AsmInstructionListModifier<RISCVRegister> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_JR) {
                continue;
            }

            // DEBUG - before
            // System.out.println(asmLine);

            asmLine.mnemonic = Mnemonic.I_JALR;

            asmLine.offset_1 = 0L;

            // 1. copy register_0 into regster_1
            asmLine.register_1 = asmLine.register_0;

            // 2. override register_0
            asmLine.register_0 = RISCVRegister.REG_ZERO;

            // DEBUG - after
            // System.out.println(asmLine);
        }
    }

}
