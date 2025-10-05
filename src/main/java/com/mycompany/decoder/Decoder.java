package com.mycompany.decoder;

import com.mycompany.data.AsmLine;

public interface Decoder {

    AsmLine<?> decode(int instruction);
    
}
