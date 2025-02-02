package com.mycompany.cpu;

import com.mycompany.data.AsmLine;

public class IF_DE {

    public int instruction;

    public AsmLine asmLine;

    public void flush() {
        System.out.println("IF_DE: flush");
        instruction = 0;
        asmLine = null;
    }

}
