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

/**
 * Will turn the pseudo instruction 'call' into real assembly instructions.
 * Note: Currently outputs a far call (auipc, jalr) always even if it might be a near call.
 * A CallOptimizer class can be activated which replaces jumps by near jumps if possible.
 * The CallOptimizer is activated using the constant RiscVAssembler.USE_CALL_OPTIMIZER.
 *
 * Because every RISC-V instruction is 32 bit the full address range cannot be encoded
 * into an instruction since a address is 32 bit large and there is no space left for
 * funct3 and funct7.
 *
 * So function calls for ±1MiB relative to the PC (near calls) can be encoded into
 * a single jal instruction whereas far calls outside that range (far calls) have to be split
 * into two instructions (auipc, jalr).
 *
 * In order to leviate the user from these details, assemblers contain the 'call'
 * pseudo instruction. The assembler will tell apart near from far calls and will
 * generate either jal or auipc+jalr.
 *
 * Near calls and far calls
 * In the above example we used jal to call our function, but it’s limited to ±1MiB relative to the PC.
 * For far calls we can combine jalr with auipc to reach anywhere in 32-bit memory space.
 * Use the call pseudoinstruction and the assembler will choose the correct instruction(s) for you.
 */
public class CallResolver implements AsmInstructionListModifier {

    @SuppressWarnings({ "rawtypes", "unchecked" })
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

            // Java specifics: If a collection is modified while being iterated over, there will be an exception
            // To prevent concurrent modification exception, separate search from modification by storing
            // a AsmLine containing a call statement to a local variable
            for (AsmLine<?> asmLine : asmLines) {

                if (asmLine.mnemonic != Mnemonic.I_CALL) {
                    index++;
                    continue;
                }

                found = true;
                foundAsmLine = asmLine;

                break;
            }

            //
            // Phase 2 - replace call instruction by far call (auipc+jalr)
            //

            if (found) {

                //
                // process special functions like printf, puts
                //

                String offsetLabel = foundAsmLine.identifier_1;
                if (offsetLabel.equalsIgnoreCase("printf")) {
                    throw new RuntimeException("Implement library function printf!");
                } else if (offsetLabel.equalsIgnoreCase("puts")) {
                    //throw new RuntimeException("Implement library function puts!");
                    foundAsmLine.mnemonic = Mnemonic.I_PUTS;
                    foundAsmLine.identifier_1 = null;
                    foundAsmLine.optimized = true;
                    return;
                }

                //
                // call
                //

                // remove the call line and add it's children instead.
                // The pseudo line CALL is the parent of two real lines
                // being auipc and jalr
                asmLines.remove(foundAsmLine);

                // if the call pseudo instruction should be optimized,
                // set optized to falso so that the CALL-Optimizer kicks in.
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
                //auipc.offsetLabel_1 = foundAsmLine.identifier_0;
                auipc.offsetLabel_1 = foundAsmLine.identifier_1;

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
