// $antlr-format alignTrailingComments true, columnLimit 150, minEmptyLines 1, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine false, allowShortBlocksOnASingleLine true, alignSemicolons hanging, alignColons hanging

parser grammar RISCVASMParser;

options {
    language = Java;
    tokenVocab = RISCVASMLexer;
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
    ( label COLON )? mnemonic params
    ;

label :
    IDENTIFIER
    ;

mnemonic :
    I_ADDI
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
    expr OPENING_BRACKET expr CLOSING_BRACKET
    ;

expr :
    register
    ;

register :
    REG_ZERO_ABI |
    REG_RA_ABI |
    REG_SP_ABI |
    REG_GP_ABI |
    REG_TP_ABI |

    REG_T0_ABI |
    REG_T1_ABI |
    REG_T2_ABI |
    REG_T3_ABI |
    REG_T4_ABI |
    REG_T5_ABI |
    REG_T6_ABI |

    REG_FP_ABI |

    REG_A0_ABI |
    REG_A1_ABI |
    REG_A2_ABI |
    REG_A3_ABI |
    REG_A4_ABI |
    REG_A5_ABI |
    REG_A6_ABI |
    REG_A7_ABI |

    REG_S0_ABI |
    REG_S1_ABI |
    REG_S2_ABI |
    REG_S3_ABI |
    REG_S4_ABI |
    REG_S5_ABI |
    REG_S6_ABI |
    REG_S7_ABI |
    REG_S8_ABI |
    REG_S9_ABI |
    REG_S10_ABI |
    REG_S11_ABI |

    REG_ZERO |
    REG_RA |
    REG_SP |
    REG_GP |
    REG_TP |

    REG_T0 |
    REG_T1 |
    REG_T2 |

    REG_S0 |
    REG_S1 |

    REG_A0 |
    REG_A1 |

    REG_A2 |
    REG_A3 |
    REG_A4 |
    REG_A5 |
    REG_A6 |
    REG_A7 |

    REG_S2 |
    REG_S3 |
    REG_S4 |
    REG_S5 |
    REG_S6 |
    REG_S7 |
    REG_S8 |
    REG_S9 |
    REG_S10 |
    REG_S11 |

    REG_T3 |
    REG_T4 |
    REG_T5 |
    REG_T6
    ;
