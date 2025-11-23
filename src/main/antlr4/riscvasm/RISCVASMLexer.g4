// $antlr-format alignTrailingComments true, columnLimit 150, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine true, allowShortBlocksOnASingleLine true, minEmptyLines 0, alignSemicolons ownLine
// $antlr-format alignColons trailing, singleLineOverrulesHangingColon true, alignLexerCommands true, alignLabels true, alignTrailers true

// start symbol: asm_file

lexer grammar RISCVASMLexer;

LINE_COMMENT
  : ( '#' | ';' | '/''/' ) ~[\r\n]* -> channel(HIDDEN)
  ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;

fragment A : [aA]; // match either an 'a' or 'A'
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

MODIFIER_HI : '%' H I ;
MODIFIER_LO : '%' L O ;
MODIFIER_PCREL_HI : '%' P C R E L '_' H I ;
MODIFIER_PCREL_LO : '%' P C R E L '_' L O ;

DOT_ATTRIBUTE : DOT A T T R I B U T E ;
DOT_ALIGN : DOT A L I G N ;
DOT_EQU : DOT E Q U ;
DOT_EXTERN : DOT E X T E R N ;
DOT_SECTION : DOT S E C T I O N ;
DOT_GLOBL : DOT G L O B L ;
DOT_GLOBAL : DOT G L O B A L ;
DOT_TEXT : DOT T E X T ;
DOT_TYPE : DOT T Y P E ;
DOT_DATA : DOT D A T A ;
DOT_BYTE : DOT B Y T E ;
DOT_SPACE : DOT S P A C E ;
DOT_HALF : DOT H A L F ;
DOT_WEAK : DOT W E A K ;
DOT_WORD : DOT W O R D ;
DOT_DWORD : DOT D W O R D ;
DOT_FILE : DOT F I L E ;
DOT_RODATA : DOT R O D A T A ;
DOT_ASCII : DOT A S C I I ; // ASCII string without zero termination
DOT_ASCIZ : DOT A S C I Z ; // ASCII string with zero termination
DOT_SKIP : DOT S K I P ;
DOT_STRING : DOT S T R I N G ;
DOT_OPTION : DOT O P T I O N ;
DOT_SIZE : DOT S I Z E ;
DOT_IDENT : DOT I D E N T ;
DOT_QUAD : DOT Q U A D ;
DOT_ZERO : DOT Z E R O ;

//
// CUSTOM these instructions do not exist in the RISC-V Spec!
//

I_BRK : B R K ;                                 // custom break point instruction 0x1f1f1f1f
I_PRINT_REG : P R I N T UNDERSCORE R E G ;      // print a register

//
// RV32 I
//

I_ADD : A D D ;
I_ADDI : A D D I ;
I_AND : A N D ;
I_ANDI : A N D I ;
I_AUIPC : A U I P C ;

I_BEQ : B E Q ;
I_BEQZ : B E Q Z ;
I_BGE : B G E ;
I_BGT : B G T ;
I_BGTU : B G T U ;
I_BLT : B L T ;
I_BLTU : B L T U ;
I_BLE : B L E ;
I_BLEU : B L E U ;
I_BNE : B N E ;
I_BNEZ : B N E Z ;

I_CALL : C A L L ;

I_ECALL : E C A L L ;
I_EBREAK : E B R E A K ;

I_FENCE : F E N C E ;

I_J : J ;
I_JR : J R ;
I_JAL : J A L ;
I_JALR : J A L R ;

I_LA : L A ;
// I_LD : L D ; // see RV64 extension section
I_LW : L W ;
I_LH : L H ;
I_LHU : L H U ;
I_LB : L B ;
I_LBU : L B U ;
I_LI : L I ;
I_LUI : L U I ;

I_MV : M V ;

I_NOP : N O P ;
I_NOT : N O T ;

I_OR : O R ;

I_RET : R E T ;

// shifts
I_SLL : S L L ;
I_SLLI : S L L I ;
I_SRL : S R L ;
I_SRLI : S R L I ;
I_SRA : S R A ;
I_SRAI : S R A I ;
// set flags
I_SLT : S L T ;
I_SLTI : S L T I ;
I_SLTU : S L T U ;
I_SLTIU : S L T I U ;
I_SEQZ : S E Q Z ;
I_SNEZ : S N E Z ;
I_SLTZ : S L T Z ;
I_SGTZ : S G T Z ;
// subtract
I_SUB : S U B ;
// I_SD : S D ; // see RV64 extension section
I_SW : S W ;
I_SH : S H ;
I_SB : S B ;

I_WFI : W F I ;

I_XORI : X O R I ;
I_XOR : X O R ;

//
// RV64 I
//

I_ADDIW : A D D I W ;
I_ADDW : A D D W ;
I_LD : L D ;
I_SD : S D ;

//
// Zifencei Extension for Instruction-Fetch Fence, Version 2.0
//

I_FENCEI : F E N C E DOT I ;

//
// Zicsr Extension (Command and Status Registers, CSR)
//

// https://www.csl.cornell.edu/courses/ece6745/handouts/ece6745-tinyrv-isa.txt
// Read CSR
I_CSRR : C S R R ;      // pseudo instruction of CSRRS with rs1 = x0. Resolved to CSRRS rd, csr, x0
I_CSRRS : C S R R S ;   // CSRRS rd, csr, rs1
// Read CSR immediate
I_CSRWI : C S R W I ;   // pseudo instruction. Implemented by csrrwi x0, csr, imm
I_CSRRWI : C S R R W I ;
// Write CSR
I_CSRW : C S R W ;      // pseudo instruction. Implemented by csrrw x0, csr, rs
I_CSRRW : C S R R W ;
// Set Bits in CSR
I_CSRS : C S R S ;      // pseudo instruction. Implemented by csrrs x0, csr, rs
// Set Bits in CSR immediate
I_CSRSI : C S R S I ;   // pseudo instruction. Implemented by csrrsi x0, csr, imm
I_CSRRSI : C S R R S I ;
// Clear Bits in CSR
I_CSRC : C S R C ;      // pseudo instruction. Implemented by csrrc x0, csr, rs
I_CSRRC : C S R R C ;
// Clear bits in CSR, immediate
I_CSRCI : C S R C I ;   // pseudo instruction. Implemented by csrrci x0, csr, imm
I_CSRRCI : C S R R C I ;

//
// M-Extension (Integer Multiplication and Division)
//

I_MUL : M U L ;
I_MULH  : M U L H ;
I_MULHSU : M U L H S U ;
I_MULHU  : M U L H U ;
I_DIV : D I V ;
I_DIVU : D I V U ;
I_REM : R E M ;
I_REMU : R E M U ;

//
// V-Extension (Vector Extension, RVV)
//

I_VSETVLI : V S E T V L I ;
I_VSETVL : V S E T V L ;
I_VLE8_V : V L E '8' DOT V ;
I_VLE16_V : V L E '1' '6' DOT V ;
I_VLE32_V : V L E '3' '2' DOT V ;
I_VLE64_V : V L E '6' '4' DOT V ;
I_VMSNE_VI : V M S N E DOT V I ;
I_VADD_VV : V A D D DOT V V ;
I_VAADD_VV : V A A D D DOT V V ;
I_VSE8_V : V S E '8' DOT V ;
I_VSE16_V : V S E '1' '6' DOT V ;
I_VSE32_V : V S E '3' '2' DOT V ;
I_VSE64_V : V S E '6' '4' DOT V ;
I_VMV_V_I : V M V DOT V DOT I ;

REG_ZERO_ABI : Z E R O ;
REG_RA_ABI : R A ;
REG_SP_ABI : S P ;
REG_GP_ABI : G P ;
REG_TP_ABI : T P ;

REG_T0_ABI : T '0' ;
REG_T1_ABI : T '1' ;
REG_T2_ABI : T '2' ;
REG_T3_ABI : T '3' ;
REG_T4_ABI : T '4' ;
REG_T5_ABI : T '5' ;
REG_T6_ABI : T '6' ;

REG_FP_ABI : F P ;

REG_A0_ABI : A '0' ;
REG_A1_ABI : A '1' ;
REG_A2_ABI : A '2' ;
REG_A3_ABI : A '3' ;
REG_A4_ABI : A '4' ;
REG_A5_ABI : A '5' ;
REG_A6_ABI : A '6' ;
REG_A7_ABI : A '7' ;

REG_S0_ABI : S '0' ;
REG_S1_ABI : S '1' ;
REG_S2_ABI : S '2' ;
REG_S3_ABI : S '3' ;
REG_S4_ABI : S '4' ;
REG_S5_ABI : S '5' ;
REG_S6_ABI : S '6' ;
REG_S7_ABI : S '7' ;
REG_S8_ABI : S '8' ;
REG_S9_ABI : S '9' ;
REG_S10_ABI : S '1' '0' ;
REG_S11_ABI : S '1' '1' ;

REG_ZERO : (X | DOLLAR) '0' ; // 0, Hard-wired zero
REG_RA   : (X | DOLLAR) '1' ; // 1, Return address
REG_SP   : (X | DOLLAR) '2' ; // 2, Stack pointer
REG_GP   : (X | DOLLAR) '3' ; // 3, Global pointer
REG_TP   : (X | DOLLAR) '4' ; // 4, Thread pointer

REG_T0  : (X | DOLLAR) '5' ; // 5, Temporary/alternate link register
REG_T1  : (X | DOLLAR) '6' ; // 6, Temporary
REG_T2  : (X | DOLLAR) '7' ; // 7, Temporary

REG_S0  : (X | DOLLAR) '8' ; // 8, Saved register/frame pointer
REG_S1  : (X | DOLLAR) '9' ; // 9, Saved register

REG_A0  : (X | DOLLAR) '1' '0' ; // 10, Function arguments/return values
REG_A1  : (X | DOLLAR) '1' '1' ; // 11, Function arguments/return values

REG_A2  : (X | DOLLAR) '1' '2' ; // 12, Function arguments
REG_A3  : (X | DOLLAR) '1' '3' ; // 13, Function arguments
REG_A4  : (X | DOLLAR) '1' '4' ; // 14, Function arguments
REG_A5  : (X | DOLLAR) '1' '5' ; // 15, Function arguments
REG_A6  : (X | DOLLAR) '1' '6' ; // 16, Function arguments
REG_A7  : (X | DOLLAR) '1' '7' ; // 17, Function arguments

REG_S2  : (X | DOLLAR) '1' '8' ; // 18, Saved registers
REG_S3  : (X | DOLLAR) '1' '9' ; // 19, Saved registers
REG_S4  : (X | DOLLAR) '2' '0' ; // 20, Saved registers
REG_S5  : (X | DOLLAR) '2' '1' ; // 21, Saved registers
REG_S6  : (X | DOLLAR) '2' '2' ; // 22, Saved registers
REG_S7  : (X | DOLLAR) '2' '3' ; // 23, Saved registers
REG_S8  : (X | DOLLAR) '2' '4' ; // 24, Saved registers
REG_S9  : (X | DOLLAR) '2' '5' ; // 25, Saved registers
REG_S10 : (X | DOLLAR) '2' '6' ; // 26, Saved registers
REG_S11 : (X | DOLLAR) '2' '7' ; // 27, Saved registers

REG_T3 : (X | DOLLAR) '2' '8' ; // 28, Temporary
REG_T4 : (X | DOLLAR) '2' '9' ; // 29, Temporary
REG_T5 : (X | DOLLAR) '3' '0' ; // 30, Temporary
REG_T6 : (X | DOLLAR) '3' '1' ; // 31, Temporary

//
// RVV
//

REG_V0 : V '0' ;
REG_V0_T : V '0' DOT T ;
REG_V1 : V '1' ;
REG_V1_T : V '1' DOT T ;
REG_V2 : V '2' ;
REG_V2_T : V '2' DOT T ;

// Selected element width (SEW) setting
RVV_SEW_E8 : E '8' ;
RVV_SEW_E16 : E '1' '6' ;
RVV_SEW_E32 : E '3' '2' ;
RVV_SEW_E64 : E '6' '4' ;

// RVV LMUL
RVV_LMUL_MF8 : M F '8' ;
RVV_LMUL_MF4 : M F '4' ;
RVV_LMUL_MF2 : M F '2' ;
RVV_LMUL_M1 : M '1' ;
RVV_LMUL_M2 : M '2' ;
RVV_LMUL_M4 : M '4' ;
RVV_LMUL_M8 : M '8' ;

// RVV tail
RVV_TAIL_TA : T A ;
RVV_TAIL_TU : T U ;
RVV_TAIL_MA : M A ;
RVV_TAIL_MU : M U ;

ASTERISK : '*' ;
PLUS : '+' ;
MINUS : '-' ;
UNDERSCORE : '_' ;
PERCENT : '%' ;
DOT : '.' ;
DOLLAR : '$' ;
COLON : ':' ;
COMMA : ',' ;
OPENING_BRACKET : '(' ;
CLOSING_BRACKET : ')' ;

NUMERIC : '-'? [0-9]+ ;
HEX_NUMERIC : '0' 'x' [a-fA-F0-9]+ ;

IDENTIFIER : (('@')?DOT?('_')*[_0-9a-zA-Z]*[a-zA-Z]+)(DOT?('_')*[_0-9a-zA-Z$]+)* ;

WS : [ \t\r]+ -> skip ;

STRING_LITERAL : UNTERMINATED_STRING_LITERAL '"' ;

UNTERMINATED_STRING_LITERAL : '"' (~["\\\r\n] | '\\' (. | EOF))* ;

NEWLINE : [\n] ;