package com.mycompany.cpu;

import java.util.HashMap;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Register;

public class EX_MEM {

    public Register register_0;

    public AsmLine<?> asmLine;

    public int value;

    public int rd_value;

    public Map<Register, Integer> forwardingMap = new HashMap<>();

    public int memoryAddress;

}
