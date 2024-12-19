// $antlr-format alignTrailingComments true, columnLimit 150, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine true, allowShortBlocksOnASingleLine true, minEmptyLines 0, alignSemicolons ownLine
// $antlr-format alignColons trailing, singleLineOverrulesHangingColon true, alignLexerCommands true, alignLabels true, alignTrailers true

lexer grammar RISCVASMLexer;

LINE_COMMENT
  : ( '#' | ';' ) ~[\r\n]* -> channel(HIDDEN)
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

I_ADD : A D D ;
I_ADDI : A D D I ;
I_AND : A N D ;
I_ANDI : A N D I ;
I_AUIPC : A U I P C ;

I_BEQ : B E Q ;
I_BEQZ : B E Q Z ;
I_BGE : B G E ;
I_BGT : B G T ;
I_BLT : B L T ;
I_BNE : B N E ;
I_BNEZ : B N E Z ;

I_CALL : C A L L ;
I_ECALL : E C A L L ;

I_J : J ;
I_JR : J R ;
I_JALR : J A L R ;

I_LA : L A ;
I_LD : L D ;
I_LW : L W ;
I_LH : L H ;
I_LB : L B ;
I_LBU : L B U ;
I_LI : L I ;
I_LUI : L U I ;

I_MUL : M U L ;
I_MV : M V ;

I_NOP : N O P ;
I_NOT : N O T ;

I_RET : R E T ;

I_SRLI : S R L I ;
I_SLLI : S L L I ;
I_SUB : S U B ;
I_SD : S D ;
I_SW : S W ;
I_SH : S H ;
I_SB : S B ;

I_WFI : W F I ;

I_XORI : X O R I ;

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

REG_ZERO : X '0' ; // 0, Hard-wired zero
REG_RA   : X '1' ; // 1, Return address
REG_SP   : X '2' ; // 2, Stack pointer
REG_GP   : X '3' ; // 3, Global pointer
REG_TP   : X '4' ; // 4, Thread pointer

REG_T0  : X '5' ; // 5, Temporary/alternate link register
REG_T1  : X '6' ; // 6, Temporary
REG_T2  : X '7' ; // 7, Temporary

REG_S0  : X '8' ; // 8, Saved register/frame pointer
REG_S1  : X '9' ; // 9, Saved register

REG_A0  : X '1' '0' ; // 10, Function arguments/return values
REG_A1  : X '1' '1' ; // 11, Function arguments/return values

REG_A2  : X '1' '2' ; // 12, Function arguments
REG_A3  : X '1' '3' ; // 13, Function arguments
REG_A4  : X '1' '4' ; // 14, Function arguments
REG_A5  : X '1' '5' ; // 15, Function arguments
REG_A6  : X '1' '6' ; // 16, Function arguments
REG_A7  : X '1' '7' ; // 17, Function arguments

REG_S2  : X '1' '8' ; // 18, Saved registers
REG_S3  : X '1' '9' ; // 19, Saved registers
REG_S4  : X '2' '0' ; // 20, Saved registers
REG_S5  : X '2' '1' ; // 21, Saved registers
REG_S6  : X '2' '2' ; // 22, Saved registers
REG_S7  : X '2' '3' ; // 23, Saved registers
REG_S8  : X '2' '4' ; // 24, Saved registers
REG_S9  : X '2' '5' ; // 25, Saved registers
REG_S10 : X '2' '6' ; // 26, Saved registers
REG_S11 : X '2' '7' ; // 27, Saved registers

REG_T3 : X '2' '8' ; // 28, Temporary
REG_T4 : X '2' '9' ; // 29, Temporary
REG_T5 : X '3' '0' ; // 30, Temporary
REG_T6 : X '3' '1' ; // 31, Temporary

ASTERISK : '*' ;
PLUS : '+' ;
PERCENT : '%' ;
DOT : '.' ;
COLON : ':' ;
COMMA : ',' ;
OPENING_BRACKET : '(' ;
CLOSING_BRACKET : ')' ;

NUMERIC : '-'? [0-9]+ ;
HEX_NUMERIC : '0' 'x' [a-fA-F0-9]+ ;

IDENTIFIER : (('@')?DOT?('_')*[_0-9a-zA-Z]*[a-zA-Z]+)(DOT?('_')*[_0-9a-zA-Z]+)* ;

WS : [ \t\r]+ -> skip ;

STRING_LITERAL : UNTERMINATED_STRING_LITERAL '"' ;

UNTERMINATED_STRING_LITERAL : '"' (~["\\\r\n] | '\\' (. | EOF))* ;

NEWLINE : [\n] ;