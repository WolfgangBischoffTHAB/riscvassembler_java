package com.mycompany.cpu;

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
        if (register_index == 0) {
            return;
        }

        // set the value
        registerFile[register_index] = value;
    }
    
}
