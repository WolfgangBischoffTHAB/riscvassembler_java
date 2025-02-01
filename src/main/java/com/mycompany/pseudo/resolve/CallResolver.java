package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.assembler.RiscVAssembler;
import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

public class CallResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine<?>> asmLines, final Map<String, Section> sectionMap) {

        // Loop over the entire set of asmLines to replace all call instructions
        boolean done = false;
        while (!done) {

            boolean found = false;
            AsmLine foundAsmLine = null;

            int index = 0;

            //
            // Phase 1 - search first call instruction
            //

            // to prevent concurrent modification exception, separate search
            // from modification
            for (AsmLine asmLine : asmLines) {

                if (asmLine.mnemonic != Mnemonic.I_CALL) {
                    index++;
                    continue;
                }

                found = true;
                foundAsmLine = asmLine;

                break;
            }

            //
            // Phase 2 - replace call instruction
            //

            if (found) {

                //
                // call
                //

                asmLines.remove(foundAsmLine);

                foundAsmLine.optimized = true;
                if (RiscVAssembler.USE_CALL_OPTIMIZER) {
                    foundAsmLine.optimized = false;
                }

                //
                // auipc
                //

                AsmLine auipc = new AsmLine();
                asmLines.add(index, auipc);
                foundAsmLine.pseudoInstructionChildren.add(auipc);
                auipc.pseudoInstructionAsmLine = foundAsmLine;
                auipc.section = foundAsmLine.section;
                index++;

                auipc.mnemonic = Mnemonic.I_AUIPC;
                auipc.register_0 = RISCVRegister.REG_RA;
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
                jalr.section = foundAsmLine.section;
                index++;

                jalr.mnemonic = Mnemonic.I_JALR;
                jalr.register_0 = RISCVRegister.REG_RA;
                jalr.register_1 = RISCVRegister.REG_RA;
                jalr.modifier_2 = Modifier.LO;
                jalr.offsetLabel_2 = foundAsmLine.identifier_0;

                System.out.println(jalr);

                continue;
            }

            done = true;

        }
    }

}
