package com.mycompany.cpu;

import java.io.IOException;

public abstract class AbstractCPU implements CPU {    

    @Override
    public abstract boolean step() throws IOException;
    
}
