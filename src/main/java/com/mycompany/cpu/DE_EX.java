package com.mycompany.cpu;

import java.util.HashMap;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Register;

public class DE_EX {

    public AsmLine asmLine;

    // result value from IE is stored here for forwarding to prevent pipeline stalls
    // public int forwarded_rd_value;
    // public boolean forwarded;

    public Map<Register, Integer> forwardingMap = new HashMap<>();

    public int instruction;

}
