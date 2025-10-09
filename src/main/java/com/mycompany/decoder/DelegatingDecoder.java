package com.mycompany.decoder;

import com.mycompany.data.AsmLine;

public class DelegatingDecoder implements Decoder {

    private RV32IBaseIntegerInstructionSetDecoder rv32IDecoder = new RV32IBaseIntegerInstructionSetDecoder();

    private MDecoder mDecoder = new MDecoder();

    @Override
    public AsmLine<?> decode(int instruction) {

        // if (instruction == 0x2F777B3) {
        //     System.out.println("test");
        // }

        AsmLine<?> asmLine = mDecoder.decode(instruction);
        if (asmLine != null) {
            return asmLine;
        }
        return rv32IDecoder.decode(instruction);
    }
    
}
