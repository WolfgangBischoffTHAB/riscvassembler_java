package com.mycompany.cpu;

import com.mycompany.data.Mnemonic;

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
