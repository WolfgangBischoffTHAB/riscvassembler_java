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
  Elf64_Word st_name;     // Symbol name (index into string table) (4 byte)
  unsigned char st_info;  // Symbol's type and binding attributes (1 byte)
  unsigned char st_other; // Must be zero; reserved (1 byte)
  Elf64_Half st_shndx;    // Which section (header tbl index) it's defined in (2 byte)
  Elf64_Addr st_value;    // Value or address associated with the symbol (8 byte)
  Elf64_Xword st_size;    // Size of the symbol (8 byte)
};
*/
public class Elf64Sym {

    public static final long SIZE = 24;

    public int st_name; // (4 byte)
    public int st_info; // (1 byte)
    public int st_other; // (1 byte)
    public int st_shndx; // (2 byte)
    public long st_value; // (8 byte)
    public long st_size; // (8 byte)

    public String resolved_st_name;

    public void load(byte[] buffer, int pos) {

        // 4 byte
        st_name = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, pos += 0);
        // 1 byte
        st_info = ByteArrayUtil.decodeInt8FromArray(buffer, pos += 4);
        // 1 byte
        st_other = ByteArrayUtil.decodeInt8FromArray(buffer, pos += 1);
        // 2 byte
        st_shndx = ByteArrayUtil.decodeInt16FromArrayBigEndian(buffer, pos += 1);
        // 8 byte
        st_value = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, pos += 2);
        // 8 byte
        st_size = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, pos += 8);
    }

    public String toString() {

        // System.out.println((j+1) + ") +++++++++++++++++++++++++++++++++++");
        // System.out.println(" st_name: " + st_name);
        // System.out.println(" st_value: " + ByteArrayUtil.byteToHex(st_value));
        // System.out.println(" st_size: " + st_size);
        // System.out.println(" st_info: " + st_info);
        // System.out.println(" st_other: " + st_other);
        // System.out.println(" st_shndx: " + st_shndx);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("+++++++++++++++++++++++++++++++++++\n");
        stringBuilder.append("st_name: " + ByteArrayUtil.byteToHex(st_name) +
                " (" + st_name + ") \n");
        stringBuilder.append("st_info: " + st_info + "\n");
        stringBuilder.append("st_other: " + st_other + "\n");
        stringBuilder.append("st_shndx: " + ByteArrayUtil.byteToHex(st_shndx) + " (" + st_shndx + ")\n");
        stringBuilder.append("st_value: " +
                ByteArrayUtil.byteToHex(st_value) + "\n");
        stringBuilder.append("st_size: " + st_size + "\n");

        return stringBuilder.toString();

    }

}
