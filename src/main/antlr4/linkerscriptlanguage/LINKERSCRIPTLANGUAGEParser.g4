parser grammar LINKERSCRIPTLANGUAGEParser;

options {
    language = Java;
    tokenVocab = LINKERSCRIPTLANGUAGELexer;
}

program :
   script_file
//		INPUT_SCRIPT script_file
//	|	INPUT_MRI_SCRIPT mri_script_file
//	|	INPUT_VERSION_SCRIPT version_script_file
//	|
//  INPUT_SECTION_ORDERING_SCRIPT section_ordering_script_file
//	|	INPUT_DYNAMIC_LIST dynamic_list_file
//	|	INPUT_DEFSYM defsym_expr
	;

filename :
    NAME
    ;

script_file :
    ifile_list
    ;

ifile_list :
	ifile_list ifile_p1
	|
    ifile_p1
	;

ifile_p1 :
    statement_anywhere
    |
    memory_specs
    |
    sections
    ;

// https://sourceware.org/binutils/docs/ld/MEMORY.html
memory_specs :
    MEMORY OPENING_SQUIGGLY_BRACKET memory_spec+ CLOSING_SQUIGGLY_BRACKET
    ;

memory_spec :
    NAME memory_spec_attributes? COLON memory_spec_origin COMMA memory_spec_length
    ;

memory_spec_attributes :
    OPENING_BRACKET memory_spec_attribute+ CLOSING_BRACKET
    ;

memory_spec_attribute :
    NAME
    ;

memory_spec_origin :
    ORIGIN ASSIGN exp
    ;

memory_spec_length :
    LENGTH ASSIGN exp
    ;

sections :
		SECTIONS OPENING_SQUIGGLY_BRACKET sec_or_group_p1 CLOSING_SQUIGGLY_BRACKET
	;

sec_or_group_p1 :
	section sec_or_group_p1
	//|
    //    statement_anywhere //sec_or_group_p1
	|
    section
    //|
    //    statement_anywhere
	;

wildcard_name :
		NAME
//			{
//			  $$ = $1;
//			}
    |
    MULT
	;

wildcard_maybe_exclude :
	wildcard_name
//			{
//			  $$.name = $1;
//			  $$.sorted = none;
//			  $$.exclude_name_list = NULL;
//			  $$.section_flag_list = NULL;
//			  $$.reversed = false;
//			}
	|
    EXCLUDE_FILE OPENING_BRACKET exclude_name_list CLOSING_BRACKET wildcard_name
//			{
//			  $$.name = $5;
//			  $$.sorted = none;
//			  $$.exclude_name_list = $3;
//			  $$.section_flag_list = NULL;
//			  $$.reversed = false;
//			}
	;

wildcard_maybe_reverse :
	wildcard_maybe_exclude
	|
    REVERSE OPENING_BRACKET wildcard_maybe_exclude CLOSING_BRACKET
//			{
//			  $$ = $3;
//			  $$.reversed = true;
//			  $$.sorted = by_name;
//			}
	;

filename_spec :
	wildcard_maybe_reverse
	|
    SORT_BY_NAME OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET
//			{
//			  $$ = $3;
//			  $$.sorted = by_name;
//			}
	|	SORT_NONE OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET
//			{
//			  $$ = $3;
//			  $$.sorted = by_none;
//			  $$.reversed = false;
//			}
	|	REVERSE OPENING_BRACKET SORT_BY_NAME OPENING_BRACKET wildcard_maybe_exclude CLOSING_BRACKET CLOSING_BRACKET
//			{
//			  $$ = $5;
//			  $$.sorted = by_name;
//			  $$.reversed = true;
//			}
	;

section_name_spec :
	wildcard_maybe_reverse
	|
    SORT_BY_NAME OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET
//			{
//			  $$ = $3;
//			  $$.sorted = by_name;
//			}
	|	SORT_BY_ALIGNMENT OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET
//			{
//			  $$ = $3;
//			  $$.sorted = by_alignment;
//			}
	|	SORT_NONE OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET
//			{
//			  $$ = $3;
//			  $$.sorted = by_none;
//			}
	|	SORT_BY_NAME OPENING_BRACKET SORT_BY_ALIGNMENT OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET CLOSING_BRACKET
//			{
//			  $$ = $5;
//			  $$.sorted = by_name_alignment;
//			}
	|	SORT_BY_NAME OPENING_BRACKET SORT_BY_NAME OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET CLOSING_BRACKET
//			{
//			  $$ = $5;
//			  $$.sorted = by_name;
//			}
	|	SORT_BY_ALIGNMENT OPENING_BRACKET SORT_BY_NAME OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET CLOSING_BRACKET
//			{
//			  $$ = $5;
//			  $$.sorted = by_alignment_name;
//			}
	|	SORT_BY_ALIGNMENT OPENING_BRACKET SORT_BY_ALIGNMENT OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET CLOSING_BRACKET
//			{
//			  $$ = $5;
//			  $$.sorted = by_alignment;
//			}
	|	SORT_BY_INIT_PRIORITY OPENING_BRACKET wildcard_maybe_reverse CLOSING_BRACKET
//			{
//			  $$ = $3;
//			  $$.sorted = by_init_priority;
//			}
	|	REVERSE OPENING_BRACKET SORT_BY_NAME OPENING_BRACKET wildcard_maybe_exclude CLOSING_BRACKET CLOSING_BRACKET
//			{
//			  $$ = $5;
//			  $$.sorted = by_name;
//			  $$.reversed = true;
//			}
	|	REVERSE OPENING_BRACKET SORT_BY_INIT_PRIORITY OPENING_BRACKET wildcard_maybe_exclude CLOSING_BRACKET CLOSING_BRACKET
//			{
//			  $$ = $5;
//			  $$.sorted = by_init_priority;
//			  $$.reversed = true;
//			}
	;

sect_flag_list :
    NAME
//			{
//			  struct flag_info_list *n;
//			  n = ((struct flag_info_list *) xmalloc (sizeof *n));
//			  if ($1[0] == EXCLAMATION)
//			    {
//			      n->with = without_flags;
//			      n->name = &$1[1];
//			    }
//			  else
//			    {
//			      n->with = with_flags;
//			      n->name = $1;
//			    }
//			  n->valid = false;
//			  n->next = NULL;
//			  $$ = n;
//			}
	|
    sect_flag_list AND NAME
//			{
//			  struct flag_info_list *n;
//			  n = ((struct flag_info_list *) xmalloc (sizeof *n));
//			  if ($3[0] == EXCLAMATION)
//			    {
//			      n->with = without_flags;
//			      n->name = &$3[1];
//			    }
//			  else
//			    {
//			      n->with = with_flags;
//			      n->name = $3;
//			    }
//			  n->valid = false;
//			  n->next = $1;
//			  $$ = n;
//			}
	;

sect_flags :
		INPUT_SECTION_FLAGS OPENING_BRACKET sect_flag_list CLOSING_BRACKET
//			{
//			  struct flag_info *n;
//			  n = ((struct flag_info *) xmalloc (sizeof *n));
//			  n->flag_list = $3;
//			  n->flags_initialized = false;
//			  n->not_with_flags = 0;
//			  n->only_with_flags = 0;
//			  $$ = n;
//			}
	;

exclude_name_list :
		exclude_name_list wildcard_name
//			{
//			  struct name_list *tmp;
//			  tmp = (struct name_list *) xmalloc (sizeof *tmp);
//			  tmp->name = $2;
//			  tmp->next = $1;
//			  $$ = tmp;
//			}
	|
//		wildcard_name
//			{
//			  struct name_list *tmp;
//			  tmp = (struct name_list *) xmalloc (sizeof *tmp);
//			  tmp->name = $1;
//			  tmp->next = NULL;
//			  $$ = tmp;
//			}
	;

section_name_list :
    section_name_list opt_comma? section_name_spec
//			{
//			  struct wildcard_list *tmp;
//			  tmp = (struct wildcard_list *) xmalloc (sizeof *tmp);
//			  tmp->next = $1;
//			  tmp->spec = $3;
//			  $$ = tmp;
//			}
	|
    section_name_spec
//			{
//			  struct wildcard_list *tmp;
//			  tmp = (struct wildcard_list *) xmalloc (sizeof *tmp);
//			  tmp->next = NULL;
//			  tmp->spec = $1;
//			  $$ = tmp;
//			}
	;

input_section_spec_no_keep :
    NAME
//			{
//			  struct wildcard_spec tmp;
//			  tmp.name = $1;
//			  tmp.exclude_name_list = NULL;
//			  tmp.sorted = none;
//			  tmp.section_flag_list = NULL;
//			  lang_add_wild (&tmp, NULL, ldgram_had_keep);
//			}
	|
    sect_flags NAME
//			{
//			  struct wildcard_spec tmp;
//			  tmp.name = $2;
//			  tmp.exclude_name_list = NULL;
//			  tmp.sorted = none;
//			  tmp.section_flag_list = $1;
//			  lang_add_wild (&tmp, NULL, ldgram_had_keep);
//			}
	|
    OPENING_RECTANGULAR_BRACKET section_name_list CLOSING_RECTANGULAR_BRACKET
//			{
//			  lang_add_wild (NULL, $2, ldgram_had_keep);
//			}
	|
    sect_flags OPENING_RECTANGULAR_BRACKET section_name_list CLOSING_RECTANGULAR_BRACKET
//			{
//			  struct wildcard_spec tmp;
//			  tmp.name = NULL;
//			  tmp.exclude_name_list = NULL;
//			  tmp.sorted = none;
//			  tmp.section_flag_list = $1;
//			  lang_add_wild (&tmp, $3, ldgram_had_keep);
//			}
	|	filename_spec OPENING_BRACKET section_name_list CLOSING_BRACKET
//			{
//			  lang_add_wild (&$1, $3, ldgram_had_keep);
//			}
	|	sect_flags filename_spec OPENING_BRACKET section_name_list CLOSING_BRACKET
//			{
//			  $2.section_flag_list = $1;
//			  lang_add_wild (&$2, $4, ldgram_had_keep);
//			}
	;

input_section_spec :
	input_section_spec_no_keep
	|
    KEEP OPENING_BRACKET
//			{ ldgram_had_keep = true; }
		input_section_spec_no_keep CLOSING_BRACKET
//			{ ldgram_had_keep = false; }
	;

statement :
	SEMICOLON
	|
    assignment separator
	|
    CREATE_OBJECT_SYMBOLS
//		{
//		  lang_add_attribute (lang_object_symbols_statement_enum);
//		}
	|
    CONSTRUCTORS
//		{
//		  lang_add_attribute (lang_constructors_statement_enum);
//		}
	|
    SORT_BY_NAME OPENING_BRACKET CONSTRUCTORS CLOSING_BRACKET
//		{
//		  constructors_sorted = true;
//		  lang_add_attribute (lang_constructors_statement_enum);
//		}
	|
    input_section_spec
	|
    length OPENING_BRACKET mustbe_exp CLOSING_BRACKET
//		{
//		  lang_add_data ((int) $1, $3);
//		}
	|
    ASCIZ NAME
//		{
//		  lang_add_string ($2);
//		}
	|
    FILL OPENING_BRACKET fill_exp CLOSING_BRACKET
//		{
//		  lang_add_fill ($3);
//		}
	|
    LINKER_VERSION
//		{
//		  lang_add_version_string ();
//		}
	|
    ASSERT_K
//		{ ldlex_expression (); }
	OPENING_BRACKET exp COMMA NAME CLOSING_BRACKET separator
//		{
//		  ldlex_popstate ();
//		  lang_add_assignment (exp_assert ($4, $6));
//		}
	|
    INCLUDE filename
//		{
//		  ldfile_open_command_file ($2);
//		}
	statement_list_opt? END
	;

statement_list_opt :
	statement_list
	;

statement_list :
    statement_list statement
	|
    statement
	;

length :
		QUAD
//			{ $$ = $1; }
	|	SQUAD
//			{ $$ = $1; }
	|	LONG
//			{ $$ = $1; }
	|	SHORT
//			{ $$ = $1; }
	|	BYTE
//			{ $$ = $1; }
	;

fill_exp :
	mustbe_exp
//		{
//		  $$ = exp_get_fill ($1, 0, _("fill value"));
//		}
	;

fill_opt :
	ASSIGN fill_exp
//		{ $$ = $2; }
//	|	// { $$ = (fill_type *) 0; }
	;

statement_anywhere :
    ENTRY OPENING_BRACKET NAME CLOSING_BRACKET
//		{ lang_add_entry ($3, false); }
	|
    assignment separator
	|
    ASSERT_K
//        { ldlex_expression (); }
        OPENING_BRACKET exp COMMA NAME CLOSING_BRACKET
//		{ ldlex_popstate ();
//		  lang_add_assignment (exp_assert ($4, $6)); }
	;

assign_op :
	PLUSEQ
//			{ $$ = PLUS; }
	|
    MINUSEQ
//			{ $$ = MINUS; }
	|
    MULTEQ
//			{ $$ = '*'; }
	|
    DIVEQ
//			{ $$ = '/'; }
	|
    LSHIFTEQ
//			{ $$ = LSHIFT; }
	|
    RSHIFTEQ
//			{ $$ = RSHIFT; }
	|
    ANDEQ
//			{ $$ = AND; }
	|
    OREQ
//			{ $$ = '|'; }
	|
    XOREQ
//			{ $$ = '^'; }
	;

separator :
    SEMICOLON
    |
    COMMA
	;

assignment :
	NAME ASSIGN mustbe_exp
//		{
//		  lang_add_assignment (exp_assign ($1, $3, false));
//		}
	|
    NAME assign_op mustbe_exp
//		{
//		  lang_add_assignment (exp_assign ($1,
//						   exp_binop ($2,
//							      exp_nameop (NAME,
//									  $1),
//							      $3), false));
//		}
	|
    HIDDEN_ OPENING_BRACKET NAME ASSIGN mustbe_exp CLOSING_BRACKET
//		{
//		  lang_add_assignment (exp_assign ($3, $5, true));
//		}
	|
    PROVIDE OPENING_BRACKET NAME ASSIGN mustbe_exp CLOSING_BRACKET
//		{
//		  lang_add_assignment (exp_provide ($3, $5, false));
//		}
	|
    PROVIDE_HIDDEN OPENING_BRACKET NAME ASSIGN mustbe_exp CLOSING_BRACKET
//		{
//		  lang_add_assignment (exp_provide ($3, $5, true));
//		}
	;

opt_comma :
    COMMA
    ;

paren_script_name :
//    { ldlex_script (); }
	OPENING_BRACKET NAME CLOSING_BRACKET
//	  { ldlex_popstate (); $$ = $3; }
    ;

mustbe_exp :
//    { ldlex_expression (); }
	exp
//	  { ldlex_popstate (); $$ = $2; }
	;

exp	:
		MINUS exp /* %prec */ UNARY
//			{ $$ = exp_unop (MINUS, $2); }
	|	OPENING_BRACKET exp CLOSING_BRACKET
//			{ $$ = $2; }
	|	NEXT OPENING_BRACKET exp CLOSING_BRACKET /* %prec */ UNARY
//			{ $$ = exp_unop ((int) $1,$3); }
	|	EXCLAMATION exp /* %prec */ UNARY
//			{ $$ = exp_unop (EXCLAMATION, $2); }
	|	PLUS exp /* %prec */ UNARY
//			{ $$ = $2; }
	|	'~' exp /* %prec */ UNARY
//			{ $$ = exp_unop ('~', $2); }
	|	exp '*' exp
//			{ $$ = exp_binop ('*', $1, $3); }
	|	exp '/' exp
//			{ $$ = exp_binop ('/', $1, $3); }
	|	exp '%' exp
//			{ $$ = exp_binop ('%', $1, $3); }
	|	exp PLUS exp
//			{ $$ = exp_binop (PLUS, $1, $3); }
	|	exp MINUS exp
//			{ $$ = exp_binop (MINUS , $1, $3); }
	|	exp LSHIFT exp
//			{ $$ = exp_binop (LSHIFT , $1, $3); }
	|	exp RSHIFT exp
//			{ $$ = exp_binop (RSHIFT , $1, $3); }
	|	exp EQ exp
//			{ $$ = exp_binop (EQ , $1, $3); }
	|	exp NE exp
//			{ $$ = exp_binop (NE , $1, $3); }
	|	exp LE exp
//			{ $$ = exp_binop (LE , $1, $3); }
	|	exp GE exp
//			{ $$ = exp_binop (GE , $1, $3); }
	|	exp LT exp
//			{ $$ = exp_binop (LT , $1, $3); }
	|	exp GT exp
//			{ $$ = exp_binop (GT , $1, $3); }
	|	exp AND exp
//			{ $$ = exp_binop (AND , $1, $3); }
	|	exp '^' exp
//			{ $$ = exp_binop ('^' , $1, $3); }
	|	exp '|' exp
//			{ $$ = exp_binop ('|' , $1, $3); }
	|	exp '?' exp COLON exp
//			{ $$ = exp_trinop ('?' , $1, $3, $5); }
	|	exp ANDAND exp
//			{ $$ = exp_binop (ANDAND , $1, $3); }
	|	exp OROR exp
//			{ $$ = exp_binop (OROR , $1, $3); }
	|	DEFINED OPENING_BRACKET NAME CLOSING_BRACKET
//			{ $$ = exp_nameop (DEFINED, $3); }
	|	INT
//			{ $$ = exp_bigintop ($1.integer, $1.str); }
	|	SIZEOF_HEADERS
//			{ $$ = exp_nameop (SIZEOF_HEADERS,0); }
	|	ALIGNOF paren_script_name
//			{ $$ = exp_nameop (ALIGNOF, $2); }
	|	SIZEOF	paren_script_name
//			{ $$ = exp_nameop (SIZEOF, $2); }
	|	ADDR	paren_script_name
//			{ $$ = exp_nameop (ADDR, $2); }
	|	LOADADDR paren_script_name
//			{ $$ = exp_nameop (LOADADDR, $2); }
	|	CONSTANT OPENING_BRACKET NAME CLOSING_BRACKET
//			{ $$ = exp_nameop (CONSTANT,$3); }
	|	ABSOLUTE OPENING_BRACKET exp CLOSING_BRACKET
//			{ $$ = exp_unop (ABSOLUTE, $3); }
	|	ALIGN_K OPENING_BRACKET exp CLOSING_BRACKET
//			{ $$ = exp_unop (ALIGN_K,$3); }
	|	ALIGN_K OPENING_BRACKET exp COMMA exp CLOSING_BRACKET
//			{ $$ = exp_binop (ALIGN_K,$3,$5); }
	|	DATA_SEGMENT_ALIGN OPENING_BRACKET exp COMMA exp CLOSING_BRACKET
//			{ $$ = exp_binop (DATA_SEGMENT_ALIGN, $3, $5); }
	|	DATA_SEGMENT_RELRO_END OPENING_BRACKET exp COMMA exp CLOSING_BRACKET
//			{ $$ = exp_binop (DATA_SEGMENT_RELRO_END, $5, $3); }
	|	DATA_SEGMENT_END OPENING_BRACKET exp CLOSING_BRACKET
//			{ $$ = exp_unop (DATA_SEGMENT_END, $3); }
	|	SEGMENT_START
//          { ldlex_script (); }
    OPENING_BRACKET NAME
//			{ ldlex_popstate (); }
    COMMA exp CLOSING_BRACKET
//			{ /* The operands to the expression node are
//			     placed in the opposite order from the way
//			     in which they appear in the script as
//			     that allows us to reuse more code in
//			     fold_binary.  */
//			  $$ = exp_binop (SEGMENT_START,
//					  $7,
//					  exp_nameop (NAME, $4)); }
	|	BLOCK OPENING_BRACKET exp CLOSING_BRACKET
//			{ $$ = exp_unop (ALIGN_K,$3); }
	|	NAME
//			{ $$ = exp_nameop (NAME,$1); }
	|	MAX_K OPENING_BRACKET exp COMMA exp CLOSING_BRACKET
//			{ $$ = exp_binop (MAX_K, $3, $5 ); }
	|	MIN_K OPENING_BRACKET exp COMMA exp CLOSING_BRACKET
//			{ $$ = exp_binop (MIN_K, $3, $5 ); }
	|	ASSERT_K OPENING_BRACKET exp COMMA NAME CLOSING_BRACKET
//			{ $$ = exp_assert ($3, $5); }
	|	ORIGIN paren_script_name
//			{ $$ = exp_nameop (ORIGIN, $2); }
	|	LENGTH paren_script_name
//			{ $$ = exp_nameop (LENGTH, $2); }
	|	LOG2CEIL OPENING_BRACKET exp CLOSING_BRACKET
//			{ $$ = exp_unop (LOG2CEIL, $3); }
	;

memspec_at_opt :
		AT '>' NAME // { $$ = $3; }
	//|	// { $$ = 0; }
	;

opt_at :
		AT OPENING_BRACKET exp CLOSING_BRACKET // { $$ = $3; }
	//|	// { $$ = 0; }
	;

opt_align :
		ALIGN_K OPENING_BRACKET exp CLOSING_BRACKET // { $$ = $3; }
	//|	// { $$ = 0; }
	;

opt_align_with_input :
		ALIGN_WITH_INPUT // { $$ = ALIGN_WITH_INPUT; }
	//|	// { $$ = 0; }
	;

opt_subalign :
		SUBALIGN OPENING_BRACKET exp CLOSING_BRACKET // { $$ = $3; }
	//|	// { $$ = 0; }
	;

sect_constraint :
		ONLY_IF_RO // { $$ = ONLY_IF_RO; }
	|
        ONLY_IF_RW // { $$ = ONLY_IF_RW; }
	|
        SPECIAL // { $$ = SPECIAL; }
	//|	// { $$ = 0; }
	;

section :
    NAME COLON
    //			{ ldlex_expression(); }
///		opt_exp_with_type?
///		opt_at?
///		opt_align?
///		opt_align_with_input?
///		opt_subalign?
///		sect_constraint?
//			{
//			  ldlex_popstate ();
//			  ldlex_wild ();
//			  lang_enter_output_section_statement ($1, $3, sectype,
//					sectype_value, $5, $7, $4, $8, $6);
//			}
		OPENING_SQUIGGLY_BRACKET
		statement_list_opt?
		CLOSING_SQUIGGLY_BRACKET
//			{ ldlex_popstate (); }
		memspec_opt?
///     memspec_at_opt? phdr_opt? fill_opt?
//			{
//			  /* fill_opt may have switched the lexer into
//			     expression state, and back again, but in
//			     order to find the end of the fill
//			     expression the parser must look ahead one
//			     token.  If it is a NAME, throw it away as
//			     it will have been lexed in the wrong
//			     state.  */
//			  if (yychar == NAME)
//			    {
//			      yyclearin;
//			      ldlex_backup ();
//			    }
//			  lang_leave_output_section_statement ($17, $14, $16, $15);
//			}
		opt_comma?
///	|
///        OVERLAY
//			{ ldlex_expression (); }
///		opt_exp_without_type? opt_nocrossrefs? opt_at? opt_subalign?
//			{ ldlex_popstate (); }
///		OPENING_SQUIGGLY_BRACKET
//			{
//			  lang_enter_overlay ($3, $6);
//			}
///		overlay_section
///		CLOSING_SQUIGGLY_BRACKET
///		memspec_opt memspec_at_opt phdr_opt fill_opt
//			{
//			  if (yychar == NAME)
//			    {
//			      yyclearin;
//			      ldlex_backup ();
//			    }
//			  lang_leave_overlay ($5, (int) $4,
//					      $15, $12, $14, $13);
//			}
///		opt_comma?
///	|
//    /* The GROUP case is just enough to support the gcc
//		   svr3.ifile script.  It is not intended to be full
//		   support.  I'm not even sure what GROUP is supposed
//		   to mean.  */
///		GROUP
//			{ ldlex_expression (); }
///		opt_exp_with_type?
//			{
//			  ldlex_popstate ();
//			  lang_add_assignment (exp_assign (".", $3, false));
//			}
///		OPENING_SQUIGGLY_BRACKET sec_or_group_p1 CLOSING_SQUIGGLY_BRACKET
///	|
///    INCLUDE filename
//			{
//			  ldfile_open_command_file ($2);
//			}
///		sec_or_group_p1 END
	;

type :
	   NOLOAD  // { sectype = noload_section; }
	|  DSECT   // { sectype = noalloc_section; }
	|  COPY    // { sectype = noalloc_section; }
	|  INFO    // { sectype = noalloc_section; }
	|  OVERLAY // { sectype = noalloc_section; }
    |  READONLY OPENING_BRACKET TYPE ASSIGN exp CLOSING_BRACKET // { sectype = typed_readonly_section; sectype_value = $5; }
	|  READONLY // { sectype = readonly_section; }
	|  TYPE ASSIGN exp // { sectype = type_section; sectype_value = $3; }
    ;

atype :
		OPENING_BRACKET type CLOSING_BRACKET
	|
//        /* EMPTY */ { sectype = normal_section; }
	|
    OPENING_BRACKET CLOSING_BRACKET
    //{ sectype = normal_section; }
	;

opt_exp_with_type :
		exp atype COLON		// { $$ = $1; }
	|
        atype COLON		// { $$ = (etree_type *)NULL;  }
	|
        // The BIND cases are to support the gcc svr3.ifile
		//   script.  They aren't intended to implement full
		//   support for the BIND keyword.  I'm not even sure
		//   what BIND is supposed to mean.
		BIND OPENING_BRACKET exp CLOSING_BRACKET atype COLON  // { $$ = $3; }
	|
        BIND OPENING_BRACKET exp CLOSING_BRACKET BLOCK OPENING_BRACKET exp CLOSING_BRACKET atype COLON
		//{ $$ = $3; }
	;

opt_exp_without_type :
    exp COLON		// { $$ = $1; }
	|
    COLON		// { $$ = (etree_type *) NULL;  }
	;

opt_nocrossrefs :
    NOCROSSREFS
	;

memspec_opt :
    '>' NAME
	;

phdr_opt :
//		// empty
//		{
//		  $$ = NULL;
//		}
//	|
    // TODO: REACTIVATE THIS phdr_opt
    COLON NAME
//		{
//		  struct lang_output_section_phdr_list *n;
//
//		  n = ((struct lang_output_section_phdr_list *)
//		       xmalloc (sizeof *n));
//		  n->name = $3;
//		  n->used = false;
//		  n->next = $1;
//		  $$ = n;
//		}
	;

overlay_section :
// TODO: REACTIVATE THIS!!!!        overlay_section
		NAME
//			{
//			  ldlex_wild ();
//			  lang_enter_overlay_section ($2);
//			}
		OPENING_SQUIGGLY_BRACKET
		statement_list_opt?
		CLOSING_SQUIGGLY_BRACKET
//			{ ldlex_popstate (); }
		phdr_opt fill_opt
//			{
//			  if (yychar == NAME)
//			    {
//			      yyclearin;
//			      ldlex_backup ();
//			    }
//			  lang_leave_overlay_section ($9, $8);
//			}
		opt_comma?
	;

phdrs :
	PHDRS OPENING_SQUIGGLY_BRACKET phdr_list CLOSING_SQUIGGLY_BRACKET
	;

phdr_list :
	phdr_list phdr
    |
    phdr
	;

phdr :
		NAME // { ldlex_expression (); }
		  phdr_type phdr_qualifiers // { ldlex_popstate (); }
		  SEMICOLON
//		{
//		  lang_new_phdr ($1, $3, $4.filehdr, $4.phdrs, $4.at, $4.flags);
//		}
	;

phdr_type :
		exp
//		{
//		  $$ = $1;
//
//		  if ($1->type.node_class == etree_name
//		      && $1->type.node_code == NAME)
//		    {
//		      const char *s;
//		      unsigned int i;
//		      static const char * const phdr_types[] =
//			{
//			  "PT_NULL", "PT_LOAD", "PT_DYNAMIC",
//			  "PT_INTERP", "PT_NOTE", "PT_SHLIB",
//			  "PT_PHDR", "PT_TLS"
//			};
//
//		      s = $1->name.name;
//		      for (i = 0;
//			   i < sizeof phdr_types / sizeof phdr_types[0];
//			   i++)
//			if (strcmp (s, phdr_types[i]) == 0)
//			  {
//			    $$ = exp_intop (i);
//			    break;
//			  }
//		      if (i == sizeof phdr_types / sizeof phdr_types[0])
//			{
//			  if (strcmp (s, "PT_GNU_EH_FRAME") == 0)
//			    $$ = exp_intop (0x6474e550);
//			  else if (strcmp (s, "PT_GNU_STACK") == 0)
//			    $$ = exp_intop (0x6474e551);
//			  else if (strcmp (s, "PT_GNU_RELRO") == 0)
//			    $$ = exp_intop (0x6474e552);
//			  else if (strcmp (s, "PT_GNU_PROPERTY") == 0)
//			    $$ = exp_intop (0x6474e553);
//			  else
//			    {
//			      einfo (_("\
//%X%P:%pS: unknown phdr type `%s' (try integer literal)\n"),
//				     NULL, s);
//			      $$ = exp_intop (0);
//			    }
//			}
//		    }
//		}
	;

phdr_qualifiers :
		// empty
//		{
//		  memset (&$$, 0, sizeof (struct phdr_info));
//		}
	//|
    NAME phdr_val phdr_qualifiers
//		{
//		  $$ = $3;
//		  if (strcmp ($1, "FILEHDR") == 0 && $2 == NULL)
//		    $$.filehdr = true;
//		  else if (strcmp ($1, "PHDRS") == 0 && $2 == NULL)
//		    $$.phdrs = true;
//		  else if (strcmp ($1, "FLAGS") == 0 && $2 != NULL)
//		    $$.flags = $2;
//		  else
//		    einfo (_("%X%P:%pS: PHDRS syntax error at `%s'\n"),
//			   NULL, $1);
//		}
	|	AT OPENING_BRACKET exp CLOSING_BRACKET phdr_qualifiers
//		{
//		  $$ = $5;
//		  $$.at = $3;
//		}
	;

phdr_val :
		// empty
//		{
//		  $$ = NULL;
//		}
	//|
    OPENING_BRACKET exp CLOSING_BRACKET
//		{
//		  $$ = $2;
//		}
	;

dynamic_list_file :
//		{
//		  ldlex_version_file ();
//		  PUSH_ERROR (_("dynamic list"));
//		}
		dynamic_list_nodes
//		{
//		  ldlex_popstate ();
//		  POP_ERROR ();
//		}
	;

dynamic_list_nodes :
		dynamic_list_node
	|
        dynamic_list_nodes dynamic_list_node
	;

dynamic_list_node :
		OPENING_SQUIGGLY_BRACKET dynamic_list_tag CLOSING_SQUIGGLY_BRACKET SEMICOLON
	;

dynamic_list_tag :
		vers_defns SEMICOLON
//		{
//		  lang_append_dynamic_list (current_dynamic_list_p, $1);
//		}
	;

// This syntax is used within an external version script file.

version_script_file :
//		{
//		  ldlex_version_file ();
//		  PUSH_ERROR (_("VERSION script"));
//		}
		vers_nodes
//		{
//		  ldlex_popstate ();
//		  POP_ERROR ();
//		}
	;

// This is used within a normal linker script file.

version :
//		{
//		  ldlex_version_script ();
//		}
		VERSIONK OPENING_SQUIGGLY_BRACKET vers_nodes CLOSING_SQUIGGLY_BRACKET
//		{
//		  ldlex_popstate ();
//		}
	;

vers_nodes :
		vers_node
	|
        vers_nodes vers_node
	;

vers_node :
		OPENING_SQUIGGLY_BRACKET vers_tag CLOSING_SQUIGGLY_BRACKET SEMICOLON
//		{
//		  lang_register_vers_node (NULL, $2, NULL);
//		}
	|	VERS_TAG OPENING_SQUIGGLY_BRACKET vers_tag CLOSING_SQUIGGLY_BRACKET SEMICOLON
//		{
//		  lang_register_vers_node ($1, $3, NULL);
//		}
	|	VERS_TAG OPENING_SQUIGGLY_BRACKET vers_tag CLOSING_SQUIGGLY_BRACKET verdep SEMICOLON
//		{
//		  lang_register_vers_node ($1, $3, $5);
//		}
	;

verdep :
		VERS_TAG
//		{
//		  $$ = lang_add_vers_depend (NULL, $1);
//		}
	|	verdep VERS_TAG
//		{
//		  $$ = lang_add_vers_depend ($1, $2);
//		}
	;

vers_tag :
		// empty
//		{
//		  $$ = lang_new_vers_node (NULL, NULL);
//		}
	//|
    vers_defns SEMICOLON
//		{
//		  $$ = lang_new_vers_node ($1, NULL);
//		}
	|	GLOBAL COLON vers_defns SEMICOLON
//		{
//		  $$ = lang_new_vers_node ($3, NULL);
//		}
	|	LOCAL COLON vers_defns SEMICOLON
//		{
//		  $$ = lang_new_vers_node (NULL, $3);
//		}
	|	GLOBAL COLON vers_defns SEMICOLON LOCAL COLON vers_defns SEMICOLON
//		{
//		  $$ = lang_new_vers_node ($3, $7);
//		}
	;

vers_defns :
		VERS_IDENTIFIER
//		{
//		  $$ = lang_new_vers_pattern (NULL, $1, ldgram_vers_current_lang, false);
//		}
	|	NAME
//		{
//		  $$ = lang_new_vers_pattern (NULL, $1, ldgram_vers_current_lang, true);
//		}
	|	vers_defns SEMICOLON VERS_IDENTIFIER
//		{
//		  $$ = lang_new_vers_pattern ($1, $3, ldgram_vers_current_lang, false);
//		}
	|	vers_defns SEMICOLON NAME
//		{
//		  $$ = lang_new_vers_pattern ($1, $3, ldgram_vers_current_lang, true);
//		}
	|	vers_defns SEMICOLON EXTERN NAME OPENING_SQUIGGLY_BRACKET
//			{
//			  $<name>$ = ldgram_vers_current_lang;
//			  ldgram_vers_current_lang = $4;
//			}
		vers_defns opt_semicolon? CLOSING_SQUIGGLY_BRACKET
//			{
//			  struct bfd_elf_version_expr *pat;
//			  for (pat = $7; pat->next != NULL; pat = pat->next);
//			  pat->next = $1;
//			  $$ = $7;
//			  ldgram_vers_current_lang = $<name>6;
//			}
	|	EXTERN NAME OPENING_SQUIGGLY_BRACKET
//			{
//			  $<name>$ = ldgram_vers_current_lang;
//			  ldgram_vers_current_lang = $2;
//			}
		vers_defns opt_semicolon? CLOSING_SQUIGGLY_BRACKET
//			{
//			  $$ = $5;
//			  ldgram_vers_current_lang = $<name>4;
//			}
	|	GLOBAL
//		{
//		  $$ = lang_new_vers_pattern (NULL, "global", ldgram_vers_current_lang, false);
//		}
	|	vers_defns SEMICOLON GLOBAL
//		{
//		  $$ = lang_new_vers_pattern ($1, "global", ldgram_vers_current_lang, false);
//		}
	|	LOCAL
//		{
//		  $$ = lang_new_vers_pattern (NULL, "local", ldgram_vers_current_lang, false);
//		}
	|	vers_defns SEMICOLON LOCAL
//		{
//		  $$ = lang_new_vers_pattern ($1, "local", ldgram_vers_current_lang, false);
//		}
	|	EXTERN
//		{
//		  $$ = lang_new_vers_pattern (NULL, "extern", ldgram_vers_current_lang, false);
//		}
	|	vers_defns SEMICOLON EXTERN
//		{
//		  $$ = lang_new_vers_pattern ($1, "extern", ldgram_vers_current_lang, false);
//		}
	;

opt_semicolon :
		SEMICOLON
	;

section_ordering_script_file :
//		{
//		  ldlex_script ();
//		  PUSH_ERROR (_("section-ordering-file script"));
//		}
		section_ordering_list
//		{
//		  ldlex_popstate ();
//		  POP_ERROR ();
//		}
	;

section_ordering_list :
		section_ordering_list section_order
	|	section_ordering_list statement_anywhere
	|   section_order
    |   statement_anywhere
	;

section_order :
    NAME COLON
//		{
//		  ldlex_wild ();
//		  lang_enter_output_section_statement
//		    ($1, NULL, 0, NULL, NULL, NULL, NULL, 0, 0);
//		}
		OPENING_SQUIGGLY_BRACKET
		statement_list_opt?
		CLOSING_SQUIGGLY_BRACKET
//		{
//		  ldlex_popstate ();
//		  lang_leave_output_section_statement (NULL, NULL, NULL, NULL);
//		}
		opt_comma?
        ;