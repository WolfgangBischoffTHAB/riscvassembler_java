package com.mycompany.cpu;

import java.util.HashMap;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Register;

/**
 * Pipeline stage between Decode and Execute.
 */
public class DE_EX {

    private AsmLine<?> asmLine;

    public Map<Register, Integer> forwardingMap = new HashMap<>();

    public int instruction;

    /** for branch expression, the decode phase is able to evaluate the
     * expression. It stores the verdict here.
     */
    public boolean branchTaken;

    public boolean flush;

    public void flush() {
        System.out.println("DE_EX: flush");
        asmLine = null;
        instruction = 0;
        branchTaken = false;
        flush = true;
    }

    public AsmLine<?> getAsmLine() {
        return asmLine;
    }

    public void setAsmLine(AsmLine<?> asmLine) {
        this.asmLine = asmLine;
    }

}
