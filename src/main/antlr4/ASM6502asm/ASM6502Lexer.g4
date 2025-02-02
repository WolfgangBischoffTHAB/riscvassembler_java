// $antlr-format alignTrailingComments true, columnLimit 150, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine true, allowShortBlocksOnASingleLine true, minEmptyLines 0, alignSemicolons ownLine
// $antlr-format alignColons trailing, singleLineOverrulesHangingColon true, alignLexerCommands true, alignLabels true, alignTrailers true

lexer grammar ASM6502Lexer;

LINE_COMMENT
  : ( ';' ) ~[\r\n]* -> channel(HIDDEN)
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
DOT_ASCIIZ : DOT A S C I I Z ; // ASCII string with zero termination
DOT_SKIP : DOT S K I P ;
DOT_STRING : DOT S T R I N G ;
DOT_OPTION : DOT O P T I O N ;
DOT_SIZE : DOT S I Z E ;
DOT_IDENT : DOT I D E N T ;

I_AND : A N D ;
//I_ADD : A D D ;
//I_ADDI : A D D I ;
//I_ADDIU : A D D I U ;
//I_AND : A N D ;
//I_ANDI : A N D I ;
//I_AUIPC : A U I P C ;

//I_BEQ : B E Q ;
//I_BEQZ : B E Q Z ;
//I_BGE : B G E ;
//I_BGT : B G T ;
//I_BLT : B L T ;
//I_BLE : B L E ;
//I_BNE : B N E ;
//I_BNEZ : B N E Z ;

//I_CALL : C A L L ;
//I_ECALL : E C A L L ;

//I_J : J ;
//I_JR : J R ;
//I_JAL : J A L ;
//I_JALR : J A L R ;

//I_LA : L A ;
//I_LD : L D ;
I_LDA : L D A ;
//I_LW : L W ;
//I_LH : L H ;
//I_LB : L B ;
//I_LBU : L B U ;
//I_LI : L I ;
//I_LUI : L U I ;

//I_MUL : M U L ;
//I_MV : M V ;

//I_NOP : N O P ;
//I_NOT : N O T ;

//I_OR : O R ;

//I_RET : R E T ;

I_STA : S T A ;
//I_SLT : S L T ;
//I_SRAI : S R A I ;
//I_SRLI : S R L I ;
//I_SLLI : S L L I ;
//I_SUB : S U B ;
//I_SD : S D ;
//I_SW : S W ;
//I_SH : S H ;
//I_SB : S B ;
//I_SYSCALL : S Y S C A L L ;

//I_WFI : W F I ;

//I_XORI : X O R I ;

//
// Registers
//

REG_ZERO_ABI : DOLLAR Z E R O ; // 0

REG_AT_ABI : DOLLAR A T ; // 1

REG_V0_ABI : DOLLAR V '0' ; // 2
REG_V1_ABI : DOLLAR V '1' ; // 3

REG_A0_ABI : DOLLAR A '0' ; // 4
REG_A1_ABI : DOLLAR A '1' ; // 5
REG_A2_ABI : DOLLAR A '2' ; // 6
REG_A3_ABI : DOLLAR A '3' ; // 7

REG_T0_ABI : DOLLAR T '0' ; // 8
REG_T1_ABI : DOLLAR T '1' ;
REG_T2_ABI : DOLLAR T '2' ;
REG_T3_ABI : DOLLAR T '3' ;
REG_T4_ABI : DOLLAR T '4' ;
REG_T5_ABI : DOLLAR T '5' ;
REG_T6_ABI : DOLLAR T '6' ;
REG_T7_ABI : DOLLAR T '7' ; // 15

REG_S0_ABI : DOLLAR S '0' ; // 16
REG_S1_ABI : DOLLAR S '1' ;
REG_S2_ABI : DOLLAR S '2' ;
REG_S3_ABI : DOLLAR S '3' ;
REG_S4_ABI : DOLLAR S '4' ;
REG_S5_ABI : DOLLAR S '5' ;
REG_S6_ABI : DOLLAR S '6' ;
REG_S7_ABI : DOLLAR S '7' ; // 23

REG_T8_ABI : DOLLAR T '8' ; // 24
REG_T9_ABI : DOLLAR T '9' ; // 25

REG_K0_ABI : DOLLAR K '0' ; // 26 // reserved for use by the interrupt/trap handler
REG_K1_ABI : DOLLAR K '1' ; // 27 // reserved for use by the interrupt/trap handler

REG_GP_ABI : DOLLAR G P ; // 28 // global pointer. Points to the middle of the 64K block of memory in the static data segment.
REG_SP_ABI : DOLLAR S P ; // 29 // stack pointer. Points to last location on the stack.
REG_FP_ABI : DOLLAR F P ; // 30 // saved value / frame pointer. Preserved across procedure calls
REG_RA_ABI : DOLLAR R A ; // 31 // return address

//
// register names without ABI (does not exist for MIPS only for RISCV)
//

//REG_ZERO : DOLLAR X '0' ; // 0, Hard-wired zero
//REG_RA   : DOLLAR X '1' ; // 1, Return address
//REG_SP   : DOLLAR X '2' ; // 2, Stack pointer
//REG_GP   : DOLLAR X '3' ; // 3, Global pointer
//REG_TP   : DOLLAR X '4' ; // 4, Thread pointer
//
//REG_T0  : DOLLAR X '5' ; // 5, Temporary/alternate link register
//REG_T1  : DOLLAR X '6' ; // 6, Temporary
//REG_T2  : DOLLAR X '7' ; // 7, Temporary
//
//REG_S0  : DOLLAR X '8' ; // 8, Saved register/frame pointer
//REG_S1  : DOLLAR X '9' ; // 9, Saved register
//
//REG_A0  : DOLLAR X '1' '0' ; // 10, Function arguments/return values
//REG_A1  : DOLLAR X '1' '1' ; // 11, Function arguments/return values
//
//REG_A2  : DOLLAR X '1' '2' ; // 12, Function arguments
//REG_A3  : DOLLAR X '1' '3' ; // 13, Function arguments
//REG_A4  : DOLLAR X '1' '4' ; // 14, Function arguments
//REG_A5  : DOLLAR X '1' '5' ; // 15, Function arguments
//REG_A6  : DOLLAR X '1' '6' ; // 16, Function arguments
//REG_A7  : DOLLAR X '1' '7' ; // 17, Function arguments
//
//REG_S2  : DOLLAR X '1' '8' ; // 18, Saved registers
//REG_S3  : DOLLAR X '1' '9' ; // 19, Saved registers
//REG_S4  : DOLLAR X '2' '0' ; // 20, Saved registers
//REG_S5  : DOLLAR X '2' '1' ; // 21, Saved registers
//REG_S6  : DOLLAR X '2' '2' ; // 22, Saved registers
//REG_S7  : DOLLAR X '2' '3' ; // 23, Saved registers
//REG_S8  : DOLLAR X '2' '4' ; // 24, Saved registers
//REG_S9  : DOLLAR X '2' '5' ; // 25, Saved registers
//REG_S10 : DOLLAR X '2' '6' ; // 26, Saved registers
//REG_S11 : DOLLAR X '2' '7' ; // 27, Saved registers
//
//REG_T3 : DOLLAR X '2' '8' ; // 28, Temporary
//REG_T4 : DOLLAR X '2' '9' ; // 29, Temporary
//REG_T5 : DOLLAR X '3' '0' ; // 30, Temporary
//REG_T6 : DOLLAR X '3' '1' ; // 31, Temporary

ASTERISK : '*' ;
PLUS : '+' ;
MINUS : '-' ;
PERCENT : '%' ;
DOT : '.' ;
DOLLAR : '$' ;
COLON : ':' ;
COMMA : ',' ;
OPENING_BRACKET : '(' ;
CLOSING_BRACKET : ')' ;
HASH : '#' ;

BIN_NUMERIC : '%' [0-1]+ ;
DEC_NUMERIC : '-'? [0-9]+ ;
HEX_NUMERIC : '$' [a-fA-F0-9]+ ;

IDENTIFIER : (('@')?DOT?('_')*[_0-9a-zA-Z]*[a-zA-Z]+)(DOT?('_')*[_0-9a-zA-Z]+)* ;

WS : [ \t\r]+ -> skip ;

STRING_LITERAL : UNTERMINATED_STRING_LITERAL '"' ;

UNTERMINATED_STRING_LITERAL : '"' (~["\\\r\n] | '\\' (. | EOF))* ;

NEWLINE : [\n] ;