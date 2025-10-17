package com.mycompany.data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Section {

    public long address;
    public long currentOffset;

    public String name;
    public String memspec;
    public List<String> inputSections = new ArrayList<>();

    public MemorySpecifier outputSection;

    public ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();

    @Override
    public String toString() {
        return "Section [address=" + address + ", name=" + name + ", memspec=" + memspec + ", inputSections="
                + inputSections + "]";
    }

}
