package com.mycompany.pseudo.resolve;

import java.util.List;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.Register;

public class CallResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines) {

        boolean done = false;
        while (!done) {

            boolean found = false;
            AsmLine foundAsmLine = null;

            int index = 0;

            // to prevent concurrent modification exception, separate search
            // from modification
            for (AsmLine asmLine : asmLines) {

                if (asmLine.mnemonic != Mnemonic.I_CALL) {
                    index++;
                    continue;
                }

                //System.out.println(asmLine);

                found = true;
                foundAsmLine = asmLine;

                break;
            }

            if (found) {

                //
                // call
                //

                asmLines.remove(foundAsmLine);

                foundAsmLine.optimized = false;

                //
                // auipc
                //

                AsmLine auipc = new AsmLine();
                asmLines.add(index, auipc);
                foundAsmLine.pseudoInstructionChildren.add(auipc);
                auipc.pseudoInstructionAsmLine = foundAsmLine;
                index++;

                auipc.mnemonic = Mnemonic.I_AUIPC;
                auipc.register_0 = Register.REG_T1;
                auipc.modifier_1 = Modifier.HI;
                auipc.offsetLabel_1 = foundAsmLine.identifier_0;

                if (foundAsmLine.label != null) {
                    auipc.label = foundAsmLine.label;
                }

                //
                // jalr
                //

                AsmLine jalr = new AsmLine();
                asmLines.add(index, jalr);
                foundAsmLine.pseudoInstructionChildren.add(jalr);
                jalr.pseudoInstructionAsmLine = foundAsmLine;
                index++;

                jalr.mnemonic = Mnemonic.I_JALR;
                jalr.register_0 = Register.REG_RA;
                jalr.register_1 = Register.REG_T1;
                jalr.modifier_2 = Modifier.LO;
                jalr.offsetLabel_2 = foundAsmLine.identifier_0;

                continue;
            }

            done = true;

        }
    }

}
