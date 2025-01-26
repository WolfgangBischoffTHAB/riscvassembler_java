package com.mycompany.cpu;

import com.mycompany.data.AsmLine;

public class DE_EX {

    public AsmLine asm_line;

    // result value from IE is stored here for forwarding to prevent pipeline stalls
    public int rd_value;

    public int instruction;

    public boolean forwarded;

}
