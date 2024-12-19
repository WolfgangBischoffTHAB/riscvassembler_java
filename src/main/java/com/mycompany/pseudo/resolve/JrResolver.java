package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

public class JrResolver  implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_JR) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_JALR;
            asmLine.register_1 = asmLine.register_0;
            asmLine.register_0 = Register.REG_ZERO;
            asmLine.numeric_2 = 0L;
        }
    }

}
