package com.mycompany.elf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.decoder.DelegatingDecoder;
import com.mycompany.memory.Memory;
import com.mycompany.memory.Memory64;

/**
 * https://refspecs.linuxbase.org/elf/gabi4+/ch4.eheader.html
 * 
 * 64-bit ELF base types.
 * typedef __u64 Elf64_Addr;
 * typedef __u16 Elf64_Half;
 * typedef __s16 Elf64_SHalf;
 * typedef __u64 Elf64_Off;
 * typedef __s32 Elf64_Sword;
 * typedef __u32 Elf64_Word;
 * typedef __u64 Elf64_Xword;
 * typedef __s64 Elf64_Sxword;
 * typedef __u16 Elf64_Versym;
 * 
 * class Elf64_Ehdr {
 * unsigned char e_ident[EI_NIDENT]; // 16 byte
 * Elf64_Half e_type; // 16 byte
 * Elf64_Half e_machine; // 16 byte
 * Elf64_Word e_version; // 32 byte
 * Elf64_Addr e_entry;
 * Elf64_Off e_phoff;
 * Elf64_Off e_shoff;
 * Elf64_Word e_flags;
 * Elf64_Half e_ehsize;
 * Elf64_Half e_phentsize;
 * Elf64_Half e_phnum;
 * Elf64_Half e_shentsize;
 * Elf64_Half e_shnum;
 * Elf64_Half e_shstrndx;
 * }
 */
class Elf64_Ehdr {

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

    public long e_entry;

    public long e_phoff;

    public long e_shoff;

    public int e_flags; // Processor-specific flags

    public int e_ehsize; // ELF header size in bytes

    public int e_phentsize; // Program header table entry size

    public int e_phnum; // Program header table entry count

    public int e_shentsize; // Section header table entry size

    public int e_shnum; // Section header table entry count

    public int e_shstrndx; // Section header string table index

    public int load(byte[] buffer, int pos) {

        //
        // 16 byte e_ident[EI_NIDENT]
        //

        // int32_t (4 byte) magic number
        magicNumbers1 = ByteArrayUtil.decodeInt32FromArrayLittleEndian(buffer, pos);
        pos += 4;

        // int8_t (1 byte) e_class (32/64 bit file)
        e_class = EI_CLASS.fromInt(ByteArrayUtil.decodeInt8FromArray(buffer, pos));
        pos += 12;

        //
        // rest of the fields
        //

        // int16_t (2 byte) e_type (executable or other)
        e_type = EI_TYPE.fromInt(ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos));
        pos += 2;

        // int16_t (2 byte) e_machine
        e_machine = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos);
        pos += 2;

        // 4 byte
        e_version = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos);
        pos += 4;

        // int64_t (8 byte), program flow entry point virtual address
        e_entry = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, pos);
        pos += 8;

        // int64_t (8 byte) e_phoff (program header offset)
        e_phoff = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, pos);
        pos += 8;

        // int64_t (8 byte) e_shoff (section header offset)
        e_shoff = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, pos);
        pos += 8;

        // int32_t (4 byte)
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
 * typedef struct {
 * Elf64_Word sh_name; // 4 byte
 * Elf64_Word sh_type; // 4 byte
 * Elf64_Xword sh_flags; // 8 byte
 * Elf64_Addr sh_addr; // 8 byte
 * Elf64_Off sh_offset; // 8 byte
 * Elf64_Xword sh_size; // 8 byte
 * Elf64_Word sh_link; // 4 byte
 * Elf64_Word sh_info; // 4 byte
 * Elf64_Xword sh_addralign; // 8 byte
 * Elf64_Xword sh_entsize; // 8 byte
 * } Elf64_Shdr;
 */
class Elf64_Shdr {

    public static final int SIZE = 4 * 4 + 6 * 8; // 10 members, 4 byte each

    public int sh_name; // index into another table that stores the name as a string
    public int sh_type;
    public long sh_flags;
    public long sh_addr;
    public long sh_offset; // absolute offset from start of elf-file to he location where the entries are
                           // stored
    public long sh_size; // section's overall size in bytes
    public int sh_link;
    public int sh_info;
    public long sh_addralign;
    public long sh_entsize; // size in bytes of each entry

    public SH_TYPE type; // sh_type converted to a enum

    protected String resolved_st_name; // name resolved from the string table

    public void load(byte[] buffer, int offset) {
        sh_name = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 0);
        sh_type = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_flags = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 4);
        sh_addr = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 8);
        sh_offset = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 8);
        sh_size = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 8);
        sh_link = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 8);
        sh_info = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        sh_addralign = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 4);
        sh_entsize = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 8);
    }

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("------------------------------------------\n");
        stringBuilder.append("Name: " + ByteArrayUtil.byteToHex(sh_name) + " (" + sh_name + ")\n");
        stringBuilder.append("Type: " + type + " (" + sh_type + ")\n");
        stringBuilder.append("Flags: " + ByteArrayUtil.byteToHex(sh_flags) + " (" + sh_flags + ")\n");
        stringBuilder.append("Addr: " + ByteArrayUtil.byteToHex(sh_addr) + " (" + sh_addr + ")\n");
        stringBuilder.append("Offset: " + ByteArrayUtil.byteToHex(sh_offset) + "\n");
        stringBuilder.append("Size: " + ByteArrayUtil.byteToHex(sh_size) + " (" + sh_size + ")\n");
        stringBuilder.append("Link: " + ByteArrayUtil.byteToHex(sh_link) + " (" + sh_link + ")\n");
        stringBuilder.append("Info: " + ByteArrayUtil.byteToHex(sh_info) + " (" + sh_info + ")\n");
        stringBuilder.append("Addralign: " + ByteArrayUtil.byteToHex(sh_addralign) + " (" + sh_addralign + ")\n");
        stringBuilder.append("Entsize: " + ByteArrayUtil.byteToHex(sh_entsize) + " (" + sh_entsize + ")\n");

        return stringBuilder.toString();
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
 * Elf64_Word p_type; // 4 byte
 * Elf64_Word p_flags; // 4 byte
 * Elf64_Off p_offset; // 8 byte
 * Elf64_Addr p_vaddr; // 8 byte
 * Elf64_Addr p_paddr; // 8 byte
 * Elf64_Xword p_filesz; // 8 byte
 * Elf64_Xword p_memsz; // 8 byte
 * Elf64_Xword p_align; // 8 byte
 * } Elf64_Phdr;
 */
class Elf64_Phdr {

    public SEGMENT_TYPE p_type;
    public int p_flags;
    public long p_offset;
    public long p_vaddr;
    public long p_paddr;
    public long p_filesz;
    public long p_memsz;
    public long p_align;

    public void load(byte[] buffer, int e_phoff) {

        p_type = SEGMENT_TYPE.fromInt(ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 0));
        p_flags = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, e_phoff += 4);
        p_offset = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, e_phoff += 4);
        p_vaddr = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, e_phoff += 8);
        p_paddr = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, e_phoff += 8);
        p_filesz = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, e_phoff += 8);
        p_memsz = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, e_phoff += 8);
        p_align = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, e_phoff += 8);

    }

}

/**
 * Elf Header, 64 bit version
 * 
 * https://refspecs.linuxbase.org/elf/gabi4+/ch4.eheader.html
 */
public class Elf64 extends BaseElf {

    private static final Logger logger = LoggerFactory.getLogger(Elf64.class);

    private static final boolean NOT_LOAD_NON_EXECUTABLE_PROGRAM_HEADERS = false;

    public Memory64 memory;

    public Elf64_Phdr programHeader;
    
    public List<Elf64Sym> elf64SymList = new ArrayList<>();

    @SuppressWarnings("unused")
    public void load() throws IOException {

        buffer = Files.readAllBytes(Paths.get(filename));

        // load the overall, top-level ELF-header that has offsets to all other parts of
        // the elf file
        Elf64_Ehdr elf64_Ehdr = new Elf64_Ehdr();

        int pos = 0;
        pos = elf64_Ehdr.load(buffer, pos);

        if (elf64_Ehdr.magicNumbers1 != ELF_MAGIC_NUMBER) {
            throw new RuntimeException("Not an elf file! " + filename);
        }

        if (elf64_Ehdr.e_class != EI_CLASS.ELFCLASS64) {
            throw new RuntimeException("Not an 64bit elf file! " + filename);
        }

        if (elf64_Ehdr.e_type != EI_TYPE.ET_EXEC) {
            throw new RuntimeException("Not an executable elf file! " + filename);
        }

        // GOAL: Find main symbol
        // 1. Retrieve the "Section header table offset" from the ELF-Header
        long sectionHeaderTableOffset = elf64_Ehdr.e_shoff;

        // 2. Retrieve the "Number of entries in the section header table"
        int numberOfEntriesSectionHeaderTable = elf64_Ehdr.e_shnum;

        List<Elf64_Shdr> sectionHeaders = new ArrayList<>();

        for (int i = 0; i < numberOfEntriesSectionHeaderTable; i++) {

            long sectionHeaderOffset = sectionHeaderTableOffset + i * Elf64_Shdr.SIZE;

            Elf64_Shdr sectionHeader = new Elf64_Shdr();
            sectionHeaders.add(sectionHeader);

            sectionHeader.load(buffer, (int) sectionHeaderOffset);

            sectionHeader.type = SH_TYPE.fromInt(sectionHeader.sh_type);

            // DEBUG
            logger.info(sectionHeader.toString());

            // https://wiki.osdev.org/ELF_Tutorial
            // The String Table
            // The string table conceptually is quite simple: it's just a number of
            // consecutive
            // zero-terminated strings. String literals used in the program are stored in
            // one of
            // the tables. There are a number of different string tables that may be present
            // in
            // an ELF object such as .strtab (the default string table), .shstrtab (the
            // section
            // string table) and .dynstr (string table for dynamic linking). Any time the
            // loading
            // process needs access to a string, it uses an offset into one of the string
            // tables.
            // The offset may point to the beginning of a zero-terminated string or
            // somewhere in
            // the middle or even to the zero terminator itself, depending on usage and
            // scenario.
            // The size of the string table itself is specified by sh_size in the
            // corresponding
            // section header entry. The simplest program loader may copy all string tables
            // into
            // memory, but a more complete solution would omit any that are not necessary
            // during
            // runtime such, notably those not flagged with SHF_ALLOC in their respective
            // section
            // header (such as .shstrtab, since section names aren't used in program
            // runtime).

            // load the string table.
            // The String table is a large string that is index by symbols in the symbol
            // table.
            //
            // How indexing works is by treating the strtab as one large string and then
            // index to a individual character within this large string. The index is stored
            // in the st_name field of a symbol in the symbol table. The substring is then
            // constructed up to the next zero-terminator.
            //
            // It is incorrect to split the symbol table into individual strings and put
            // then
            // into a hashmap! This is not how the indexing works! The indexes are pointing
            // at substrings as described above!
            if (sectionHeader.type == SH_TYPE.SHT_STRTAB) {

                // the entries of the section are stored at sh_offset
                long sectionEntryOffset = sectionHeader.sh_offset;

                long size = sectionHeader.sh_size;
                // System.out.println(size);

                String strtab = new String(buffer, (int) sectionEntryOffset, (int) size);

                // // DEBUG
                // String temp = strtab;
                // temp = temp.replaceAll("\0", "\n");
                // System.out.println(temp);

                ElfSymbolTable symbolTable = new ElfSymbolTable();
                symbolTableList.add(symbolTable);
                symbolTable.strtab = strtab;
            }

            // symbols which have a reference to the string tables
            if (sectionHeader.type == SH_TYPE.SHT_SYMTAB) {

                // the entries of the section are stored at sh_offset
                long sectionEntryOffset = sectionHeader.sh_offset;
                // System.out.println(ByteArrayUtil.byteToHex(sectionEntryOffset));

                long sectionEntries = sectionHeader.sh_size / sectionHeader.sh_entsize;

                long size = sectionHeader.sh_size;

                @SuppressWarnings("unused")
                String result = new String(buffer, (int) sectionEntryOffset, (int) size);

                // // DEBUG
                // String temp = result;
                // temp = temp.replaceAll("\0", "\n");
                // System.out.println(temp);

                for (int j = 0; j < sectionEntries; j++) {

                    Elf64Sym elf64_Sym = new Elf64Sym();
                    elf64SymList.add(elf64_Sym);
                    elf64_Sym.load(buffer, (int) sectionEntryOffset);

                    logger.info(elf64_Sym.toString());

                    sectionEntryOffset += Elf64Sym.SIZE;
                }

            }
        }

        getProgramHeaderOffset(elf64_Ehdr, sectionHeaders);
    }

    private void getProgramHeaderOffset(Elf64_Ehdr elf64_Ehdr, List<Elf64_Shdr> sectionHeaders) {

        Elf64_Shdr dataSectionHeader = null;
        Elf64_Shdr sdataSectionHeader = null;

        for (Elf64_Shdr sectionHeader : sectionHeaders) {

            if (sectionHeader.sh_name != 0) {

                String val = symbolTableList.get(1).strtab;

                // start with the index and construct a string of all
                // characters up to the next null-terminator character
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = sectionHeader.sh_name; i < val.length(); i++) {
                    char c = val.charAt(i);
                    if (c == '\0') {
                        break;
                    }
                    stringBuilder.append(c);
                }
                // System.out.println(stringBuilder.toString());

                sectionHeader.resolved_st_name = stringBuilder.toString();

                // DEBUG
                logger.info("------------------------------------------");
                logger.info("Name: " + sectionHeader.resolved_st_name);
                logger.info("Type: " + sectionHeader.type);
                logger.info("Size: " + sectionHeader.sh_size);
                logger.info("Offset: " + ByteArrayUtil.byteToHex(sectionHeader.sh_offset));

                // store the .data section for the GP global pointer register
                if (".data".equalsIgnoreCase(sectionHeader.resolved_st_name)) {
                    dataSectionHeader = sectionHeader;
                }

                if (".sdata".equalsIgnoreCase(sectionHeader.resolved_st_name)) {
                    sdataSectionHeader = sectionHeader;
                }

            }
        }

        //
        // Set the GP (Global Pointer) register
        //
        // https://stackoverflow.com/questions/77019070/what-is-the-gp-register-set-to-if-there-is-no-data-section-in-an-elf-executable
        //
        // The GP register is usually set to the middle of a 4K window that begins at
        // .sdata,
        // so the linker can relax accesses to global symbols within that window. See
        // this sifive
        // post and Liviu Ionescu's write up
        // https://www.sifive.com/blog/all-aboard-part-3-linker-relaxation-in-riscv-toolchain
        // https://gnu-mcu-eclipse.github.io/arch/riscv/programmer/#the-gp-global-pointer-register
        //
        if (dataSectionHeader != null) {
            globalPointerValue = dataSectionHeader.sh_offset;
        } else if (sdataSectionHeader != null) {
            globalPointerValue = sdataSectionHeader.sh_offset + 0x1000 / 2;
        } else {
            // throw new RuntimeException("No global offset pointer value retrieved!");
            logger.warn("No global offset pointer value retrieved!");
            globalPointerValue = 0;
        }

        // symbols in the symbol table
        for (Elf64Sym elf64_Sym : elf64SymList) {

            // DEBUG
            logger.info(elf64_Sym.toString());

            if (elf64_Sym.st_name != 0) {

                String val = symbolTableList.get(0).strtab;

                // start with the index and construct a string of all
                // characters up to the next null-terminator character
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = elf64_Sym.st_name; i < val.length(); i++) {
                    char c = val.charAt(i);
                    if (c == '\0') {
                        break;
                    }
                    stringBuilder.append(c);
                }
                // System.out.println(stringBuilder.toString());

                elf64_Sym.resolved_st_name = stringBuilder.toString();
            }
        }

        long programHeaderOffset = elf64_Ehdr.e_phoff;
        for (int programHeaderI = 0; programHeaderI < elf64_Ehdr.e_phnum; programHeaderI++) {

            // Iterate over each 'program header' in the table and check
            // - the p_flags field for PF_X (executable) If it has this flag, then the
            // segment contains executable code.
            // - p_type == PT_LOAD (loadable)

            programHeader = new Elf64_Phdr();
            programHeader.load(buffer, (int) programHeaderOffset);

            // the program header is checked it if has the PL_LOAD type and if it has
            // the executable flag
            if (programHeader.p_type != SEGMENT_TYPE.PT_LOAD) {

                // throw new RuntimeException("Not a loadable program header!");
                System.out.println("Not a loadable program header!");

                // next program header
                programHeaderOffset += elf64_Ehdr.e_phentsize;

                continue;
            }

            // DO NOT CHECK THE EXECUTABLE FLAG; JUST LOAD LOADABLE PROGRAM HEADERS!
            // OTHERWISE THE RISC-V TESTSUITE ELF FILES WILL NOT RUN CORRECTLY
            if (NOT_LOAD_NON_EXECUTABLE_PROGRAM_HEADERS && (programHeader.p_flags & Elf64_Ehdr.PF_X) == 0) {

                // throw new RuntimeException("Not an executable program header!");
                System.out.println("Not an executable program header!");

                // next program header
                programHeaderOffset += elf64_Ehdr.e_phentsize;

                continue;
            }

            // compute offset to machine code
            long machine_code_offset = programHeader.p_offset;
            // if the offset in the loadable, executable header is 0, this means
            // it points onto the ELF-Header which is not executable. In this
            // case, add the entry-point offset from the ELF-header to point to
            // the machine code
            // if (machine_code_offset == 0) {
            // machine_code_offset += elf64_Ehdr.e_entry;

            // System.out.println("Loading machine code virtual address: " +
            // programHeader.p_vaddr);
            // machine_code_offset -= programHeader.p_vaddr;
            // }

            // create a machine_code buffer large enough to hold the application
            // plus additional space for the stack!
            // TODO: the stack and the application memory should not be related
            // to each other. Change how the stack pointer of the CPU is initialized: see
            // App.java
            // stack-pointer initialization
            // machine_code = new byte[programHeader.p_memsz + 10000];
            // System.arraycopy(buffer, machine_code_offset, machine_code, 0,
            // programHeader.p_memsz);

            System.out.println("-----------------");
            System.out.println("p_paddr: " + ByteArrayUtil.byteToHex(programHeader.p_paddr));
            System.out.println("machine_code_offset: " + ByteArrayUtil.byteToHex(machine_code_offset));
            System.out.println("p_memsz (size_in_bytes): " + ByteArrayUtil.byteToHex(programHeader.p_memsz));

            // memory.copy(programHeader.p_paddr, buffer, machine_code_offset,
            // programHeader.p_memsz);
            memory.copy(programHeader.p_paddr, buffer, machine_code_offset, programHeader.p_filesz);

            // DEBUG

            // fence.i
            // MemoryBlock tempMemoryBlock = memory.getMemoryBlockForAddress(0x80002000);
            // tempMemoryBlock.print(0x80000000, 0x80000290, ByteOrder.LITTLE_ENDIAN);
            // tempMemoryBlock.print(0x80002000, 0x8000201e, ByteOrder.LITTLE_ENDIAN);

            // // ori
            // MemoryBlock tempMemoryBlock = memory.getMemoryBlockForAddress(0x80000000);
            // tempMemoryBlock.print(0x80000000, 0x800003ba, ByteOrder.LITTLE_ENDIAN);

            // lb
            // MemoryBlock tempMemoryBlock = memory.getMemoryBlockForAddress(0x80000000);
            // tempMemoryBlock.print(0x80002000, 0x80002010, ByteOrder.LITTLE_ENDIAN);

            // tempMemoryBlock.print(0x80000000, 0x80000008);

            // DEBUG
            if (DECODE) {

                logger.info(
                        "Loading machine code from elf-file offset: " + ByteArrayUtil.byteToHex(machine_code_offset));

                DelegatingDecoder delegatingDecoder = new DelegatingDecoder();

                int instructionMachineCode = 0;
                try {

                    int decodePos = (int) machine_code_offset;

                    logger.info("Programheader memsize: " + ByteArrayUtil.byteToHex(programHeader.p_memsz) + " "
                            + programHeader.p_memsz);
                    logger.info("Programheader filesize: " + ByteArrayUtil.byteToHex(programHeader.p_filesz) + " "
                            + programHeader.p_filesz);
                    for (int i = 0; i < (programHeader.p_memsz / 4); i++) {

                        instructionMachineCode = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, decodePos);

                        List<AsmLine<?>> asmLines = delegatingDecoder.decode(instructionMachineCode);

                        for (AsmLine<?> asmLine : asmLines) {

                            // DEBUG
                            logger.info(ByteArrayUtil.byteToHex(decodePos) + ": " + asmLine);

                            decodePos += asmLine.encodedLength;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info(ByteArrayUtil.byteToHex(instructionMachineCode));
                }

            }

            // next program header
            programHeaderOffset += elf64_Ehdr.e_phentsize;

        }
    }

    public Optional<Elf64Sym> getSymbolFromSymbolTable(String symbolName) {
        return elf64SymList.stream().filter(x -> {
            if (x.resolved_st_name == null) {
                return false;
            }
            return x.resolved_st_name.equalsIgnoreCase(symbolName);
        }).findFirst();
    }
}
