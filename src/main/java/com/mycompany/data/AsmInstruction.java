package com.mycompany.data;

public enum AsmInstruction {

    EQU,
    EXTERN,
    SECTION,
    GLOBAL,
    TEXT,
    TYPE,
    DATA,
    BYTE,
    SPACE,
    HALF,
    WEAK,
    WORD,
    DWORD,
    FILE,
    SKIP,
    ASCII,
    ASCIZ,
    ASCIIZ,
    STRING,
    OPTION,
    QUAD;

    public static String toString(final AsmInstruction asmInstruction) {

        switch (asmInstruction) {
            case EQU:
                return ".equ";
            case EXTERN:
                return ".extern";
            case SECTION:
                return ".section";
            case GLOBAL:
                return ".global";
            case TEXT:
                return ".text";
            case TYPE:
                return ".type";
            case DATA:
                return ".data";
            case BYTE:
                return ".byte";
            case SPACE:
                return ".space";
            case HALF:
                return ".half";
            case WEAK:
                return ".weak";
            case WORD:
                return ".word";
            case DWORD:
                return ".dword";
            case FILE:
                return ".file";
            case SKIP:
                return ".skip";
            case ASCII:
                return ".ascii";
            case ASCIZ:
                return ".asciz";
            case ASCIIZ:
                return ".asciiz";
            case STRING:
                return ".string";
            case OPTION:
                return ".option";
            case QUAD:
                return ".quad";

            default:
                throw new RuntimeException("Unknown AsmInstruction: \"" + asmInstruction + "\"");
        }
    }

}
