package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

public class BgezResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_BGEZ) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_BGE;
            asmLine.register_0 = Register.REG_ZERO;
            asmLine.register_1 = asmLine.register_0;
        }
    }
    
}
