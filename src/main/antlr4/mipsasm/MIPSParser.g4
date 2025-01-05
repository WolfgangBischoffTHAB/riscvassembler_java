// $antlr-format alignTrailingComments true, columnLimit 150, minEmptyLines 1, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine false, allowShortBlocksOnASingleLine true, alignSemicolons hanging, alignColons hanging

parser grammar MIPSParser;

options {
    language = Java;
    tokenVocab = MIPSLexer;
}

asm_file :
    (
        ( asm_line NEWLINE )
        |
        ( NEWLINE )
    )*
    (
        asm_line
        |
        ( NEWLINE )*
    )
    ;

asm_line :
    ( label COLON )?
    (
        mnemonic params
        |
        assembler_instruction
    )?
    ;

label :
    IDENTIFIER
    |
    NUMERIC
    ;

mnemonic :
    I_ADD | I_ADDI | I_ADDIU | I_AND | I_ANDI | I_AUIPC |
    I_BEQ | I_BEQZ | I_BGE | I_BGT | I_BLE | I_BLT | I_BNE | I_BNEZ |
    I_CALL | I_ECALL |
    I_J | I_JR | I_JAL | I_JALR |
    I_LA | I_LD | I_LW | I_LH | I_LB | I_LBU | I_LI | I_LUI |
    I_MUL | I_MV |
    I_NOP | I_NOT |
    I_OR |
    I_RET |
    I_SLT | I_SRAI | I_SRLI | I_SLLI | I_SUB | I_SD | I_SW | I_SH | I_SB | I_SYSCALL |
    I_WFI |
    I_XORI
    ;

params :
    | param COMMA param COMMA param COMMA? {
    }
    | param COMMA param COMMA? {
    }
    | param COMMA? {
    }
    ;

param :
    expr
    |
    offset ( OPENING_BRACKET expr CLOSING_BRACKET )?
    ;

offset :
    expr
    |
    modifier OPENING_BRACKET expr CLOSING_BRACKET
    ;

modifier :
    MODIFIER_HI | MODIFIER_LO | MODIFIER_PCREL_HI | MODIFIER_PCREL_LO
    ;

expr :
    expr PLUS expr
    |
    expr MINUS expr
    |
    expr ASTERISK expr
    |
    OPENING_BRACKET expr CLOSING_BRACKET
    |
    register
    |
    NUMERIC | HEX_NUMERIC
    |
    IDENTIFIER
    |
    STRING_LITERAL
    |
    DOT
    ;

register :
    REG_ZERO_ABI |

    REG_AT_ABI |

    REG_V0_ABI |
    REG_V1_ABI |

    REG_A0_ABI |
    REG_A1_ABI |
    REG_A2_ABI |
    REG_A3_ABI |

    REG_T0_ABI |
    REG_T1_ABI |
    REG_T2_ABI |
    REG_T3_ABI |
    REG_T4_ABI |
    REG_T5_ABI |
    REG_T6_ABI |
    REG_T7_ABI |

    REG_S0_ABI |
    REG_S1_ABI |
    REG_S2_ABI |
    REG_S3_ABI |
    REG_S4_ABI |
    REG_S5_ABI |
    REG_S6_ABI |
    REG_S7_ABI |

    REG_T8_ABI |
    REG_T9_ABI |

    REG_K0_ABI |
    REG_K1_ABI |

    REG_GP_ABI |
    REG_SP_ABI |
    REG_FP_ABI |
    REG_RA_ABI
    ;

assembler_instruction :
    attribute_assembler_instruction
    |
    align_assembler_instruction
    |
    equ_assembler_instruction
    |
    extern_assembler_instruction
//    |
//    section_text_assembler_instruction
//    |
//    section_rodata_assembler_instruction
    |
    section_definition_assembler_instruction
    |
    globl_assembler_instruction
    |
    global_assembler_instruction
    |
    text_assembler_instruction
    |
    type_assembler_instruction
    |
    data_assembler_instruction
    |
    byte_assembler_instruction
    |
    space_assembler_instruction
    |
    half_assembler_instruction
    |
    word_assembler_instruction
    |
    weak_assembler_instruction
    |
    dword_assembler_instruction
    |
    file_assembler_instruction
    |
    skip_assembler_instruction
    |
    ascii_assembler_instruction
    |
    asciz_assembler_instruction
    |
    asciiz_assembler_instruction
    |
    string_assembler_instruction
    |
    option_assembler_instruction
    |
    size_assembler_instruction
    |
    ident_assembler_instruction
    ;

attribute_assembler_instruction :
    DOT_ATTRIBUTE IDENTIFIER COMMA expr
    ;

align_assembler_instruction :
    DOT_ALIGN expr
    ;

equ_assembler_instruction :
    DOT_EQU IDENTIFIER COMMA expr
    ;

extern_assembler_instruction :
    DOT_EXTERN IDENTIFIER
    ;

//section_text_assembler_instruction :
//    DOT_SECTION DOT_TEXT
//    ;

//section_rodata_assembler_instruction :
//    DOT_SECTION DOT_RODATA
//    ;

section_definition_assembler_instruction :
    DOT_SECTION ( IDENTIFIER | DOT_RODATA |  DOT_TEXT )
    ;

globl_assembler_instruction :
    DOT_GLOBL csv_identifier_list
    ;

global_assembler_instruction :
    DOT_GLOBAL csv_identifier_list
    ;

text_assembler_instruction :
    DOT_TEXT
    ;

type_assembler_instruction :
    DOT_TYPE csv_identifier_list
    ;

data_assembler_instruction :
    DOT_DATA
    ;

byte_assembler_instruction :
    DOT_BYTE csv_numeric_list
    ;

space_assembler_instruction :
    DOT_SPACE csv_numeric_list
    ;

half_assembler_instruction :
    DOT_HALF csv_numeric_list
    ;

weak_assembler_instruction :
    DOT_WEAK IDENTIFIER
    ;

word_assembler_instruction :
    DOT_WORD csv_numeric_list
    ;

dword_assembler_instruction :
    DOT_DWORD csv_numeric_list
    ;

file_assembler_instruction :
    DOT_FILE expr
    ;

skip_assembler_instruction :
    DOT_SKIP expr
    ;

ascii_assembler_instruction :
    DOT_ASCII STRING_LITERAL
    ;

asciz_assembler_instruction :
    DOT_ASCIZ STRING_LITERAL
    ;

asciiz_assembler_instruction :
    DOT_ASCIIZ STRING_LITERAL
    ;

string_assembler_instruction :
    DOT_STRING STRING_LITERAL
    ;

option_assembler_instruction :
    DOT_OPTION IDENTIFIER
    ;

size_assembler_instruction :
    DOT_SIZE expr COMMA expr
    ;

ident_assembler_instruction :
    DOT_IDENT STRING_LITERAL
    ;

csv_identifier_list :
    IDENTIFIER COMMA csv_identifier_list
    |
    IDENTIFIER
    ;

csv_numeric_list :
    ( NUMERIC | HEX_NUMERIC ) COMMA csv_numeric_list
    |
    ( NUMERIC | HEX_NUMERIC )
    ;