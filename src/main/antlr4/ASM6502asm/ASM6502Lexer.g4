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

I_ADC : A D C ;
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
I_CLC : C L C ;

//I_ECALL : E C A L L ;

I_INC : I N C ;

//I_J : J ;
//I_JR : J R ;
//I_JAL : J A L ;
//I_JALR : J A L R ;

I_JMP : J M P ;

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
I_SEC : S E C ; 
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

// https://en.wikibooks.org/wiki/6502_Assembly
BIN_NUMERIC : '%' [0-1]+ ;
DEC_NUMERIC : '-'? [0-9]+ ;
HEX_NUMERIC : '$' [a-fA-F0-9]+ ;

IDENTIFIER : (('@')?DOT?('_')*[_0-9a-zA-Z]*[a-zA-Z]+)(DOT?('_')*[_0-9a-zA-Z]+)* ;

WS : [ \t\r]+ -> skip ;

STRING_LITERAL : UNTERMINATED_STRING_LITERAL '"' ;

UNTERMINATED_STRING_LITERAL : '"' (~["\\\r\n] | '\\' (. | EOF))* ;

NEWLINE : [\n] ;