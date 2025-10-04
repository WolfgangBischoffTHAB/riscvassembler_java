package com.mycompany.elf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;
import com.mycompany.memory.Memory;


// using Elf32_Addr = uint32_t; // Program address
// using Elf32_Off = uint32_t;  // File offset
// using Elf32_Half = uint16_t;
// using Elf32_Word = uint32_t;
// using Elf32_Sword = int32_t;
 
// using Elf64_Addr = uint64_t;
// using Elf64_Off = uint64_t;
// using Elf64_Half = uint16_t;
// using Elf64_Word = uint32_t;
// using Elf64_Sword = int32_t;
// using Elf64_Xword = uint64_t;
// using Elf64_Sxword = int64_t;

/**
 * Section type.
 * https://refspecs.linuxbase.org/elf/gabi4+/ch4.sheader.html
 * Name Value
 * SHT_NULL 0
 * SHT_PROGBITS 1
 * SHT_SYMTAB 2
 * SHT_STRTAB 3
 * SHT_RELA 4
 * SHT_HASH 5
 * SHT_DYNAMIC 6
 * SHT_NOTE 7
 * SHT_NOBITS 8
 * SHT_REL 9
 * SHT_SHLIB 10
 * SHT_DYNSYM 11
 * SHT_INIT_ARRAY 14
 * SHT_FINI_ARRAY 15
 * SHT_PREINIT_ARRAY 16
 * SHT_GROUP 17
 * SHT_SYMTAB_SHNDX 18
 * SHT_LOOS 0x60000000
 * SHT_HIOS 0x6fffffff
 * SHT_LOPROC 0x70000000
 * SHT_HIPROC 0x7fffffff
 * SHT_LOUSER 0x80000000
 * SHT_HIUSER 0xffffffff
 */
enum SH_TYPE {

    SHT_NULL(0),

    SHT_PROGBITS(1),

    SHT_SYMTAB(2),

    SHT_STRTAB(3),

    SHT_RELA(4),

    SHT_HASH(5),

    SHT_DYNAMIC(6),

    SHT_NOTE(7),

    SHT_NOBITS(8),

    SHT_REL(9),

    SHT_SHLIB(10),

    SHT_DYNSYM(11),

    SHT_INIT_ARRAY(14),

    SHT_FINI_ARRAY(15),

    SHT_PREINIT_ARRAY(16),

    SHT_GROUP(17),

    SHT_SYMTAB_SHNDX(18),

    SHT_LOOS(0x60000000),

    SHT_HIOS(0x6fffffff),

    SHT_LOPROC(0x70000000),

    SHT_HIPROC(0x7fffffff),

    SHT_LOUSER(0x80000000),

    SHT_HIUSER(0xffffffff),

    SHT_UNKNOWN_FIXME(0x12345678);

    @SuppressWarnings("unused")
    private int type_;

    SH_TYPE(final int type_) {
        this.type_ = type_;
    }

    public static SH_TYPE fromInt(final int type_in) {

        switch (type_in) {
            case 0:
                return SHT_NULL;

            case 1:
                return SHT_PROGBITS;

            case 2:
                return SHT_SYMTAB;

            case 3:
                return SHT_STRTAB;

            case 4:
                return SHT_RELA;

            case 5:
                return SHT_HASH;

            case 6:
                return SHT_DYNAMIC;

            case 7:
                return SHT_NOTE;

            case 8:
                return SHT_NOBITS;

            case 9:
                return SHT_REL;

            case 10:
                return SHT_SHLIB;

            case 11:
                return SHT_DYNSYM;

            case 14:
                return SHT_INIT_ARRAY;

            case 15:
                return SHT_FINI_ARRAY;

            case 16:
                return SHT_PREINIT_ARRAY;

            case 17:
                return SHT_GROUP;

            case 18:
                return SHT_SYMTAB_SHNDX;

            case 0x60000000:
                return SHT_LOOS;

            case 0x6fffffff:
                return SHT_HIOS;

            case 0x70000000:
                return SHT_LOPROC;

            case 0x7fffffff:
                return SHT_HIPROC;

            case 0x80000000:
                return SHT_LOUSER;

            case 0xffffffff:
                return SHT_HIUSER;

            default:
                return SHT_UNKNOWN_FIXME;
        }

        //throw new RuntimeException("Unknown type: \"" + type_in + "\"");
    }

}

/*
 * typedef struct {
 * Elf32_Word sh_name;
 * Elf32_Word sh_type;
 * Elf32_Word sh_flags;
 * Elf32_Addr sh_addr;
 * Elf32_Off sh_offset;
 * Elf32_Word sh_size;
 * Elf32_Word sh_link;
 * Elf32_Word sh_info;
 * Elf32_Word sh_addralign;
 * Elf32_Word sh_entsize;
 * } Elf32_Shdr;
 * 
 * typedef struct {
 * Elf64_Word sh_name;
 * Elf64_Word sh_type;
 * Elf64_Xword sh_flags;
 * Elf64_Addr sh_addr;
 * Elf64_Off sh_offset;
 * Elf64_Xword sh_size;
 * Elf64_Word sh_link;
 * Elf64_Word sh_info;
 * Elf64_Xword sh_addralign;
 * Elf64_Xword sh_entsize;
 * } Elf64_Shdr;
 */

class Elf32_Shdr {

    public static final int SIZE = 10 * 4; // 10 membery, 4 byte each

    public int sh_name; // index into another table that stores the name as a string
    public int sh_type;
    public int sh_flags;
    public int sh_addr;
    public int sh_offset; // absolute offset from start of elf-file to he location where the entries are
                          // stored
    public int sh_size; // section's overall size in bytes
    public int sh_link;
    public int sh_info;
    public int sh_addralign;
    public int sh_entsize; // size in bytes of each entry

    public void load(byte[] buffer, int offset) {

        sh_name = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 0);
        sh_type = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_flags = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_addr = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_offset = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_size = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_link = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_info = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_addralign = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_entsize = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);

    }

}

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
 * Elf32_Half e_type; // Object file type // 2 Byte // Offset(16)
 * Elf32_Half e_machine; // Architecture // 2 Byte // Offset(18)
 * Elf32_Word e_version; // Object file version // 4 Byte // Offset(20)
 * Elf32_Addr e_entry; // Entry point virtual address // 4 Byte // Offset(24)
 * Elf32_Off e_phoff; // Program header table file offset // 4 Byte
 * Elf32_Off e_shoff; // Section header table file offset // 4 Byte
 * Elf32_Word e_flags; // Processor-specific flags // 4 Byte
 * Elf32_Half e_ehsize; // ELF header size in bytes // 2 Byte
 * Elf32_Half e_phentsize; // Program header table entry size // 2 Byte
 * Elf32_Half e_phnum; // Program header table entry count // 2 Byte
 * Elf32_Half e_shentsize; // Section header table entry size // 2 Byte
 * Elf32_Half e_shnum; // Section header table entry count // 2 Byte
 * Elf32_Half e_shstrndx; // Section header string table index // 2 Byte
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

    public int e_machine;

    public int e_version;

    public int e_entry;

    public int e_phoff;

    public int e_shoff;

    public int e_flags; // Processor-specific flags

    public int e_ehsize; // ELF header size in bytes

    public int e_phentsize; // Program header table entry size

    public int e_phnum; // Program header table entry count

    public int e_shentsize; // Section header table entry size

    public int e_shnum; // Section header table entry count

    public int e_shstrndx; // Section header string table index

    public Elf32_Phdr programHeader;

    public int load(byte[] buffer, int pos) {

        // int32_t (4 byte) magic number
        magicNumbers1 = ByteArrayUtil.decodeInt32FromArrayLittleEndian(buffer, pos);
        pos += 4;

        // int8_t (1 byte) e_class (32/64 bit file)
        e_class = EI_CLASS.fromInt(ByteArrayUtil.decodeInt8FromArray(buffer, pos));
        pos += 12;

        // int16_t (2 byte) e_type (executable or other)
        e_type = EI_TYPE.fromInt(ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos));
        pos += 2;

        // int16_t (2 byte) e_machine
        e_machine = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos);
        pos += 2;

        // 4 byte
        e_version = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos);
        pos += 4;

        // 4 byte, program flow entry point virtual address
        e_entry = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos);
        pos += 4;

        // int32_t (4 byte) e_phoff (program header offset)
        e_phoff = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos);
        pos += 4;

        // int32_t (4 byte) e_shoff (section header offset)
        e_shoff = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos);
        pos += 4;

        e_flags = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos); // Processor-specific flags
        pos += 4;

        e_ehsize = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos); // ELF header size in bytes
        pos += 2;
        e_phentsize = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos); // Program header table entry size
        pos += 2;
        e_phnum = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos); // Program header table entry count
        pos += 2;
        e_shentsize = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos); // Section header table entry size
        pos += 2;
        e_shnum = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos); // Section header table entry count
        pos += 2;
        e_shstrndx = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos); // Section header string table index
        pos += 2;

        // Flags: 1 = executable, 2 = writable, 4 = readable.
        return pos;
    }

}




/**
 * 
 */
public class Elf {

    private static final boolean DECODE = false;

    public String filename;

    public File file;

    public byte[] buffer;

    // public byte[] machine_code;

    public Elf32_Phdr programHeader;

    public List<Elf32Sym> elf32SymList = new ArrayList<>();

    public List<ElfSymbolTable> symbolTableList = new ArrayList<>();

    public Memory memory;

    // public String strtab;

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

        // GOAL: Find main symbol
        // 1. Retrieve the "Section header table offset" from the ELF-Header
        int sectionHeaderTableOffset = elf32_Ehdr.e_shoff;

        // 2. Retrieve the "Number of entries in the section header table"
        int numberOfEntriesSectionHeaderTable = elf32_Ehdr.e_shnum;

        //
        // Iterate over sections in order to find the address of the main-function/symbol
        //

        // Elf32_Shdr symTabSectionHeader = new Elf32_Shdr();

        for (int i = 0; i < numberOfEntriesSectionHeaderTable; i++) {

            int sectionHeaderOffset = sectionHeaderTableOffset + i * Elf32_Shdr.SIZE;

            Elf32_Shdr sectionHeader = new Elf32_Shdr();
            sectionHeader.load(buffer, sectionHeaderOffset);

            SH_TYPE type = SH_TYPE.fromInt(sectionHeader.sh_type);

            System.out.println("------------------------------------------");
            System.out.println("Name: " + sectionHeader.sh_name);
            System.out.println("Type: " + type);
            System.out.println("Size: " + sectionHeader.sh_size);
            System.out.println("Offset: " + ByteArrayUtil.byteToHex(sectionHeader.sh_offset));

            // https://wiki.osdev.org/ELF_Tutorial
            // The String Table
            // The string table conceptually is quite simple: it's just a number of consecutive 
            // zero-terminated strings. String literals used in the program are stored in one of 
            // the tables. There are a number of different string tables that may be present in 
            // an ELF object such as .strtab (the default string table), .shstrtab (the section 
            // string table) and .dynstr (string table for dynamic linking). Any time the loading 
            // process needs access to a string, it uses an offset into one of the string tables. 
            // The offset may point to the beginning of a zero-terminated string or somewhere in 
            // the middle or even to the zero terminator itself, depending on usage and scenario. 
            // The size of the string table itself is specified by sh_size in the corresponding 
            // section header entry. The simplest program loader may copy all string tables into 
            // memory, but a more complete solution would omit any that are not necessary during 
            // runtime such, notably those not flagged with SHF_ALLOC in their respective section 
            // header (such as .shstrtab, since section names aren't used in program runtime).
            
            // load the string table.
            // The String table is a large string that is index by symbols in the symbol table.
            //
            // How indexing works is by treating the strtab as one large string and then 
            // index to a individual character within this large string. The index is stored
            // in the st_name field of a symbol in the symbol table. The substring is then 
            // constructed up to the next zero-terminator.
            //
            // It is incorrect to split the symbol table into individual strings and put then
            // into a hashmap! This is not how the indexing works! The indexes are pointing
            // at substrings as described above!
            if (type == SH_TYPE.SHT_STRTAB) {

                // the entries of the section are stored at sh_offset
                int sectionEntryOffset = sectionHeader.sh_offset;

                int size = sectionHeader.sh_size;
                // System.out.println(size);

                String strtab = new String(buffer, sectionEntryOffset, size);
                
                // DEBUG
                String temp = strtab;
                temp = temp.replaceAll("\0", "\n");
                System.out.println(temp);

                ElfSymbolTable symbolTable = new ElfSymbolTable();
                symbolTableList.add(symbolTable);
                symbolTable.strtab = strtab;
            }

            // symbols which have a reference to the string tables
            if (type == SH_TYPE.SHT_SYMTAB) {

                // the entries of the section are stored at sh_offset
                int sectionEntryOffset = sectionHeader.sh_offset;
                System.out.println(ByteArrayUtil.byteToHex(sectionEntryOffset));

                int sectionEntries = sectionHeader.sh_size / sectionHeader.sh_entsize;

                int size = sectionHeader.sh_size;

                String result = new String(buffer, sectionEntryOffset, size);
            
                // DEBUG
                String temp = result;
                temp = temp.replaceAll("\0", "\n");
                System.out.println(temp);

                for (int j = 0; j < sectionEntries; j++) {

                    Elf32Sym elf32_Sym = new Elf32Sym();
                    elf32SymList.add(elf32_Sym);
                    elf32_Sym.load(buffer, sectionEntryOffset);

                    // int st_name = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, sectionEntryOffset + 0);
                    // int st_value = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, sectionEntryOffset + 4);
                    // int st_size = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, sectionEntryOffset + 8);
                    // int st_info = ByteArrayUtil.decodeInt8FromArray(buffer, sectionEntryOffset + 12);
                    // int st_other = ByteArrayUtil.decodeInt8FromArray(buffer, sectionEntryOffset + 13);
                    // int st_shndx = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, sectionEntryOffset + 14);

                    // System.out.println((j+1) + ")    +++++++++++++++++++++++++++++++++++");
                    // System.out.println("    st_name: " + st_name);
                    // System.out.println("    st_value: " + ByteArrayUtil.byteToHex(st_value));
                    // System.out.println("    st_size: " + st_size);
                    // System.out.println("    st_info: " + st_info);
                    // System.out.println("    st_other: " + st_other);
                    // System.out.println("    st_shndx: " + st_shndx);

                    // // resolve the index to a string
                    // if (st_name != 0) {
                    //     System.out.println("    string_value: " + stringArray[st_shndx]);
                    // }

                    sectionEntryOffset += 16;
                }

            }
        }

        // DEBUG output the symbols in the symbol table
        int elf32SymIndex = 0;
        for (Elf32Sym elf32_Sym : elf32SymList) {

            // DEBUG
            //System.out.println((elf32SymIndex) + ")    +++++++++++++++++++++++++++++++++++");
            System.out.print((elf32SymIndex) + ")");
            System.out.print("    st_name: " + ByteArrayUtil.byteToHex(elf32_Sym.st_name) + " (" + elf32_Sym.st_name + ") ");
            System.out.print("    st_value: " + ByteArrayUtil.byteToHex(elf32_Sym.st_value));
            System.out.print("    st_size: " + elf32_Sym.st_size);
            System.out.print("    st_info: " + elf32_Sym.st_info);
            System.out.print("    st_other: " + elf32_Sym.st_other);
            System.out.println("    st_shndx: " + elf32_Sym.st_shndx);

            if (elf32_Sym.st_name != 0) {

                String val = symbolTableList.get(0).strtab;

                // start with the index and construct a string of all
                // characters up to the next null-terminator character
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = elf32_Sym.st_name; i < val.length(); i++) {
                    char c = val.charAt(i);
                    if (c == '\0') {
                        break;
                    }
                    stringBuilder.append(c);
                }
                System.out.println(stringBuilder.toString());

                elf32_Sym.resolved_st_name = stringBuilder.toString();
            }

            elf32SymIndex++;
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
        //if (machine_code_offset == 0) {
        //    machine_code_offset += elf32_Ehdr.e_entry;

            // System.out.println("Loading machine code virtual address: " +
            // programHeader.p_vaddr);
            //machine_code_offset -= programHeader.p_vaddr;
        //}

        // create a machine_code buffer large enough to hold the application
        // plus additional space for the stack! 
        // TODO: the stack and the application memory should not be related
        // to each other. Change how the stack pointer of the CPU is initialized: see App.java
        // stack-pointer initialization
        //machine_code = new byte[programHeader.p_memsz + 10000];
        //System.arraycopy(buffer, machine_code_offset, machine_code, 0, programHeader.p_memsz);

        memory.copy(programHeader.p_paddr, buffer, machine_code_offset, programHeader.p_memsz);

        if (DECODE) {

            System.out.println(
                    "Loading machine code from elf-file offset: " + ByteArrayUtil.byteToHex(machine_code_offset));

            int instructionMachineCode = 0;
            try {

                int decodePos = machine_code_offset;

                System.out.println("Programheader memsize: " + ByteArrayUtil.byteToHex(programHeader.p_memsz) + " "
                        + programHeader.p_memsz);
                System.out.println("Programheader filesize: " + ByteArrayUtil.byteToHex(programHeader.p_filesz) + " "
                        + programHeader.p_filesz);
                for (int i = 0; i < (programHeader.p_memsz / 4); i++) {

                    instructionMachineCode = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, decodePos);

                    // if (instructionMachineCode == 0x93d71740) {
                    // System.out.println("Test");
                    // // System.out.println(ByteArrayUtil.byteToHex(instruction));
                    // }
                    // if (instructionMachineCode == 0x4017d793) {
                    // System.out.println("Test");
                    // // System.out.println(ByteArrayUtil.byteToHex(instruction));
                    // }
                    // if (instructionMachineCode == 0x3a434347) {
                    // System.out.println("Test");
                    // // System.out.println(ByteArrayUtil.byteToHex(instruction));
                    // }
                    // if (instructionMachineCode == 0x4743433a) {
                    // System.out.println("Test");
                    // // System.out.println(ByteArrayUtil.byteToHex(instruction));
                    // }

                    AsmLine<?> asmLine = Decoder.decode(instructionMachineCode);
                    System.out.println(ByteArrayUtil.byteToHex(decodePos) + ": " + asmLine);

                    decodePos += 4;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(ByteArrayUtil.byteToHex(instructionMachineCode));
            }

        }

    }

    // public byte[] getMachineCode() {
    //     return machine_code;
    // }

    public void setFile(String filename) {
        this.filename = filename;
    }
}
