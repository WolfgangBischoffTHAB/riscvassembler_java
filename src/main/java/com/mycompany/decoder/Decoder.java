package com.mycompany.decoder;

import java.util.List;

import com.mycompany.data.AsmLine;

public interface Decoder {

    List<AsmLine<?>> decode(int instruction);

}
