package com.mycompany.data;

import java.util.ArrayList;
import java.util.List;

public class Section {

    public long address;
    public String name;
    public String memspec;
    public List<String> inputSections = new ArrayList<>();

    @Override
    public String toString() {
        return "Section [address=" + address + ", name=" + name + ", memspec=" + memspec + ", inputSections="
                + inputSections + "]";
    }

}
