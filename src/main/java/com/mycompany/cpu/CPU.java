package com.mycompany.cpu;

import java.io.IOException;

public interface CPU {

    boolean step() throws IOException;

    long[] getRegisterFile();

}
