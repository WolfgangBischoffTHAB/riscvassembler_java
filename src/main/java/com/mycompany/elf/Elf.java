package com.mycompany.elf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;

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

/**
 * e_type
 * This member identifies the object file type.
 * Name Value Meaning
 * ET_NONE 0 No file type
 * ET_REL 1 Relocatable file
 * ET_EXEC 2 Executable file
 * ET_DYN 3 Shared object file
 * ET_CORE 4 Core file
 * ET_LOOS 0xfe00 Operating system-specific
 * ET_HIOS 0xfeff Operating system-specific
 * ET_LOPROC 0xff00 Processor-specific
 * ET_HIPROC 0xffff Processor-specific
 */
enum EI_TYPE {

    ET_NONE(0),

    ET_REL(1),

    ET_EXEC(2),

    ET_DYN(3),

    ET_CORE(4),

    ET_LOOS(0xfe00),

    ET_HIOS(0xfeff),

    ET_LOPROC(0xff00),

    ET_HIPROC(0xffff);

    @SuppressWarnings("unused")
    private int type_;

    EI_TYPE(final int type_) {
        this.type_ = type_;
    }

    public static EI_TYPE fromInt(final int type_in) {

        switch (type_in) {
            case 0:
                return ET_NONE;

            case 1:
                return ET_REL;

            case 2:
                return ET_EXEC;

            case 3:
                return ET_DYN;

            case 4:
                return ET_CORE;

            case 0xfe00:
                return ET_LOOS;

            case 0xfeff:
                return ET_HIOS;

            case 0xff00:
                return ET_LOPROC;

            case 0xffff:
                return ET_HIPROC;
        }

        throw new RuntimeException("Unknown type: \"" + type_in + "\"");
    }

}

enum SEGMENT_TYPE {

    PT_NULL(0),

    PT_LOAD(1),

    PT_DYNAMIC(2),

    PT_INTERP(3),

    PT_NOTE(4),

    PT_SHLIB(5),

    PT_PHDR(6),

    PT_TLS(7),

    PT_LOOS(0x60000000),

    PT_HIOS(0x6fffffff),

    PT_LOPROC(0x70000000),

    PT_HIPROC(0x7fffffff);

    @SuppressWarnings("unused")
    private int type_;

    SEGMENT_TYPE(final int type_) {
        this.type_ = type_;
    }

    public static SEGMENT_TYPE fromInt(final int type_in) {

        switch (type_in) {
            case 0:
                return PT_NULL;

            case 1:
                return PT_LOAD;

            case 2:
                return PT_DYNAMIC;

            case 3:
                return PT_INTERP;

            case 4:
                return PT_NOTE;

            case 0xfe00:
                return PT_SHLIB;

            case 0xfeff:
                return PT_PHDR;

            case 0xff00:
                return PT_TLS;

            case 0xffff:
                return PT_LOOS;

            case 0x6fffffff:
                return PT_HIOS;

            case 0x70000000:
                return PT_LOPROC;

            case 0x7fffffff:
                return PT_HIPROC;
        }

        throw new RuntimeException("Unknown type: \"" + type_in + "\"");
    }

}

/**
 * 
 * 
 * ypedef __u32 Elf32_Addr;
 * typedef __u16 Elf32_Half;
 * typedef __u32 Elf32_Off;
 * typedef __s32 Elf32_Sword;
 * typedef __u32 Elf32_Word;
 * 
 * typedef struct {
 * Elf32_Word p_type;
 * Elf32_Off p_offset;
 * Elf32_Addr p_vaddr;
 * Elf32_Addr p_paddr;
 * Elf32_Word p_filesz;
 * Elf32_Word p_memsz;
 * Elf32_Word p_flags;
 * Elf32_Word p_align;
 * } Elf32_Phdr;
 * 
 * typedef struct {
 * Elf64_Word p_type;
 * Elf64_Word p_flags;
 * Elf64_Off p_offset;
 * Elf64_Addr p_vaddr;
 * Elf64_Addr p_paddr;
 * Elf64_Xword p_filesz;
 * Elf64_Xword p_memsz;
 * Elf64_Xword p_align;
 * } Elf64_Phdr;
 */
class Elf32_Phdr {

    public SEGMENT_TYPE p_type;

    public int p_offset;

    public int p_vaddr;

    public int p_paddr;

    public int p_filesz;

    public int p_memsz;

    public int p_flags;

    public int p_align;

    public void load(byte[] buffer, int e_phoff) {

        p_type = SEGMENT_TYPE.fromInt(ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 0));
        p_offset = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);
        p_vaddr = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);
        p_paddr = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);
        p_filesz = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);
        p_memsz = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);
        p_flags = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);
        p_align = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);

    }

}

/**
 * typedef struct
 * {
 * unsigned char e_ident[EI_NIDENT]; // Offset(0) - Magic number and other info
 * (16 byte) magic number 7f 45 4c 46
 * // EI_MAG0 0 File identification
 * // EI_MAG1 1 File identification
 * // EI_MAG2 2 File identification
 * // EI_MAG3 3 File identification
 * // EI_CLASS 4 File class
 * // EI_DATA 5 Data encoding
 * // EI_VERSION 6 File version
 * // EI_OSABI 7 Operating system/ABI identification
 * // EI_ABIVERSION 8 ABI version
 * // EI_PAD 9 Start of padding bytes
 * // EI_NIDENT 16 Size of e_ident[]
 * Elf32_Half e_type; // Object file type // Offset(16)
 * Elf32_Half e_machine; // Architecture // Offset(18)
 * Elf32_Word e_version; // Object file version // Offset(20)
 * Elf32_Addr e_entry; // Entry point virtual address // Offset(24)
 * Elf32_Off e_phoff; // Program header table file offset
 * Elf32_Off e_shoff; // Section header table file offset
 * Elf32_Word e_flags; // Processor-specific flags
 * Elf32_Half e_ehsize; // ELF header size in bytes
 * Elf32_Half e_phentsize; // Program header table entry size
 * Elf32_Half e_phnum; // Program header table entry count
 * Elf32_Half e_shentsize; // Section header table entry size
 * Elf32_Half e_shnum; // Section header table entry count
 * Elf32_Half e_shstrndx; // Section header string table index
 * } Elf32_Ehdr;
 */
class Elf32_Ehdr {

    /**
     * Figure 5-3: Segment Flag Bits, p_flags
     * Name Value Meaning
     * PF_X 0x1 Execute
     * PF_W 0x2 Write
     * PF_R 0x4 Read
     * PF_MASKOS 0x0ff00000 Unspecified
     * PF_MASKPROC 0xf0000000 Unspecified
     */
    public static final int PF_X = 0x01;
    public static final int PF_W = 0x02;
    public static final int PF_R = 0x04;
    public static final int PF_MASKOS = 0x0ff00000;
    public static final int PF_MASKPROC = 0xf0000000;

    public int magicNumbers1;

    public EI_CLASS e_class;

    public EI_TYPE e_type;

    public int e_version;

    public int e_entry;

    public int e_phoff;

    public Elf32_Phdr programHeader;

    public int load(byte[] buffer, int pos) {

        // int32_t (4 byte) magic number
        magicNumbers1 = ByteArrayUtil.decodeInt32FromArrayLittleEndian(buffer, pos += 0);
        // int8_t (1 byte) e_class (32/64 bit file)
        e_class = EI_CLASS.fromInt(ByteArrayUtil.decodeInt8FromArray(buffer, pos += 4));

        pos += 8;

        // int16_t (2 byte) e_type (executable or other)
        e_type = EI_TYPE.fromInt(ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos += 4));

        e_version = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos += 4);

        e_entry = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos += 4);

        // int32_t (4 byte) e_phoff (program header offset)
        e_phoff = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos += 4);

        // Flags: 1 = executable, 2 = writable, 4 = readable.
        return pos;
    }

}

/**
 * 
 */
public class Elf {

    public String filename;

    public File file;

    public byte[] buffer;

    public Elf32_Phdr programHeader;

    public void load() throws IOException {

        int pos = 0;
        buffer = Files.readAllBytes(Paths.get(filename));

        Elf32_Ehdr elf32_Ehdr = new Elf32_Ehdr();
        pos = elf32_Ehdr.load(buffer, pos);

        // System.out.println("magicNumbers: " + ByteArrayUtil.intToHex(magicNumbers1));

        if (elf32_Ehdr.magicNumbers1 != 0x7F454C46) {
            throw new RuntimeException("Not an elf file! " + filename);
        }

        if (elf32_Ehdr.e_class != EI_CLASS.ELFCLASS32) {
            throw new RuntimeException("Not an 32bit elf file! " + filename);
        }

        if (elf32_Ehdr.e_type != EI_TYPE.ET_EXEC) {
            throw new RuntimeException("Not an executable elf file! " + filename);
        }

        // Iterate over each 'program header' in the table and check
        // - the p_flags field for PF_X (executable) If it has this flag, then the
        // segment contains executable code.
        // - p_type == PT_LOAD (loadable)

        programHeader = new Elf32_Phdr();
        programHeader.load(buffer, elf32_Ehdr.e_phoff);

        // the first program header is checked it if has the PL_LOAD type and if it has
        // the executable flag
        if (programHeader.p_type != SEGMENT_TYPE.PT_LOAD) {
            throw new RuntimeException("Not an loadable program header!");
        }
        if ((programHeader.p_flags & Elf32_Ehdr.PF_X) == 0) {
            throw new RuntimeException("Not an executable program header!");
        }

        // compute offset to machine code
        int machine_code_offset = programHeader.p_offset;
        // if the offset in the loadable, executable header is 0, this means
        // it points onto the ELF-Header which is not executable. In this
        // case, add the entry-point offset from the ELF-header to point to
        // the machine code
        if (machine_code_offset == 0) {
            machine_code_offset += elf32_Ehdr.e_entry;
            machine_code_offset -= programHeader.p_vaddr;
        }

        System.out.println("Loading machine code virtual address: " + programHeader.p_vaddr);
        System.out.println("Loading machine code from elf-file offset: " + machine_code_offset);

        int instructionMachineCode = 0;
        try {
            int decodePos = machine_code_offset;
            for (int i = 0; i < 65; i++) {

                instructionMachineCode = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, decodePos);

                // if (instructionMachineCode == 0x93d71740) {
                //     System.out.println("Test");
                //     // System.out.println(ByteArrayUtil.byteToHex(instruction));
                // }
                // if (instructionMachineCode == 0x4017d793) {
                //     System.out.println("Test");
                //     // System.out.println(ByteArrayUtil.byteToHex(instruction));
                // }
                if (instructionMachineCode == 0x3a434347) {
                    System.out.println("Test");
                    // System.out.println(ByteArrayUtil.byteToHex(instruction));
                }
                if (instructionMachineCode == 0x4743433a) {
                    System.out.println("Test");
                    // System.out.println(ByteArrayUtil.byteToHex(instruction));
                }


                
                AsmLine<?> asmLine = Decoder.decode(instructionMachineCode);
                System.out.println(asmLine);

                decodePos += 4;
            }
        } catch (Exception e) {
            System.out.println(ByteArrayUtil.byteToHex(instructionMachineCode));
            // srai a5, a5, 1
        }

    }

    public byte[] getMachineCode() {
        throw new UnsupportedOperationException("Unimplemented method 'getMachineCode'");
    }

    public void setFile(String filename) {
        this.filename = filename;
    }
}
