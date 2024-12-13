// Generated from linkerscriptlanguage\LINKERSCRIPTLANGUAGELexer.g4 by ANTLR 4.9.1
package linkerscriptlanguage;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LINKERSCRIPTLANGUAGELexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OPENING_BRACKET=1, CLOSING_BRACKET=2, OPENING_SQUIGGLY_BRACKET=3, CLOSING_SQUIGGLY_BRACKET=4, 
		OPENING_RECTANGULAR_BRACKET=5, CLOSING_RECTANGULAR_BRACKET=6, SEMICOLON=7, 
		COMMA=8, ASSIGN=9, COLON=10, AND=11, PLUS=12, MINUS=13, EXCLAMATION=14, 
		TILDE=15, MULT=16, DIV=17, PERCENT=18, CIRC=19, OR=20, QUESTION=21, UNARY=22, 
		NEXT=23, LSHIFT=24, RSHIFT=25, EQ=26, NE=27, LE=28, LT=29, GE=30, GT=31, 
		PLUSEQ=32, MINUSEQ=33, MULTEQ=34, DIVEQ=35, LSHIFTEQ=36, RSHIFTEQ=37, 
		ANDEQ=38, OREQ=39, XOREQ=40, ANDAND=41, OROR=42, DEFINED=43, SIZEOF_HEADERS=44, 
		ALIGNOF=45, SIZEOF=46, ADDR=47, LOADADDR=48, CONSTANT=49, ABSOLUTE=50, 
		ALIGN_K=51, DATA_SEGMENT_ALIGN=52, DATA_SEGMENT_RELRO_END=53, DATA_SEGMENT_END=54, 
		SEGMENT_START=55, BLOCK=56, MAX_K=57, MIN_K=58, ASSERT_K=59, ORIGIN=60, 
		LENGTH=61, LOG2CEIL=62, PROVIDE=63, PROVIDE_HIDDEN=64, HIDDEN_=65, END=66, 
		ENTRY=67, NOLOAD=68, DSECT=69, COPY=70, INFO=71, OVERLAY=72, READONLY=73, 
		TYPE=74, INPUT_SCRIPT=75, SECTIONS=76, EXCLUDE_FILE=77, REVERSE=78, SORT_BY_NAME=79, 
		SORT_NONE=80, SORT_BY_ALIGNMENT=81, SORT_BY_INIT_PRIORITY=82, INPUT_SECTION_FLAGS=83, 
		KEEP=84, CREATE_OBJECT_SYMBOLS=85, CONSTRUCTORS=86, ASCIZ=87, FILL=88, 
		LINKER_VERSION=89, INCLUDE=90, QUAD=91, SQUAD=92, LONG=93, SHORT=94, BYTE=95, 
		AT=96, ALIGN_WITH_INPUT=97, SUBALIGN=98, ONLY_IF_RO=99, ONLY_IF_RW=100, 
		SPECIAL=101, GROUP=102, NOCROSSREFS=103, PHDRS=104, VERSIONK=105, VERS_TAG=106, 
		GLOBAL=107, LOCAL=108, VERS_IDENTIFIER=109, EXTERN=110, BIND=111, NAME=112, 
		INT=113, WS=114;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "OPENING_BRACKET", 
			"CLOSING_BRACKET", "OPENING_SQUIGGLY_BRACKET", "CLOSING_SQUIGGLY_BRACKET", 
			"OPENING_RECTANGULAR_BRACKET", "CLOSING_RECTANGULAR_BRACKET", "SEMICOLON", 
			"COMMA", "ASSIGN", "COLON", "AND", "PLUS", "MINUS", "EXCLAMATION", "TILDE", 
			"MULT", "DIV", "PERCENT", "CIRC", "OR", "QUESTION", "UNARY", "NEXT", 
			"LSHIFT", "RSHIFT", "EQ", "NE", "LE", "LT", "GE", "GT", "PLUSEQ", "MINUSEQ", 
			"MULTEQ", "DIVEQ", "LSHIFTEQ", "RSHIFTEQ", "ANDEQ", "OREQ", "XOREQ", 
			"ANDAND", "OROR", "DEFINED", "SIZEOF_HEADERS", "ALIGNOF", "SIZEOF", "ADDR", 
			"LOADADDR", "CONSTANT", "ABSOLUTE", "ALIGN_K", "DATA_SEGMENT_ALIGN", 
			"DATA_SEGMENT_RELRO_END", "DATA_SEGMENT_END", "SEGMENT_START", "BLOCK", 
			"MAX_K", "MIN_K", "ASSERT_K", "ORIGIN", "LENGTH", "LOG2CEIL", "PROVIDE", 
			"PROVIDE_HIDDEN", "HIDDEN_", "END", "ENTRY", "NOLOAD", "DSECT", "COPY", 
			"INFO", "OVERLAY", "READONLY", "TYPE", "INPUT_SCRIPT", "SECTIONS", "EXCLUDE_FILE", 
			"REVERSE", "SORT_BY_NAME", "SORT_NONE", "SORT_BY_ALIGNMENT", "SORT_BY_INIT_PRIORITY", 
			"INPUT_SECTION_FLAGS", "KEEP", "CREATE_OBJECT_SYMBOLS", "CONSTRUCTORS", 
			"ASCIZ", "FILL", "LINKER_VERSION", "INCLUDE", "QUAD", "SQUAD", "LONG", 
			"SHORT", "BYTE", "AT", "ALIGN_WITH_INPUT", "SUBALIGN", "ONLY_IF_RO", 
			"ONLY_IF_RW", "SPECIAL", "GROUP", "NOCROSSREFS", "PHDRS", "VERSIONK", 
			"VERS_TAG", "GLOBAL", "LOCAL", "VERS_IDENTIFIER", "EXTERN", "BIND", "NAME", 
			"INT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'{'", "'}'", "'['", "']'", "';'", "','", "'='", 
			"':'", "'&'", "'+'", "'-'", "'!'", "'~'", "'*'", "'/'", "'%'", "'^'", 
			"'|'", "'?'", "'UNARY'", "'NEXT'", "'<<'", "'>>'", "'=='", "'!='", "'<='", 
			"'<'", "'>='", "'>'", "'+='", "'-='", "'*='", "'/='", "'<<='", "'>>='", 
			"'&='", "'|='", "'^='", "'&&'", "'||'", "'DEFINED'", "'SIZEOF_HEADERS'", 
			"'ALIGNOF'", "'SIZEOF'", "'ADDR'", "'LOADADDR'", "'CONSTANT'", "'ABSOLUTE'", 
			"'ALIGN_K'", "'DATA_SEGMENT_ALIGN'", "'DATA_SEGMENT_RELRO_END'", "'DATA_SEGMENT_END'", 
			"'SEGMENT_START'", "'BLOCK'", "'MAX_K'", "'MIN_K'", "'ASSERT_K'", "'ORIGIN'", 
			"'LENGTH'", "'LOG2CEIL'", "'PROVIDE'", "'PROVIDE_HIDDEN'", "'HIDDEN'", 
			"'END'", "'ENTRY'", "'NOLOAD'", "'DSECT'", "'COPY'", "'INFO'", "'OVERLAY'", 
			"'READONLY'", "'TYPE'", "'INPUT_SCRIPT'", "'SECTIONS'", "'EXCLUDE_FILE'", 
			"'REVERSE'", "'SORT_BY_NAME'", "'SORT_NONE'", "'SORT_BY_ALIGNMENT'", 
			"'SORT_BY_INIT_PRIORITY'", "'INPUT_SECTION_FLAGS'", "'KEEP'", "'CREATE_OBJECT_SYMBOLS'", 
			"'CONSTRUCTORS'", "'ASCIZ'", "'FILL'", "'LINKER_VERSION'", "'INCLUDE'", 
			"'QUAD'", "'SQUAD'", "'LONG'", "'SHORT'", "'BYTE'", "'AT'", "'ALIGN_WITH_INPUT'", 
			"'SUBALIGN'", "'ONLY_IF_RO'", "'ONLY_IF_RW'", "'SPECIAL'", "'GROUP'", 
			"'NOCROSSREFS'", "'PHDRS'", "'VERSIONK'", "'VERS_TAG'", "'GLOBAL'", "'LOCAL'", 
			"'VERS_IDENTIFIER'", "'EXTERN'", "'BIND'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "OPENING_BRACKET", "CLOSING_BRACKET", "OPENING_SQUIGGLY_BRACKET", 
			"CLOSING_SQUIGGLY_BRACKET", "OPENING_RECTANGULAR_BRACKET", "CLOSING_RECTANGULAR_BRACKET", 
			"SEMICOLON", "COMMA", "ASSIGN", "COLON", "AND", "PLUS", "MINUS", "EXCLAMATION", 
			"TILDE", "MULT", "DIV", "PERCENT", "CIRC", "OR", "QUESTION", "UNARY", 
			"NEXT", "LSHIFT", "RSHIFT", "EQ", "NE", "LE", "LT", "GE", "GT", "PLUSEQ", 
			"MINUSEQ", "MULTEQ", "DIVEQ", "LSHIFTEQ", "RSHIFTEQ", "ANDEQ", "OREQ", 
			"XOREQ", "ANDAND", "OROR", "DEFINED", "SIZEOF_HEADERS", "ALIGNOF", "SIZEOF", 
			"ADDR", "LOADADDR", "CONSTANT", "ABSOLUTE", "ALIGN_K", "DATA_SEGMENT_ALIGN", 
			"DATA_SEGMENT_RELRO_END", "DATA_SEGMENT_END", "SEGMENT_START", "BLOCK", 
			"MAX_K", "MIN_K", "ASSERT_K", "ORIGIN", "LENGTH", "LOG2CEIL", "PROVIDE", 
			"PROVIDE_HIDDEN", "HIDDEN_", "END", "ENTRY", "NOLOAD", "DSECT", "COPY", 
			"INFO", "OVERLAY", "READONLY", "TYPE", "INPUT_SCRIPT", "SECTIONS", "EXCLUDE_FILE", 
			"REVERSE", "SORT_BY_NAME", "SORT_NONE", "SORT_BY_ALIGNMENT", "SORT_BY_INIT_PRIORITY", 
			"INPUT_SECTION_FLAGS", "KEEP", "CREATE_OBJECT_SYMBOLS", "CONSTRUCTORS", 
			"ASCIZ", "FILL", "LINKER_VERSION", "INCLUDE", "QUAD", "SQUAD", "LONG", 
			"SHORT", "BYTE", "AT", "ALIGN_WITH_INPUT", "SUBALIGN", "ONLY_IF_RO", 
			"ONLY_IF_RW", "SPECIAL", "GROUP", "NOCROSSREFS", "PHDRS", "VERSIONK", 
			"VERS_TAG", "GLOBAL", "LOCAL", "VERS_IDENTIFIER", "EXTERN", "BIND", "NAME", 
			"INT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public LINKERSCRIPTLANGUAGELexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "LINKERSCRIPTLANGUAGELexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2t\u0462\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv\4"+
		"w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t\u0080"+
		"\4\u0081\t\u0081\4\u0082\t\u0082\4\u0083\t\u0083\4\u0084\t\u0084\4\u0085"+
		"\t\u0085\4\u0086\t\u0086\4\u0087\t\u0087\4\u0088\t\u0088\4\u0089\t\u0089"+
		"\4\u008a\t\u008a\4\u008b\t\u008b\4\u008c\t\u008c\4\u008d\t\u008d\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31"+
		"\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!"+
		"\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3"+
		",\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\62\3"+
		"\62\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65\3\66\3"+
		"\66\3\66\3\67\3\67\3\67\38\38\39\39\39\3:\3:\3;\3;\3;\3<\3<\3<\3=\3=\3"+
		"=\3>\3>\3>\3?\3?\3?\3?\3@\3@\3@\3@\3A\3A\3A\3B\3B\3B\3C\3C\3C\3D\3D\3"+
		"D\3E\3E\3E\3F\3F\3F\3F\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\3G\3"+
		"G\3G\3G\3G\3H\3H\3H\3H\3H\3H\3H\3H\3I\3I\3I\3I\3I\3I\3I\3J\3J\3J\3J\3"+
		"J\3K\3K\3K\3K\3K\3K\3K\3K\3K\3L\3L\3L\3L\3L\3L\3L\3L\3L\3M\3M\3M\3M\3"+
		"M\3M\3M\3M\3M\3N\3N\3N\3N\3N\3N\3N\3N\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3"+
		"O\3O\3O\3O\3O\3O\3O\3O\3O\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3P\3"+
		"P\3P\3P\3P\3P\3P\3P\3P\3P\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3"+
		"Q\3Q\3Q\3R\3R\3R\3R\3R\3R\3R\3R\3R\3R\3R\3R\3R\3R\3S\3S\3S\3S\3S\3S\3"+
		"T\3T\3T\3T\3T\3T\3U\3U\3U\3U\3U\3U\3V\3V\3V\3V\3V\3V\3V\3V\3V\3W\3W\3"+
		"W\3W\3W\3W\3W\3X\3X\3X\3X\3X\3X\3X\3Y\3Y\3Y\3Y\3Y\3Y\3Y\3Y\3Y\3Z\3Z\3"+
		"Z\3Z\3Z\3Z\3Z\3Z\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3[\3\\\3\\"+
		"\3\\\3\\\3\\\3\\\3\\\3]\3]\3]\3]\3^\3^\3^\3^\3^\3^\3_\3_\3_\3_\3_\3_\3"+
		"_\3`\3`\3`\3`\3`\3`\3a\3a\3a\3a\3a\3b\3b\3b\3b\3b\3c\3c\3c\3c\3c\3c\3"+
		"c\3c\3d\3d\3d\3d\3d\3d\3d\3d\3d\3e\3e\3e\3e\3e\3f\3f\3f\3f\3f\3f\3f\3"+
		"f\3f\3f\3f\3f\3f\3g\3g\3g\3g\3g\3g\3g\3g\3g\3h\3h\3h\3h\3h\3h\3h\3h\3"+
		"h\3h\3h\3h\3h\3i\3i\3i\3i\3i\3i\3i\3i\3j\3j\3j\3j\3j\3j\3j\3j\3j\3j\3"+
		"j\3j\3j\3k\3k\3k\3k\3k\3k\3k\3k\3k\3k\3l\3l\3l\3l\3l\3l\3l\3l\3l\3l\3"+
		"l\3l\3l\3l\3l\3l\3l\3l\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3m\3"+
		"m\3m\3m\3m\3m\3m\3m\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3n\3"+
		"n\3n\3n\3n\3o\3o\3o\3o\3o\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3p\3"+
		"p\3p\3p\3p\3p\3p\3p\3p\3q\3q\3q\3q\3q\3q\3q\3q\3q\3q\3q\3q\3q\3r\3r\3"+
		"r\3r\3r\3r\3s\3s\3s\3s\3s\3t\3t\3t\3t\3t\3t\3t\3t\3t\3t\3t\3t\3t\3t\3"+
		"t\3u\3u\3u\3u\3u\3u\3u\3u\3v\3v\3v\3v\3v\3w\3w\3w\3w\3w\3w\3x\3x\3x\3"+
		"x\3x\3y\3y\3y\3y\3y\3y\3z\3z\3z\3z\3z\3{\3{\3{\3|\3|\3|\3|\3|\3|\3|\3"+
		"|\3|\3|\3|\3|\3|\3|\3|\3|\3|\3}\3}\3}\3}\3}\3}\3}\3}\3}\3~\3~\3~\3~\3"+
		"~\3~\3~\3~\3~\3~\3~\3\177\3\177\3\177\3\177\3\177\3\177\3\177\3\177\3"+
		"\177\3\177\3\177\3\u0080\3\u0080\3\u0080\3\u0080\3\u0080\3\u0080\3\u0080"+
		"\3\u0080\3\u0081\3\u0081\3\u0081\3\u0081\3\u0081\3\u0081\3\u0082\3\u0082"+
		"\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082\3\u0082"+
		"\3\u0082\3\u0083\3\u0083\3\u0083\3\u0083\3\u0083\3\u0083\3\u0084\3\u0084"+
		"\3\u0084\3\u0084\3\u0084\3\u0084\3\u0084\3\u0084\3\u0084\3\u0085\3\u0085"+
		"\3\u0085\3\u0085\3\u0085\3\u0085\3\u0085\3\u0085\3\u0085\3\u0086\3\u0086"+
		"\3\u0086\3\u0086\3\u0086\3\u0086\3\u0086\3\u0087\3\u0087\3\u0087\3\u0087"+
		"\3\u0087\3\u0087\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088"+
		"\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088\3\u0088"+
		"\3\u0089\3\u0089\3\u0089\3\u0089\3\u0089\3\u0089\3\u0089\3\u008a\3\u008a"+
		"\3\u008a\3\u008a\3\u008a\3\u008b\5\u008b\u044e\n\u008b\3\u008b\6\u008b"+
		"\u0451\n\u008b\r\u008b\16\u008b\u0452\3\u008c\3\u008c\3\u008c\6\u008c"+
		"\u0458\n\u008c\r\u008c\16\u008c\u0459\3\u008d\6\u008d\u045d\n\u008d\r"+
		"\u008d\16\u008d\u045e\3\u008d\3\u008d\2\2\u008e\3\2\5\2\7\2\t\2\13\2\r"+
		"\2\17\2\21\2\23\2\25\2\27\2\31\2\33\2\35\2\37\2!\2#\2%\2\'\2)\2+\2-\2"+
		"/\2\61\2\63\2\65\2\67\39\4;\5=\6?\7A\bC\tE\nG\13I\fK\rM\16O\17Q\20S\21"+
		"U\22W\23Y\24[\25]\26_\27a\30c\31e\32g\33i\34k\35m\36o\37q s!u\"w#y${%"+
		"}&\177\'\u0081(\u0083)\u0085*\u0087+\u0089,\u008b-\u008d.\u008f/\u0091"+
		"\60\u0093\61\u0095\62\u0097\63\u0099\64\u009b\65\u009d\66\u009f\67\u00a1"+
		"8\u00a39\u00a5:\u00a7;\u00a9<\u00ab=\u00ad>\u00af?\u00b1@\u00b3A\u00b5"+
		"B\u00b7C\u00b9D\u00bbE\u00bdF\u00bfG\u00c1H\u00c3I\u00c5J\u00c7K\u00c9"+
		"L\u00cbM\u00cdN\u00cfO\u00d1P\u00d3Q\u00d5R\u00d7S\u00d9T\u00dbU\u00dd"+
		"V\u00dfW\u00e1X\u00e3Y\u00e5Z\u00e7[\u00e9\\\u00eb]\u00ed^\u00ef_\u00f1"+
		"`\u00f3a\u00f5b\u00f7c\u00f9d\u00fbe\u00fdf\u00ffg\u0101h\u0103i\u0105"+
		"j\u0107k\u0109l\u010bm\u010dn\u010fo\u0111p\u0113q\u0115r\u0117s\u0119"+
		"t\3\2\37\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2J"+
		"Jjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4"+
		"\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{"+
		"{\4\2\\\\||\5\2\62;C\\c|\5\2\62;CHch\5\2\13\f\17\17\"\"\2\u044b\2\67\3"+
		"\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2"+
		"\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2"+
		"Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3"+
		"\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2"+
		"\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2"+
		"w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2"+
		"\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2"+
		"\2\2\u0095\3\2\2\2\2\u0097\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d"+
		"\3\2\2\2\2\u009f\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2"+
		"\2\2\u00a7\3\2\2\2\2\u00a9\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\2\u00af"+
		"\3\2\2\2\2\u00b1\3\2\2\2\2\u00b3\3\2\2\2\2\u00b5\3\2\2\2\2\u00b7\3\2\2"+
		"\2\2\u00b9\3\2\2\2\2\u00bb\3\2\2\2\2\u00bd\3\2\2\2\2\u00bf\3\2\2\2\2\u00c1"+
		"\3\2\2\2\2\u00c3\3\2\2\2\2\u00c5\3\2\2\2\2\u00c7\3\2\2\2\2\u00c9\3\2\2"+
		"\2\2\u00cb\3\2\2\2\2\u00cd\3\2\2\2\2\u00cf\3\2\2\2\2\u00d1\3\2\2\2\2\u00d3"+
		"\3\2\2\2\2\u00d5\3\2\2\2\2\u00d7\3\2\2\2\2\u00d9\3\2\2\2\2\u00db\3\2\2"+
		"\2\2\u00dd\3\2\2\2\2\u00df\3\2\2\2\2\u00e1\3\2\2\2\2\u00e3\3\2\2\2\2\u00e5"+
		"\3\2\2\2\2\u00e7\3\2\2\2\2\u00e9\3\2\2\2\2\u00eb\3\2\2\2\2\u00ed\3\2\2"+
		"\2\2\u00ef\3\2\2\2\2\u00f1\3\2\2\2\2\u00f3\3\2\2\2\2\u00f5\3\2\2\2\2\u00f7"+
		"\3\2\2\2\2\u00f9\3\2\2\2\2\u00fb\3\2\2\2\2\u00fd\3\2\2\2\2\u00ff\3\2\2"+
		"\2\2\u0101\3\2\2\2\2\u0103\3\2\2\2\2\u0105\3\2\2\2\2\u0107\3\2\2\2\2\u0109"+
		"\3\2\2\2\2\u010b\3\2\2\2\2\u010d\3\2\2\2\2\u010f\3\2\2\2\2\u0111\3\2\2"+
		"\2\2\u0113\3\2\2\2\2\u0115\3\2\2\2\2\u0117\3\2\2\2\2\u0119\3\2\2\2\3\u011b"+
		"\3\2\2\2\5\u011d\3\2\2\2\7\u011f\3\2\2\2\t\u0121\3\2\2\2\13\u0123\3\2"+
		"\2\2\r\u0125\3\2\2\2\17\u0127\3\2\2\2\21\u0129\3\2\2\2\23\u012b\3\2\2"+
		"\2\25\u012d\3\2\2\2\27\u012f\3\2\2\2\31\u0131\3\2\2\2\33\u0133\3\2\2\2"+
		"\35\u0135\3\2\2\2\37\u0137\3\2\2\2!\u0139\3\2\2\2#\u013b\3\2\2\2%\u013d"+
		"\3\2\2\2\'\u013f\3\2\2\2)\u0141\3\2\2\2+\u0143\3\2\2\2-\u0145\3\2\2\2"+
		"/\u0147\3\2\2\2\61\u0149\3\2\2\2\63\u014b\3\2\2\2\65\u014d\3\2\2\2\67"+
		"\u014f\3\2\2\29\u0151\3\2\2\2;\u0153\3\2\2\2=\u0155\3\2\2\2?\u0157\3\2"+
		"\2\2A\u0159\3\2\2\2C\u015b\3\2\2\2E\u015d\3\2\2\2G\u015f\3\2\2\2I\u0161"+
		"\3\2\2\2K\u0163\3\2\2\2M\u0165\3\2\2\2O\u0167\3\2\2\2Q\u0169\3\2\2\2S"+
		"\u016b\3\2\2\2U\u016d\3\2\2\2W\u016f\3\2\2\2Y\u0171\3\2\2\2[\u0173\3\2"+
		"\2\2]\u0175\3\2\2\2_\u0177\3\2\2\2a\u0179\3\2\2\2c\u017f\3\2\2\2e\u0184"+
		"\3\2\2\2g\u0187\3\2\2\2i\u018a\3\2\2\2k\u018d\3\2\2\2m\u0190\3\2\2\2o"+
		"\u0193\3\2\2\2q\u0195\3\2\2\2s\u0198\3\2\2\2u\u019a\3\2\2\2w\u019d\3\2"+
		"\2\2y\u01a0\3\2\2\2{\u01a3\3\2\2\2}\u01a6\3\2\2\2\177\u01aa\3\2\2\2\u0081"+
		"\u01ae\3\2\2\2\u0083\u01b1\3\2\2\2\u0085\u01b4\3\2\2\2\u0087\u01b7\3\2"+
		"\2\2\u0089\u01ba\3\2\2\2\u008b\u01bd\3\2\2\2\u008d\u01c5\3\2\2\2\u008f"+
		"\u01d4\3\2\2\2\u0091\u01dc\3\2\2\2\u0093\u01e3\3\2\2\2\u0095\u01e8\3\2"+
		"\2\2\u0097\u01f1\3\2\2\2\u0099\u01fa\3\2\2\2\u009b\u0203\3\2\2\2\u009d"+
		"\u020b\3\2\2\2\u009f\u021e\3\2\2\2\u00a1\u0235\3\2\2\2\u00a3\u0246\3\2"+
		"\2\2\u00a5\u0254\3\2\2\2\u00a7\u025a\3\2\2\2\u00a9\u0260\3\2\2\2\u00ab"+
		"\u0266\3\2\2\2\u00ad\u026f\3\2\2\2\u00af\u0276\3\2\2\2\u00b1\u027d\3\2"+
		"\2\2\u00b3\u0286\3\2\2\2\u00b5\u028e\3\2\2\2\u00b7\u029d\3\2\2\2\u00b9"+
		"\u02a4\3\2\2\2\u00bb\u02a8\3\2\2\2\u00bd\u02ae\3\2\2\2\u00bf\u02b5\3\2"+
		"\2\2\u00c1\u02bb\3\2\2\2\u00c3\u02c0\3\2\2\2\u00c5\u02c5\3\2\2\2\u00c7"+
		"\u02cd\3\2\2\2\u00c9\u02d6\3\2\2\2\u00cb\u02db\3\2\2\2\u00cd\u02e8\3\2"+
		"\2\2\u00cf\u02f1\3\2\2\2\u00d1\u02fe\3\2\2\2\u00d3\u0306\3\2\2\2\u00d5"+
		"\u0313\3\2\2\2\u00d7\u031d\3\2\2\2\u00d9\u032f\3\2\2\2\u00db\u0345\3\2"+
		"\2\2\u00dd\u0359\3\2\2\2\u00df\u035e\3\2\2\2\u00e1\u0374\3\2\2\2\u00e3"+
		"\u0381\3\2\2\2\u00e5\u0387\3\2\2\2\u00e7\u038c\3\2\2\2\u00e9\u039b\3\2"+
		"\2\2\u00eb\u03a3\3\2\2\2\u00ed\u03a8\3\2\2\2\u00ef\u03ae\3\2\2\2\u00f1"+
		"\u03b3\3\2\2\2\u00f3\u03b9\3\2\2\2\u00f5\u03be\3\2\2\2\u00f7\u03c1\3\2"+
		"\2\2\u00f9\u03d2\3\2\2\2\u00fb\u03db\3\2\2\2\u00fd\u03e6\3\2\2\2\u00ff"+
		"\u03f1\3\2\2\2\u0101\u03f9\3\2\2\2\u0103\u03ff\3\2\2\2\u0105\u040b\3\2"+
		"\2\2\u0107\u0411\3\2\2\2\u0109\u041a\3\2\2\2\u010b\u0423\3\2\2\2\u010d"+
		"\u042a\3\2\2\2\u010f\u0430\3\2\2\2\u0111\u0440\3\2\2\2\u0113\u0447\3\2"+
		"\2\2\u0115\u044d\3\2\2\2\u0117\u0454\3\2\2\2\u0119\u045c\3\2\2\2\u011b"+
		"\u011c\t\2\2\2\u011c\4\3\2\2\2\u011d\u011e\t\3\2\2\u011e\6\3\2\2\2\u011f"+
		"\u0120\t\4\2\2\u0120\b\3\2\2\2\u0121\u0122\t\5\2\2\u0122\n\3\2\2\2\u0123"+
		"\u0124\t\6\2\2\u0124\f\3\2\2\2\u0125\u0126\t\7\2\2\u0126\16\3\2\2\2\u0127"+
		"\u0128\t\b\2\2\u0128\20\3\2\2\2\u0129\u012a\t\t\2\2\u012a\22\3\2\2\2\u012b"+
		"\u012c\t\n\2\2\u012c\24\3\2\2\2\u012d\u012e\t\13\2\2\u012e\26\3\2\2\2"+
		"\u012f\u0130\t\f\2\2\u0130\30\3\2\2\2\u0131\u0132\t\r\2\2\u0132\32\3\2"+
		"\2\2\u0133\u0134\t\16\2\2\u0134\34\3\2\2\2\u0135\u0136\t\17\2\2\u0136"+
		"\36\3\2\2\2\u0137\u0138\t\20\2\2\u0138 \3\2\2\2\u0139\u013a\t\21\2\2\u013a"+
		"\"\3\2\2\2\u013b\u013c\t\22\2\2\u013c$\3\2\2\2\u013d\u013e\t\23\2\2\u013e"+
		"&\3\2\2\2\u013f\u0140\t\24\2\2\u0140(\3\2\2\2\u0141\u0142\t\25\2\2\u0142"+
		"*\3\2\2\2\u0143\u0144\t\26\2\2\u0144,\3\2\2\2\u0145\u0146\t\27\2\2\u0146"+
		".\3\2\2\2\u0147\u0148\t\30\2\2\u0148\60\3\2\2\2\u0149\u014a\t\31\2\2\u014a"+
		"\62\3\2\2\2\u014b\u014c\t\32\2\2\u014c\64\3\2\2\2\u014d\u014e\t\33\2\2"+
		"\u014e\66\3\2\2\2\u014f\u0150\7*\2\2\u01508\3\2\2\2\u0151\u0152\7+\2\2"+
		"\u0152:\3\2\2\2\u0153\u0154\7}\2\2\u0154<\3\2\2\2\u0155\u0156\7\177\2"+
		"\2\u0156>\3\2\2\2\u0157\u0158\7]\2\2\u0158@\3\2\2\2\u0159\u015a\7_\2\2"+
		"\u015aB\3\2\2\2\u015b\u015c\7=\2\2\u015cD\3\2\2\2\u015d\u015e\7.\2\2\u015e"+
		"F\3\2\2\2\u015f\u0160\7?\2\2\u0160H\3\2\2\2\u0161\u0162\7<\2\2\u0162J"+
		"\3\2\2\2\u0163\u0164\7(\2\2\u0164L\3\2\2\2\u0165\u0166\7-\2\2\u0166N\3"+
		"\2\2\2\u0167\u0168\7/\2\2\u0168P\3\2\2\2\u0169\u016a\7#\2\2\u016aR\3\2"+
		"\2\2\u016b\u016c\7\u0080\2\2\u016cT\3\2\2\2\u016d\u016e\7,\2\2\u016eV"+
		"\3\2\2\2\u016f\u0170\7\61\2\2\u0170X\3\2\2\2\u0171\u0172\7\'\2\2\u0172"+
		"Z\3\2\2\2\u0173\u0174\7`\2\2\u0174\\\3\2\2\2\u0175\u0176\7~\2\2\u0176"+
		"^\3\2\2\2\u0177\u0178\7A\2\2\u0178`\3\2\2\2\u0179\u017a\7W\2\2\u017a\u017b"+
		"\7P\2\2\u017b\u017c\7C\2\2\u017c\u017d\7T\2\2\u017d\u017e\7[\2\2\u017e"+
		"b\3\2\2\2\u017f\u0180\7P\2\2\u0180\u0181\7G\2\2\u0181\u0182\7Z\2\2\u0182"+
		"\u0183\7V\2\2\u0183d\3\2\2\2\u0184\u0185\7>\2\2\u0185\u0186\7>\2\2\u0186"+
		"f\3\2\2\2\u0187\u0188\7@\2\2\u0188\u0189\7@\2\2\u0189h\3\2\2\2\u018a\u018b"+
		"\7?\2\2\u018b\u018c\7?\2\2\u018cj\3\2\2\2\u018d\u018e\7#\2\2\u018e\u018f"+
		"\7?\2\2\u018fl\3\2\2\2\u0190\u0191\7>\2\2\u0191\u0192\7?\2\2\u0192n\3"+
		"\2\2\2\u0193\u0194\7>\2\2\u0194p\3\2\2\2\u0195\u0196\7@\2\2\u0196\u0197"+
		"\7?\2\2\u0197r\3\2\2\2\u0198\u0199\7@\2\2\u0199t\3\2\2\2\u019a\u019b\7"+
		"-\2\2\u019b\u019c\7?\2\2\u019cv\3\2\2\2\u019d\u019e\7/\2\2\u019e\u019f"+
		"\7?\2\2\u019fx\3\2\2\2\u01a0\u01a1\7,\2\2\u01a1\u01a2\7?\2\2\u01a2z\3"+
		"\2\2\2\u01a3\u01a4\7\61\2\2\u01a4\u01a5\7?\2\2\u01a5|\3\2\2\2\u01a6\u01a7"+
		"\7>\2\2\u01a7\u01a8\7>\2\2\u01a8\u01a9\7?\2\2\u01a9~\3\2\2\2\u01aa\u01ab"+
		"\7@\2\2\u01ab\u01ac\7@\2\2\u01ac\u01ad\7?\2\2\u01ad\u0080\3\2\2\2\u01ae"+
		"\u01af\7(\2\2\u01af\u01b0\7?\2\2\u01b0\u0082\3\2\2\2\u01b1\u01b2\7~\2"+
		"\2\u01b2\u01b3\7?\2\2\u01b3\u0084\3\2\2\2\u01b4\u01b5\7`\2\2\u01b5\u01b6"+
		"\7?\2\2\u01b6\u0086\3\2\2\2\u01b7\u01b8\7(\2\2\u01b8\u01b9\7(\2\2\u01b9"+
		"\u0088\3\2\2\2\u01ba\u01bb\7~\2\2\u01bb\u01bc\7~\2\2\u01bc\u008a\3\2\2"+
		"\2\u01bd\u01be\7F\2\2\u01be\u01bf\7G\2\2\u01bf\u01c0\7H\2\2\u01c0\u01c1"+
		"\7K\2\2\u01c1\u01c2\7P\2\2\u01c2\u01c3\7G\2\2\u01c3\u01c4\7F\2\2\u01c4"+
		"\u008c\3\2\2\2\u01c5\u01c6\7U\2\2\u01c6\u01c7\7K\2\2\u01c7\u01c8\7\\\2"+
		"\2\u01c8\u01c9\7G\2\2\u01c9\u01ca\7Q\2\2\u01ca\u01cb\7H\2\2\u01cb\u01cc"+
		"\7a\2\2\u01cc\u01cd\7J\2\2\u01cd\u01ce\7G\2\2\u01ce\u01cf\7C\2\2\u01cf"+
		"\u01d0\7F\2\2\u01d0\u01d1\7G\2\2\u01d1\u01d2\7T\2\2\u01d2\u01d3\7U\2\2"+
		"\u01d3\u008e\3\2\2\2\u01d4\u01d5\7C\2\2\u01d5\u01d6\7N\2\2\u01d6\u01d7"+
		"\7K\2\2\u01d7\u01d8\7I\2\2\u01d8\u01d9\7P\2\2\u01d9\u01da\7Q\2\2\u01da"+
		"\u01db\7H\2\2\u01db\u0090\3\2\2\2\u01dc\u01dd\7U\2\2\u01dd\u01de\7K\2"+
		"\2\u01de\u01df\7\\\2\2\u01df\u01e0\7G\2\2\u01e0\u01e1\7Q\2\2\u01e1\u01e2"+
		"\7H\2\2\u01e2\u0092\3\2\2\2\u01e3\u01e4\7C\2\2\u01e4\u01e5\7F\2\2\u01e5"+
		"\u01e6\7F\2\2\u01e6\u01e7\7T\2\2\u01e7\u0094\3\2\2\2\u01e8\u01e9\7N\2"+
		"\2\u01e9\u01ea\7Q\2\2\u01ea\u01eb\7C\2\2\u01eb\u01ec\7F\2\2\u01ec\u01ed"+
		"\7C\2\2\u01ed\u01ee\7F\2\2\u01ee\u01ef\7F\2\2\u01ef\u01f0\7T\2\2\u01f0"+
		"\u0096\3\2\2\2\u01f1\u01f2\7E\2\2\u01f2\u01f3\7Q\2\2\u01f3\u01f4\7P\2"+
		"\2\u01f4\u01f5\7U\2\2\u01f5\u01f6\7V\2\2\u01f6\u01f7\7C\2\2\u01f7\u01f8"+
		"\7P\2\2\u01f8\u01f9\7V\2\2\u01f9\u0098\3\2\2\2\u01fa\u01fb\7C\2\2\u01fb"+
		"\u01fc\7D\2\2\u01fc\u01fd\7U\2\2\u01fd\u01fe\7Q\2\2\u01fe\u01ff\7N\2\2"+
		"\u01ff\u0200\7W\2\2\u0200\u0201\7V\2\2\u0201\u0202\7G\2\2\u0202\u009a"+
		"\3\2\2\2\u0203\u0204\7C\2\2\u0204\u0205\7N\2\2\u0205\u0206\7K\2\2\u0206"+
		"\u0207\7I\2\2\u0207\u0208\7P\2\2\u0208\u0209\7a\2\2\u0209\u020a\7M\2\2"+
		"\u020a\u009c\3\2\2\2\u020b\u020c\7F\2\2\u020c\u020d\7C\2\2\u020d\u020e"+
		"\7V\2\2\u020e\u020f\7C\2\2\u020f\u0210\7a\2\2\u0210\u0211\7U\2\2\u0211"+
		"\u0212\7G\2\2\u0212\u0213\7I\2\2\u0213\u0214\7O\2\2\u0214\u0215\7G\2\2"+
		"\u0215\u0216\7P\2\2\u0216\u0217\7V\2\2\u0217\u0218\7a\2\2\u0218\u0219"+
		"\7C\2\2\u0219\u021a\7N\2\2\u021a\u021b\7K\2\2\u021b\u021c\7I\2\2\u021c"+
		"\u021d\7P\2\2\u021d\u009e\3\2\2\2\u021e\u021f\7F\2\2\u021f\u0220\7C\2"+
		"\2\u0220\u0221\7V\2\2\u0221\u0222\7C\2\2\u0222\u0223\7a\2\2\u0223\u0224"+
		"\7U\2\2\u0224\u0225\7G\2\2\u0225\u0226\7I\2\2\u0226\u0227\7O\2\2\u0227"+
		"\u0228\7G\2\2\u0228\u0229\7P\2\2\u0229\u022a\7V\2\2\u022a\u022b\7a\2\2"+
		"\u022b\u022c\7T\2\2\u022c\u022d\7G\2\2\u022d\u022e\7N\2\2\u022e\u022f"+
		"\7T\2\2\u022f\u0230\7Q\2\2\u0230\u0231\7a\2\2\u0231\u0232\7G\2\2\u0232"+
		"\u0233\7P\2\2\u0233\u0234\7F\2\2\u0234\u00a0\3\2\2\2\u0235\u0236\7F\2"+
		"\2\u0236\u0237\7C\2\2\u0237\u0238\7V\2\2\u0238\u0239\7C\2\2\u0239\u023a"+
		"\7a\2\2\u023a\u023b\7U\2\2\u023b\u023c\7G\2\2\u023c\u023d\7I\2\2\u023d"+
		"\u023e\7O\2\2\u023e\u023f\7G\2\2\u023f\u0240\7P\2\2\u0240\u0241\7V\2\2"+
		"\u0241\u0242\7a\2\2\u0242\u0243\7G\2\2\u0243\u0244\7P\2\2\u0244\u0245"+
		"\7F\2\2\u0245\u00a2\3\2\2\2\u0246\u0247\7U\2\2\u0247\u0248\7G\2\2\u0248"+
		"\u0249\7I\2\2\u0249\u024a\7O\2\2\u024a\u024b\7G\2\2\u024b\u024c\7P\2\2"+
		"\u024c\u024d\7V\2\2\u024d\u024e\7a\2\2\u024e\u024f\7U\2\2\u024f\u0250"+
		"\7V\2\2\u0250\u0251\7C\2\2\u0251\u0252\7T\2\2\u0252\u0253\7V\2\2\u0253"+
		"\u00a4\3\2\2\2\u0254\u0255\7D\2\2\u0255\u0256\7N\2\2\u0256\u0257\7Q\2"+
		"\2\u0257\u0258\7E\2\2\u0258\u0259\7M\2\2\u0259\u00a6\3\2\2\2\u025a\u025b"+
		"\7O\2\2\u025b\u025c\7C\2\2\u025c\u025d\7Z\2\2\u025d\u025e\7a\2\2\u025e"+
		"\u025f\7M\2\2\u025f\u00a8\3\2\2\2\u0260\u0261\7O\2\2\u0261\u0262\7K\2"+
		"\2\u0262\u0263\7P\2\2\u0263\u0264\7a\2\2\u0264\u0265\7M\2\2\u0265\u00aa"+
		"\3\2\2\2\u0266\u0267\7C\2\2\u0267\u0268\7U\2\2\u0268\u0269\7U\2\2\u0269"+
		"\u026a\7G\2\2\u026a\u026b\7T\2\2\u026b\u026c\7V\2\2\u026c\u026d\7a\2\2"+
		"\u026d\u026e\7M\2\2\u026e\u00ac\3\2\2\2\u026f\u0270\7Q\2\2\u0270\u0271"+
		"\7T\2\2\u0271\u0272\7K\2\2\u0272\u0273\7I\2\2\u0273\u0274\7K\2\2\u0274"+
		"\u0275\7P\2\2\u0275\u00ae\3\2\2\2\u0276\u0277\7N\2\2\u0277\u0278\7G\2"+
		"\2\u0278\u0279\7P\2\2\u0279\u027a\7I\2\2\u027a\u027b\7V\2\2\u027b\u027c"+
		"\7J\2\2\u027c\u00b0\3\2\2\2\u027d\u027e\7N\2\2\u027e\u027f\7Q\2\2\u027f"+
		"\u0280\7I\2\2\u0280\u0281\7\64\2\2\u0281\u0282\7E\2\2\u0282\u0283\7G\2"+
		"\2\u0283\u0284\7K\2\2\u0284\u0285\7N\2\2\u0285\u00b2\3\2\2\2\u0286\u0287"+
		"\7R\2\2\u0287\u0288\7T\2\2\u0288\u0289\7Q\2\2\u0289\u028a\7X\2\2\u028a"+
		"\u028b\7K\2\2\u028b\u028c\7F\2\2\u028c\u028d\7G\2\2\u028d\u00b4\3\2\2"+
		"\2\u028e\u028f\7R\2\2\u028f\u0290\7T\2\2\u0290\u0291\7Q\2\2\u0291\u0292"+
		"\7X\2\2\u0292\u0293\7K\2\2\u0293\u0294\7F\2\2\u0294\u0295\7G\2\2\u0295"+
		"\u0296\7a\2\2\u0296\u0297\7J\2\2\u0297\u0298\7K\2\2\u0298\u0299\7F\2\2"+
		"\u0299\u029a\7F\2\2\u029a\u029b\7G\2\2\u029b\u029c\7P\2\2\u029c\u00b6"+
		"\3\2\2\2\u029d\u029e\7J\2\2\u029e\u029f\7K\2\2\u029f\u02a0\7F\2\2\u02a0"+
		"\u02a1\7F\2\2\u02a1\u02a2\7G\2\2\u02a2\u02a3\7P\2\2\u02a3\u00b8\3\2\2"+
		"\2\u02a4\u02a5\7G\2\2\u02a5\u02a6\7P\2\2\u02a6\u02a7\7F\2\2\u02a7\u00ba"+
		"\3\2\2\2\u02a8\u02a9\7G\2\2\u02a9\u02aa\7P\2\2\u02aa\u02ab\7V\2\2\u02ab"+
		"\u02ac\7T\2\2\u02ac\u02ad\7[\2\2\u02ad\u00bc\3\2\2\2\u02ae\u02af\7P\2"+
		"\2\u02af\u02b0\7Q\2\2\u02b0\u02b1\7N\2\2\u02b1\u02b2\7Q\2\2\u02b2\u02b3"+
		"\7C\2\2\u02b3\u02b4\7F\2\2\u02b4\u00be\3\2\2\2\u02b5\u02b6\7F\2\2\u02b6"+
		"\u02b7\7U\2\2\u02b7\u02b8\7G\2\2\u02b8\u02b9\7E\2\2\u02b9\u02ba\7V\2\2"+
		"\u02ba\u00c0\3\2\2\2\u02bb\u02bc\7E\2\2\u02bc\u02bd\7Q\2\2\u02bd\u02be"+
		"\7R\2\2\u02be\u02bf\7[\2\2\u02bf\u00c2\3\2\2\2\u02c0\u02c1\7K\2\2\u02c1"+
		"\u02c2\7P\2\2\u02c2\u02c3\7H\2\2\u02c3\u02c4\7Q\2\2\u02c4\u00c4\3\2\2"+
		"\2\u02c5\u02c6\7Q\2\2\u02c6\u02c7\7X\2\2\u02c7\u02c8\7G\2\2\u02c8\u02c9"+
		"\7T\2\2\u02c9\u02ca\7N\2\2\u02ca\u02cb\7C\2\2\u02cb\u02cc\7[\2\2\u02cc"+
		"\u00c6\3\2\2\2\u02cd\u02ce\7T\2\2\u02ce\u02cf\7G\2\2\u02cf\u02d0\7C\2"+
		"\2\u02d0\u02d1\7F\2\2\u02d1\u02d2\7Q\2\2\u02d2\u02d3\7P\2\2\u02d3\u02d4"+
		"\7N\2\2\u02d4\u02d5\7[\2\2\u02d5\u00c8\3\2\2\2\u02d6\u02d7\7V\2\2\u02d7"+
		"\u02d8\7[\2\2\u02d8\u02d9\7R\2\2\u02d9\u02da\7G\2\2\u02da\u00ca\3\2\2"+
		"\2\u02db\u02dc\7K\2\2\u02dc\u02dd\7P\2\2\u02dd\u02de\7R\2\2\u02de\u02df"+
		"\7W\2\2\u02df\u02e0\7V\2\2\u02e0\u02e1\7a\2\2\u02e1\u02e2\7U\2\2\u02e2"+
		"\u02e3\7E\2\2\u02e3\u02e4\7T\2\2\u02e4\u02e5\7K\2\2\u02e5\u02e6\7R\2\2"+
		"\u02e6\u02e7\7V\2\2\u02e7\u00cc\3\2\2\2\u02e8\u02e9\7U\2\2\u02e9\u02ea"+
		"\7G\2\2\u02ea\u02eb\7E\2\2\u02eb\u02ec\7V\2\2\u02ec\u02ed\7K\2\2\u02ed"+
		"\u02ee\7Q\2\2\u02ee\u02ef\7P\2\2\u02ef\u02f0\7U\2\2\u02f0\u00ce\3\2\2"+
		"\2\u02f1\u02f2\7G\2\2\u02f2\u02f3\7Z\2\2\u02f3\u02f4\7E\2\2\u02f4\u02f5"+
		"\7N\2\2\u02f5\u02f6\7W\2\2\u02f6\u02f7\7F\2\2\u02f7\u02f8\7G\2\2\u02f8"+
		"\u02f9\7a\2\2\u02f9\u02fa\7H\2\2\u02fa\u02fb\7K\2\2\u02fb\u02fc\7N\2\2"+
		"\u02fc\u02fd\7G\2\2\u02fd\u00d0\3\2\2\2\u02fe\u02ff\7T\2\2\u02ff\u0300"+
		"\7G\2\2\u0300\u0301\7X\2\2\u0301\u0302\7G\2\2\u0302\u0303\7T\2\2\u0303"+
		"\u0304\7U\2\2\u0304\u0305\7G\2\2\u0305\u00d2\3\2\2\2\u0306\u0307\7U\2"+
		"\2\u0307\u0308\7Q\2\2\u0308\u0309\7T\2\2\u0309\u030a\7V\2\2\u030a\u030b"+
		"\7a\2\2\u030b\u030c\7D\2\2\u030c\u030d\7[\2\2\u030d\u030e\7a\2\2\u030e"+
		"\u030f\7P\2\2\u030f\u0310\7C\2\2\u0310\u0311\7O\2\2\u0311\u0312\7G\2\2"+
		"\u0312\u00d4\3\2\2\2\u0313\u0314\7U\2\2\u0314\u0315\7Q\2\2\u0315\u0316"+
		"\7T\2\2\u0316\u0317\7V\2\2\u0317\u0318\7a\2\2\u0318\u0319\7P\2\2\u0319"+
		"\u031a\7Q\2\2\u031a\u031b\7P\2\2\u031b\u031c\7G\2\2\u031c\u00d6\3\2\2"+
		"\2\u031d\u031e\7U\2\2\u031e\u031f\7Q\2\2\u031f\u0320\7T\2\2\u0320\u0321"+
		"\7V\2\2\u0321\u0322\7a\2\2\u0322\u0323\7D\2\2\u0323\u0324\7[\2\2\u0324"+
		"\u0325\7a\2\2\u0325\u0326\7C\2\2\u0326\u0327\7N\2\2\u0327\u0328\7K\2\2"+
		"\u0328\u0329\7I\2\2\u0329\u032a\7P\2\2\u032a\u032b\7O\2\2\u032b\u032c"+
		"\7G\2\2\u032c\u032d\7P\2\2\u032d\u032e\7V\2\2\u032e\u00d8\3\2\2\2\u032f"+
		"\u0330\7U\2\2\u0330\u0331\7Q\2\2\u0331\u0332\7T\2\2\u0332\u0333\7V\2\2"+
		"\u0333\u0334\7a\2\2\u0334\u0335\7D\2\2\u0335\u0336\7[\2\2\u0336\u0337"+
		"\7a\2\2\u0337\u0338\7K\2\2\u0338\u0339\7P\2\2\u0339\u033a\7K\2\2\u033a"+
		"\u033b\7V\2\2\u033b\u033c\7a\2\2\u033c\u033d\7R\2\2\u033d\u033e\7T\2\2"+
		"\u033e\u033f\7K\2\2\u033f\u0340\7Q\2\2\u0340\u0341\7T\2\2\u0341\u0342"+
		"\7K\2\2\u0342\u0343\7V\2\2\u0343\u0344\7[\2\2\u0344\u00da\3\2\2\2\u0345"+
		"\u0346\7K\2\2\u0346\u0347\7P\2\2\u0347\u0348\7R\2\2\u0348\u0349\7W\2\2"+
		"\u0349\u034a\7V\2\2\u034a\u034b\7a\2\2\u034b\u034c\7U\2\2\u034c\u034d"+
		"\7G\2\2\u034d\u034e\7E\2\2\u034e\u034f\7V\2\2\u034f\u0350\7K\2\2\u0350"+
		"\u0351\7Q\2\2\u0351\u0352\7P\2\2\u0352\u0353\7a\2\2\u0353\u0354\7H\2\2"+
		"\u0354\u0355\7N\2\2\u0355\u0356\7C\2\2\u0356\u0357\7I\2\2\u0357\u0358"+
		"\7U\2\2\u0358\u00dc\3\2\2\2\u0359\u035a\7M\2\2\u035a\u035b\7G\2\2\u035b"+
		"\u035c\7G\2\2\u035c\u035d\7R\2\2\u035d\u00de\3\2\2\2\u035e\u035f\7E\2"+
		"\2\u035f\u0360\7T\2\2\u0360\u0361\7G\2\2\u0361\u0362\7C\2\2\u0362\u0363"+
		"\7V\2\2\u0363\u0364\7G\2\2\u0364\u0365\7a\2\2\u0365\u0366\7Q\2\2\u0366"+
		"\u0367\7D\2\2\u0367\u0368\7L\2\2\u0368\u0369\7G\2\2\u0369\u036a\7E\2\2"+
		"\u036a\u036b\7V\2\2\u036b\u036c\7a\2\2\u036c\u036d\7U\2\2\u036d\u036e"+
		"\7[\2\2\u036e\u036f\7O\2\2\u036f\u0370\7D\2\2\u0370\u0371\7Q\2\2\u0371"+
		"\u0372\7N\2\2\u0372\u0373\7U\2\2\u0373\u00e0\3\2\2\2\u0374\u0375\7E\2"+
		"\2\u0375\u0376\7Q\2\2\u0376\u0377\7P\2\2\u0377\u0378\7U\2\2\u0378\u0379"+
		"\7V\2\2\u0379\u037a\7T\2\2\u037a\u037b\7W\2\2\u037b\u037c\7E\2\2\u037c"+
		"\u037d\7V\2\2\u037d\u037e\7Q\2\2\u037e\u037f\7T\2\2\u037f\u0380\7U\2\2"+
		"\u0380\u00e2\3\2\2\2\u0381\u0382\7C\2\2\u0382\u0383\7U\2\2\u0383\u0384"+
		"\7E\2\2\u0384\u0385\7K\2\2\u0385\u0386\7\\\2\2\u0386\u00e4\3\2\2\2\u0387"+
		"\u0388\7H\2\2\u0388\u0389\7K\2\2\u0389\u038a\7N\2\2\u038a\u038b\7N\2\2"+
		"\u038b\u00e6\3\2\2\2\u038c\u038d\7N\2\2\u038d\u038e\7K\2\2\u038e\u038f"+
		"\7P\2\2\u038f\u0390\7M\2\2\u0390\u0391\7G\2\2\u0391\u0392\7T\2\2\u0392"+
		"\u0393\7a\2\2\u0393\u0394\7X\2\2\u0394\u0395\7G\2\2\u0395\u0396\7T\2\2"+
		"\u0396\u0397\7U\2\2\u0397\u0398\7K\2\2\u0398\u0399\7Q\2\2\u0399\u039a"+
		"\7P\2\2\u039a\u00e8\3\2\2\2\u039b\u039c\7K\2\2\u039c\u039d\7P\2\2\u039d"+
		"\u039e\7E\2\2\u039e\u039f\7N\2\2\u039f\u03a0\7W\2\2\u03a0\u03a1\7F\2\2"+
		"\u03a1\u03a2\7G\2\2\u03a2\u00ea\3\2\2\2\u03a3\u03a4\7S\2\2\u03a4\u03a5"+
		"\7W\2\2\u03a5\u03a6\7C\2\2\u03a6\u03a7\7F\2\2\u03a7\u00ec\3\2\2\2\u03a8"+
		"\u03a9\7U\2\2\u03a9\u03aa\7S\2\2\u03aa\u03ab\7W\2\2\u03ab\u03ac\7C\2\2"+
		"\u03ac\u03ad\7F\2\2\u03ad\u00ee\3\2\2\2\u03ae\u03af\7N\2\2\u03af\u03b0"+
		"\7Q\2\2\u03b0\u03b1\7P\2\2\u03b1\u03b2\7I\2\2\u03b2\u00f0\3\2\2\2\u03b3"+
		"\u03b4\7U\2\2\u03b4\u03b5\7J\2\2\u03b5\u03b6\7Q\2\2\u03b6\u03b7\7T\2\2"+
		"\u03b7\u03b8\7V\2\2\u03b8\u00f2\3\2\2\2\u03b9\u03ba\7D\2\2\u03ba\u03bb"+
		"\7[\2\2\u03bb\u03bc\7V\2\2\u03bc\u03bd\7G\2\2\u03bd\u00f4\3\2\2\2\u03be"+
		"\u03bf\7C\2\2\u03bf\u03c0\7V\2\2\u03c0\u00f6\3\2\2\2\u03c1\u03c2\7C\2"+
		"\2\u03c2\u03c3\7N\2\2\u03c3\u03c4\7K\2\2\u03c4\u03c5\7I\2\2\u03c5\u03c6"+
		"\7P\2\2\u03c6\u03c7\7a\2\2\u03c7\u03c8\7Y\2\2\u03c8\u03c9\7K\2\2\u03c9"+
		"\u03ca\7V\2\2\u03ca\u03cb\7J\2\2\u03cb\u03cc\7a\2\2\u03cc\u03cd\7K\2\2"+
		"\u03cd\u03ce\7P\2\2\u03ce\u03cf\7R\2\2\u03cf\u03d0\7W\2\2\u03d0\u03d1"+
		"\7V\2\2\u03d1\u00f8\3\2\2\2\u03d2\u03d3\7U\2\2\u03d3\u03d4\7W\2\2\u03d4"+
		"\u03d5\7D\2\2\u03d5\u03d6\7C\2\2\u03d6\u03d7\7N\2\2\u03d7\u03d8\7K\2\2"+
		"\u03d8\u03d9\7I\2\2\u03d9\u03da\7P\2\2\u03da\u00fa\3\2\2\2\u03db\u03dc"+
		"\7Q\2\2\u03dc\u03dd\7P\2\2\u03dd\u03de\7N\2\2\u03de\u03df\7[\2\2\u03df"+
		"\u03e0\7a\2\2\u03e0\u03e1\7K\2\2\u03e1\u03e2\7H\2\2\u03e2\u03e3\7a\2\2"+
		"\u03e3\u03e4\7T\2\2\u03e4\u03e5\7Q\2\2\u03e5\u00fc\3\2\2\2\u03e6\u03e7"+
		"\7Q\2\2\u03e7\u03e8\7P\2\2\u03e8\u03e9\7N\2\2\u03e9\u03ea\7[\2\2\u03ea"+
		"\u03eb\7a\2\2\u03eb\u03ec\7K\2\2\u03ec\u03ed\7H\2\2\u03ed\u03ee\7a\2\2"+
		"\u03ee\u03ef\7T\2\2\u03ef\u03f0\7Y\2\2\u03f0\u00fe\3\2\2\2\u03f1\u03f2"+
		"\7U\2\2\u03f2\u03f3\7R\2\2\u03f3\u03f4\7G\2\2\u03f4\u03f5\7E\2\2\u03f5"+
		"\u03f6\7K\2\2\u03f6\u03f7\7C\2\2\u03f7\u03f8\7N\2\2\u03f8\u0100\3\2\2"+
		"\2\u03f9\u03fa\7I\2\2\u03fa\u03fb\7T\2\2\u03fb\u03fc\7Q\2\2\u03fc\u03fd"+
		"\7W\2\2\u03fd\u03fe\7R\2\2\u03fe\u0102\3\2\2\2\u03ff\u0400\7P\2\2\u0400"+
		"\u0401\7Q\2\2\u0401\u0402\7E\2\2\u0402\u0403\7T\2\2\u0403\u0404\7Q\2\2"+
		"\u0404\u0405\7U\2\2\u0405\u0406\7U\2\2\u0406\u0407\7T\2\2\u0407\u0408"+
		"\7G\2\2\u0408\u0409\7H\2\2\u0409\u040a\7U\2\2\u040a\u0104\3\2\2\2\u040b"+
		"\u040c\7R\2\2\u040c\u040d\7J\2\2\u040d\u040e\7F\2\2\u040e\u040f\7T\2\2"+
		"\u040f\u0410\7U\2\2\u0410\u0106\3\2\2\2\u0411\u0412\7X\2\2\u0412\u0413"+
		"\7G\2\2\u0413\u0414\7T\2\2\u0414\u0415\7U\2\2\u0415\u0416\7K\2\2\u0416"+
		"\u0417\7Q\2\2\u0417\u0418\7P\2\2\u0418\u0419\7M\2\2\u0419\u0108\3\2\2"+
		"\2\u041a\u041b\7X\2\2\u041b\u041c\7G\2\2\u041c\u041d\7T\2\2\u041d\u041e"+
		"\7U\2\2\u041e\u041f\7a\2\2\u041f\u0420\7V\2\2\u0420\u0421\7C\2\2\u0421"+
		"\u0422\7I\2\2\u0422\u010a\3\2\2\2\u0423\u0424\7I\2\2\u0424\u0425\7N\2"+
		"\2\u0425\u0426\7Q\2\2\u0426\u0427\7D\2\2\u0427\u0428\7C\2\2\u0428\u0429"+
		"\7N\2\2\u0429\u010c\3\2\2\2\u042a\u042b\7N\2\2\u042b\u042c\7Q\2\2\u042c"+
		"\u042d\7E\2\2\u042d\u042e\7C\2\2\u042e\u042f\7N\2\2\u042f\u010e\3\2\2"+
		"\2\u0430\u0431\7X\2\2\u0431\u0432\7G\2\2\u0432\u0433\7T\2\2\u0433\u0434"+
		"\7U\2\2\u0434\u0435\7a\2\2\u0435\u0436\7K\2\2\u0436\u0437\7F\2\2\u0437"+
		"\u0438\7G\2\2\u0438\u0439\7P\2\2\u0439\u043a\7V\2\2\u043a\u043b\7K\2\2"+
		"\u043b\u043c\7H\2\2\u043c\u043d\7K\2\2\u043d\u043e\7G\2\2\u043e\u043f"+
		"\7T\2\2\u043f\u0110\3\2\2\2\u0440\u0441\7G\2\2\u0441\u0442\7Z\2\2\u0442"+
		"\u0443\7V\2\2\u0443\u0444\7G\2\2\u0444\u0445\7T\2\2\u0445\u0446\7P\2\2"+
		"\u0446\u0112\3\2\2\2\u0447\u0448\7D\2\2\u0448\u0449\7K\2\2\u0449\u044a"+
		"\7P\2\2\u044a\u044b\7F\2\2\u044b\u0114\3\2\2\2\u044c\u044e\7\60\2\2\u044d"+
		"\u044c\3\2\2\2\u044d\u044e\3\2\2\2\u044e\u0450\3\2\2\2\u044f\u0451\t\34"+
		"\2\2\u0450\u044f\3\2\2\2\u0451\u0452\3\2\2\2\u0452\u0450\3\2\2\2\u0452"+
		"\u0453\3\2\2\2\u0453\u0116\3\2\2\2\u0454\u0455\7\62\2\2\u0455\u0457\7"+
		"z\2\2\u0456\u0458\t\35\2\2\u0457\u0456\3\2\2\2\u0458\u0459\3\2\2\2\u0459"+
		"\u0457\3\2\2\2\u0459\u045a\3\2\2\2\u045a\u0118\3\2\2\2\u045b\u045d\t\36"+
		"\2\2\u045c\u045b\3\2\2\2\u045d\u045e\3\2\2\2\u045e\u045c\3\2\2\2\u045e"+
		"\u045f\3\2\2\2\u045f\u0460\3\2\2\2\u0460\u0461\b\u008d\2\2\u0461\u011a"+
		"\3\2\2\2\7\2\u044d\u0452\u0459\u045e\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}