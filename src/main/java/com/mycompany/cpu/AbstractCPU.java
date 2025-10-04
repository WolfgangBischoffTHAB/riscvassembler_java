package com.mycompany.cpu;

import com.mycompany.data.RISCVRegister;

public abstract class AbstractCPU implements CPU {

    public int[] registerFile = new int[32];

    @Override
    public abstract boolean step();
    
    public int readRegisterFile(int index) {

        // register zero is hardcoded zero
        if (index == 0) {
            return 0;
        }

        // set the value
        return registerFile[index];
    }

    public void writeRegisterFile(final int register_index, final int value) {

        // write to zero register has no effect
        if (register_index == RISCVRegister.REG_ZERO.getIndex()) {
            return;
        }

        // // DEBUG monitor the stack pointer
        // if (register_index == RISCVRegister.REG_SP.getIndex()) {
        //     if (value < 0x00) {
        //         System.out.println("stack pointer negative!");
        //     }
        // }

        // // DEBUG monitor the frame pointer
        // if (register_index == RISCVRegister.REG_FP.getIndex()) {
        //     if (value < 0x00) {
        //         System.out.println("frame pointer negative!");
        //     }
        // }

        // set the value
        registerFile[register_index] = value;
    }
    
}
