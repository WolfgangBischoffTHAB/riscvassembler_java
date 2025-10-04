package com.mycompany.elf;

import com.mycompany.common.ByteArrayUtil;

/*
// Symbol table entries for ELF32.
struct Elf32_Sym {
  Elf32_Word st_name;     // Symbol name (index into string table)
  Elf32_Addr st_value;    // Value or address associated with the symbol
  Elf32_Word st_size;     // Size of the symbol
  unsigned char st_info;  // Symbol's type and binding attributes
  unsigned char st_other; // Must be zero; reserved
  Elf32_Half st_shndx;    // Which section (header table index) it's defined in
};
 
// Symbol table entries for ELF64.
struct Elf64_Sym {
  Elf64_Word st_name;     // Symbol name (index into string table)
  unsigned char st_info;  // Symbol's type and binding attributes
  unsigned char st_other; // Must be zero; reserved
  Elf64_Half st_shndx;    // Which section (header tbl index) it's defined in
  Elf64_Addr st_value;    // Value or address associated with the symbol
  Elf64_Xword st_size;    // Size of the symbol
};
*/

public class Elf32Sym {

    public int st_name;
    public int st_value;
    public int st_size;
    public int st_info;
    public int st_other;
    public int st_shndx;
    public String resolved_st_name;

    public void load(byte[] buffer, int pos) {
        st_name = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos + 0);
        st_value = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos + 4);
        st_size = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos + 8);
        st_info = ByteArrayUtil.decodeInt8FromArray(buffer, pos + 12);
        st_other = ByteArrayUtil.decodeInt8FromArray(buffer, pos + 13);
        st_shndx = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos + 14);
    }

}
