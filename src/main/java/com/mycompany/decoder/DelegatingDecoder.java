package com.mycompany.decoder;

import com.mycompany.app.App;
import com.mycompany.data.AsmLine;

public class DelegatingDecoder implements Decoder {

    private RV32IBaseIntegerInstructionSetDecoder rv32IDecoder = new RV32IBaseIntegerInstructionSetDecoder(App.XLEN);

    private MDecoder mDecoder = new MDecoder();

    @Override
    public AsmLine<?> decode(int instruction) {
        AsmLine<?> asmLine = mDecoder.decode(instruction);
        if (asmLine != null) {
            return asmLine;
        }
        return rv32IDecoder.decode(instruction);
    }
    
}
