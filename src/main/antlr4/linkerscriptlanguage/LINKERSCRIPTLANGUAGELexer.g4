lexer grammar LINKERSCRIPTLANGUAGELexer;

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

OPENING_BRACKET : '(' ;
CLOSING_BRACKET : ')' ;

OPENING_SQUIGGLY_BRACKET : '{' ;
CLOSING_SQUIGGLY_BRACKET : '}' ;

OPENING_RECTANGULAR_BRACKET : '[' ;
CLOSING_RECTANGULAR_BRACKET : ']' ;

SEMICOLON : ';' ;
COMMA : ',' ;

ASSIGN : '=' ;

COLON : ':' ;

AND : '&' ;
PLUS : '+' ;
MINUS : '-' ;
EXCLAMATION : '!' ;
TILDE : '~' ;
MULT : '*' ;
DIV : '/' ;
PERCENT : '%' ;
CIRC : '^' ;
OR : '|' ;
QUESTION : '?' ;

UNARY : 'UNARY' ;
NEXT : 'NEXT' ;
LSHIFT : '<<' ;
RSHIFT : '>>' ;
EQ : '==' ;
NE : '!=' ;
LE : '<=' ;
LT : '<' ;
GE : '>=' ;
GT : '>' ;
PLUSEQ : '+=' ;
MINUSEQ : '-=' ;
MULTEQ : '*=' ;
DIVEQ : '/=' ;
LSHIFTEQ : '<<=' ;
RSHIFTEQ : '>>=' ;
ANDEQ : '&=' ;
OREQ : '|=' ;
XOREQ : '^=' ;
ANDAND : '&&' ;
OROR : '||' ;
DEFINED : 'DEFINED' ;
SIZEOF_HEADERS : 'SIZEOF_HEADERS' ;
ALIGNOF : 'ALIGNOF' ;
SIZEOF : 'SIZEOF' ;
ADDR : 'ADDR' ;
LOADADDR : 'LOADADDR' ;
CONSTANT : 'CONSTANT' ;
ABSOLUTE : 'ABSOLUTE' ;
ALIGN_K : 'ALIGN_K' ;
DATA_SEGMENT_ALIGN : 'DATA_SEGMENT_ALIGN' ;
DATA_SEGMENT_RELRO_END : 'DATA_SEGMENT_RELRO_END' ;
DATA_SEGMENT_END : 'DATA_SEGMENT_END' ;
SEGMENT_START : 'SEGMENT_START' ;
BLOCK : 'BLOCK' ;
MAX_K : 'MAX_K' ;
MIN_K : 'MIN_K' ;
ASSERT_K : 'ASSERT_K' ;
ORIGIN : 'ORIGIN' ;
LENGTH : 'LENGTH' ;
LOG2CEIL : 'LOG2CEIL' ;
PROVIDE : 'PROVIDE' ;
PROVIDE_HIDDEN : 'PROVIDE_HIDDEN' ;
HIDDEN_ : 'HIDDEN' ;
END : 'END' ;
ENTRY : 'ENTRY' ;
NOLOAD : 'NOLOAD' ;
DSECT : 'DSECT' ;
COPY : 'COPY' ;
INFO : 'INFO' ;
OVERLAY : 'OVERLAY' ;
READONLY : 'READONLY' ;
TYPE : 'TYPE' ;
INPUT_SCRIPT : 'INPUT_SCRIPT' ;
SECTIONS : 'SECTIONS' ;
EXCLUDE_FILE : 'EXCLUDE_FILE' ;
REVERSE : 'REVERSE' ;
SORT_BY_NAME : 'SORT_BY_NAME' ;
SORT_NONE : 'SORT_NONE' ;
SORT_BY_ALIGNMENT : 'SORT_BY_ALIGNMENT' ;
SORT_BY_INIT_PRIORITY : 'SORT_BY_INIT_PRIORITY' ;
INPUT_SECTION_FLAGS : 'INPUT_SECTION_FLAGS' ;
KEEP : 'KEEP' ;
CREATE_OBJECT_SYMBOLS : 'CREATE_OBJECT_SYMBOLS' ;
CONSTRUCTORS : 'CONSTRUCTORS' ;
ASCIZ : 'ASCIZ' ;
FILL : 'FILL' ;
LINKER_VERSION : 'LINKER_VERSION' ;
INCLUDE : 'INCLUDE' ;
QUAD : 'QUAD' ;
SQUAD : 'SQUAD' ;
LONG : 'LONG' ;
SHORT : 'SHORT' ;
BYTE : 'BYTE' ;
AT : 'AT' ;
ALIGN_WITH_INPUT : 'ALIGN_WITH_INPUT' ;
SUBALIGN : 'SUBALIGN' ;
ONLY_IF_RO : 'ONLY_IF_RO' ;
ONLY_IF_RW : 'ONLY_IF_RW' ;
SPECIAL : 'SPECIAL' ;
GROUP : 'GROUP' ;
NOCROSSREFS : 'NOCROSSREFS' ;
PHDRS : 'PHDRS' ;
VERSIONK : 'VERSIONK' ;
VERS_TAG : 'VERS_TAG' ;
GLOBAL : 'GLOBAL' ;
LOCAL : 'LOCAL' ;
VERS_IDENTIFIER : 'VERS_IDENTIFIER' ;
EXTERN : 'EXTERN' ;
BIND : 'BIND' ;



NAME : '.'? [a-zA-Z0-9]+ ;

INT : '0' 'x' [a-fA-F0-9]+ ;

//WS : (' ' | '\r' | '\n' | '\t')+ -> channel(HIDDEN);
WS: [ \t\r\n]+ -> skip ;