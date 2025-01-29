package com.mycompany.cpu;

import java.util.HashMap;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Register;

public class MEM_WB {

    public int value;

    public AsmLine asmLine;

    public Map<Register, Integer> forwardingMap = new HashMap<>();

    public int memoryAddress;

    public int rd_value;

}
