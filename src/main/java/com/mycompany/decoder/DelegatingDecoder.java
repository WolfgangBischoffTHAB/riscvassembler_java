package com.mycompany.decoder;

import java.util.List;

import com.mycompany.app.App;
import com.mycompany.data.AsmLine;
import com.mycompany.memory.Memory;

public class DelegatingDecoder implements Decoder {

    private RV32IBaseIntegerInstructionSetDecoder rv32IDecoder = new RV32IBaseIntegerInstructionSetDecoder(App.XLEN);

    private MDecoder mDecoder = new MDecoder();

    public Memory memory;

    @Override
    public List<AsmLine<?>> decode(long address) {

        mDecoder.memory = memory;

        // decoder for the M-Extension (Mult)
        List<AsmLine<?>> asmLines = mDecoder.decode(address);
        if (asmLines != null) {
            return asmLines;
        }
        rv32IDecoder.memory = memory;
        return rv32IDecoder.decode(address);
    }

}
