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
 * Note: Currently outputs a far call (auipc, jalr) always even if it might be a
 * near call. A CallOptimizer class can be activated which replaces jumps by
 * near jumps if possible. The CallOptimizer is activated using the constant
 * RiscVAssembler.USE_CALL_OPTIMIZER.
 *
 * Because every RISC-V instruction is 32 bit the full address range cannot be
 * encoded into an instruction since a address is 32 bit large and there is no
 * space left for funct3 and funct7.
 *
 * So function calls for ±1MiB relative to the PC (near calls) can be encoded
 * into a single jal instruction whereas far calls outside that range (far
 * calls) have to be split into two instructions (auipc, jalr).
 *
 * In order to leviate the user from these details, assemblers contain the
 * 'call' pseudo instruction. The assembler will tell apart near from far calls
 * and will generate either jal (for NEAR, ±1MiB relative to the PC) or
 * auipc+jalr (for FAR).
 *
 * Near calls and far calls in the above example we used jal to call our
 * function, but it's limited to ±1MiB relative to the PC. For far calls we can
 * combine jalr with auipc to reach anywhere in 32-bit memory space. Use the
 * call pseudoinstruction and the assembler will choose the correct
 * instruction(s) for you.
 */
public class CallResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        // Loop over the entire set of asmLines to replace all call instructions
        boolean done = false;
        while (!done) {

            boolean found = false;
            AsmLine<RISCVRegister> foundAsmLine = null;

            int index = 0;

            //
            // Phase 1 - search first call instruction
            //

            // Java specifics: If a collection is modified while being iterated over, there
            // will be an exception To prevent concurrent modification exception, separate
            // search from modification by storing a AsmLine containing a call statement to
            // a local variable
            for (AsmLine<RISCVRegister> asmLine : asmLines) {

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
                // call
                //

                // remove the call line and add it's children instead.
                // The pseudo line CALL is the parent of two real lines
                // being auipc and jalr
                asmLines.remove(foundAsmLine);

                // if the call pseudo instruction should be optimized,
                // set optimized to falso so that the CALL-Optimizer kicks in.
                foundAsmLine.optimized = true;
                if (RiscVAssembler.USE_CALL_OPTIMIZER) {
                    foundAsmLine.optimized = false;
                }

                //
                // auipc
                //
                // auipc rd, imm    # rd = pc + imm << 12
                //

                AsmLine<RISCVRegister> auipc = new AsmLine<>();
                asmLines.add(index, auipc);
                foundAsmLine.pseudoInstructionChildren.add(auipc);
                auipc.pseudoInstructionAsmLine = foundAsmLine;
                auipc.section = foundAsmLine.section;
                index++;

                auipc.mnemonic = Mnemonic.I_AUIPC;
                auipc.register_0 = RISCVRegister.REG_RA;
                // auipc.modifier_1 = Modifier.HI;
                auipc.modifier_1 = Modifier.PCREL_HI;

                // I do not know how to tell weather to use identifier 0 or 1
                if (foundAsmLine.identifier_0 != null) {
                    auipc.offsetLabel_1 = foundAsmLine.identifier_0;
                }
                if (foundAsmLine.identifier_1 != null) {
                    auipc.offsetLabel_1 = foundAsmLine.identifier_1;
                }

                // DEBUG
                if (auipc.offsetLabel_1 == null) {
                    throw new RuntimeException();
                }

                if (foundAsmLine.label != null) {
                    auipc.label = foundAsmLine.label;
                }

                // DEBUG
                // System.out.println(auipc);

                // jal use immediate (20bits) encoding for destination address
                // and can jump +-1MiB range. And save the actual address + 4 in
                // register rd. (x1 in your example).

                // jalr use indirect address (x1 in your example) plus a constant
                // of 12bits (0 in your example). It save the actual address + 4 in
                // register rd too. In your example you set x0 as return address
                // register because you «don't care».

                //
                // jalr
                //

                AsmLine<RISCVRegister> jalr = new AsmLine<>();
                asmLines.add(index, jalr);
                foundAsmLine.pseudoInstructionChildren.add(jalr);
                jalr.pseudoInstructionAsmLine = foundAsmLine;
                jalr.section = foundAsmLine.section;
                index++;

                // jal  rd, imm       # rd = pc+4; pc += imm
                // jalr rd, rs1, imm  # rd = pc+4; pc = rs1 + imm
                jalr.mnemonic = Mnemonic.I_JALR;
                jalr.register_0 = RISCVRegister.REG_RA; // ra = pc + 4
                jalr.register_1 = RISCVRegister.REG_RA; // RA is used here since the preceding auipc command has prepared RA with the upper 12 bit
                // jalr.modifier_2 = Modifier.LO;
                jalr.modifier_2 = Modifier.PCREL_LO;

                // I do not know how to tell weather to use identifier 0 or 1
                if (foundAsmLine.identifier_0 != null) {
                    jalr.offsetLabel_2 = foundAsmLine.identifier_0;
                }
                if (foundAsmLine.identifier_1 != null) {
                    jalr.offsetLabel_2 = foundAsmLine.identifier_1;
                }

                // DEBUG
                if (jalr.offsetLabel_2 == null) {
                    throw new RuntimeException();
                }

                // DEBUG
                // System.out.println(jalr);

                continue;
            }

            done = true;

        }
    }

}
