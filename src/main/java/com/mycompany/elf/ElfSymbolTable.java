package com.mycompany.elf;

// // Symbol table entries for ELF32.
// struct Elf32_Sym {
//   Elf32_Word st_name;     // Symbol name (index into string table)
//   Elf32_Addr st_value;    // Value or address associated with the symbol
//   Elf32_Word st_size;     // Size of the symbol
//   unsigned char st_info;  // Symbol's type and binding attributes
//   unsigned char st_other; // Must be zero; reserved
//   Elf32_Half st_shndx;    // Which section (header table index) it's defined in
// };

// // Symbol table entries for ELF64.
// struct Elf64_Sym {
//   Elf64_Word st_name;     // Symbol name (index into string table)
//   unsigned char st_info;  // Symbol's type and binding attributes
//   unsigned char st_other; // Must be zero; reserved
//   Elf64_Half st_shndx;    // Which section (header tbl index) it's defined in
//   Elf64_Addr st_value;    // Value or address associated with the symbol
//   Elf64_Xword st_size;    // Size of the symbol
// };
public class ElfSymbolTable {

    public String name;

    // public String[] stringArray = new String[0];

    public String strtab;

}
