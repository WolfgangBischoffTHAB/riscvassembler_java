package com.mycompany.elf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.memory.Memory;

/**
 * EI_CLASS - identifies the file's class, or capacity.
 * 
 * ELFCLASSNONE 0 Invalid class
 * ELFCLASS32 1 32-bit objects
 * ELFCLASS64 2 64-bit objects
 */
enum EI_CLASS {

    ELFCLASSNONE(0),

    ELFCLASS32(1),

    ELFCLASS64(2);

    @SuppressWarnings("unused")
    private int class_;

    EI_CLASS(final int class_) {
        this.class_ = class_;
    }

    public static EI_CLASS fromInt(final int class_in) {

        switch (class_in) {
            case 0:
                return ELFCLASSNONE;

            case 1:
                return ELFCLASS32;

            case 2:
                return ELFCLASS64;
        }

        throw new RuntimeException("Unknown class: \"" + class_in + "\"");
    }

}

public class BaseElf {
    
    public static final int ELF_MAGIC_NUMBER = 0x7F454C46;

    public static final boolean DECODE = false;

    public String filename;

    public File file;

    public byte[] buffer;    

    public long globalPointerValue;

    public List<ElfSymbolTable> symbolTableList = new ArrayList<>();

    public void setFile(String filename) {
        this.filename = filename;
    }

}
