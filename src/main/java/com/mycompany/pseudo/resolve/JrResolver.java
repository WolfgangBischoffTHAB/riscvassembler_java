package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * jr      ra    is resolved to     jalr x0, 0(x1)
 */
public class JrResolver  implements AsmInstructionListModifier {

    @SuppressWarnings("rawtypes")
    @Override
    public void modify(List<AsmLine<?>> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_JR) {
                continue;
            }

            System.out.println(asmLine);

            asmLine.mnemonic = Mnemonic.I_JALR;
            asmLine.register_1 = asmLine.register_1;
            asmLine.register_0 = RISCVRegister.REG_ZERO;
            asmLine.numeric_2 = 0L;
        }
    }

}
