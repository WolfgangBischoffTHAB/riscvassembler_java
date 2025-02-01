package com.mycompany.cpu;

import com.mycompany.data.Mnemonic;

/**
 * The interlock is responsible for stalling the pipeline in situations where
 * the pipeline cannot prevent stalls via forwarding. The pipeline will stall
 * the earlier stages until the later stage has finally produced the data.
 * An example is a load from memory directly followd by a register operation
 * using the loaded data. The register operation requires the data in a phase
 * which the pipeline will arrive at even before the load has produced the data.
 * There is no way out of this without waiting for the data. Waiting is called
 * stalling.
 */
public class Interlock {

    private boolean stall;

    public boolean isStall() {
        return stall;
    }

    public void checkForStall(IF_DE if_de, DE_EX de_ex, EX_MEM ex_mem, MEM_WB mem_wb) {

        stall = false;

        if ((de_ex.asmLine != null) && (ex_mem.asmLine != null)) {

            if (ex_mem.asmLine.mnemonic == Mnemonic.I_LB) {
                if (de_ex.asmLine.register_1 == ex_mem.asmLine.register_0) {
                    System.out.println("STALL DETECTED between: " + ex_mem.asmLine + " and followup " + de_ex.asmLine);
                    stall = true;
                }
                if (de_ex.asmLine.register_2 == ex_mem.asmLine.register_0) {
                    System.out.println("STALL DETECTED between: " + ex_mem.asmLine + " and followup " + de_ex.asmLine);
                    stall = true;
                }
            }
        }

    }

}
