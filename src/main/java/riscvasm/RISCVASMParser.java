// Generated from riscvasm\RISCVASMParser.g4 by ANTLR 4.9.1
package riscvasm;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RISCVASMParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MODIFIER_HI=1, MODIFIER_LO=2, DOT_EQU=3, DOT_SECTION=4, DOT_GLOBL=5, DOT_GLOBAL=6, 
		DOT_TEXT=7, DOT_DATA=8, DOT_BYTE=9, DOT_SPACE=10, DOT_HALF=11, DOT_WORD=12, 
		DOT_DWORD=13, DOT_FILE=14, DOT_RODATA=15, DOT_ASCIZ=16, DOT_SKIP=17, DOT_STRING=18, 
		I_ADD=19, I_ADDI=20, I_AND=21, I_ANDI=22, I_AUIPC=23, I_BEQ=24, I_BEQZ=25, 
		I_BGE=26, I_BGT=27, I_BLT=28, I_BNE=29, I_BNEZ=30, I_CALL=31, I_ECALL=32, 
		I_J=33, I_JR=34, I_JALR=35, I_LA=36, I_LD=37, I_LW=38, I_LH=39, I_LB=40, 
		I_LBU=41, I_LI=42, I_LUI=43, I_MUL=44, I_MV=45, I_NOP=46, I_NOT=47, I_RET=48, 
		I_SRLI=49, I_SLLI=50, I_SD=51, I_SW=52, I_SH=53, I_SB=54, I_WFI=55, REG_ZERO_ABI=56, 
		REG_RA_ABI=57, REG_SP_ABI=58, REG_GP_ABI=59, REG_TP_ABI=60, REG_T0_ABI=61, 
		REG_T1_ABI=62, REG_T2_ABI=63, REG_T3_ABI=64, REG_T4_ABI=65, REG_T5_ABI=66, 
		REG_T6_ABI=67, REG_FP_ABI=68, REG_A0_ABI=69, REG_A1_ABI=70, REG_A2_ABI=71, 
		REG_A3_ABI=72, REG_A4_ABI=73, REG_A5_ABI=74, REG_A6_ABI=75, REG_A7_ABI=76, 
		REG_S0_ABI=77, REG_S1_ABI=78, REG_S2_ABI=79, REG_S3_ABI=80, REG_S4_ABI=81, 
		REG_S5_ABI=82, REG_S6_ABI=83, REG_S7_ABI=84, REG_S8_ABI=85, REG_S9_ABI=86, 
		REG_S10_ABI=87, REG_S11_ABI=88, REG_ZERO=89, REG_RA=90, REG_SP=91, REG_GP=92, 
		REG_TP=93, REG_T0=94, REG_T1=95, REG_T2=96, REG_S0=97, REG_S1=98, REG_A0=99, 
		REG_A1=100, REG_A2=101, REG_A3=102, REG_A4=103, REG_A5=104, REG_A6=105, 
		REG_A7=106, REG_S2=107, REG_S3=108, REG_S4=109, REG_S5=110, REG_S6=111, 
		REG_S7=112, REG_S8=113, REG_S9=114, REG_S10=115, REG_S11=116, REG_T3=117, 
		REG_T4=118, REG_T5=119, REG_T6=120, PERCENT=121, DOT=122, COLON=123, COMMA=124, 
		OPENING_BRACKET=125, CLOSING_BRACKET=126, ID=127, WS=128;
	public static final int
		RULE_asm_file = 0;
	private static String[] makeRuleNames() {
		return new String[] {
			"asm_file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "'%'", "'.'", "':'", "','", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MODIFIER_HI", "MODIFIER_LO", "DOT_EQU", "DOT_SECTION", "DOT_GLOBL", 
			"DOT_GLOBAL", "DOT_TEXT", "DOT_DATA", "DOT_BYTE", "DOT_SPACE", "DOT_HALF", 
			"DOT_WORD", "DOT_DWORD", "DOT_FILE", "DOT_RODATA", "DOT_ASCIZ", "DOT_SKIP", 
			"DOT_STRING", "I_ADD", "I_ADDI", "I_AND", "I_ANDI", "I_AUIPC", "I_BEQ", 
			"I_BEQZ", "I_BGE", "I_BGT", "I_BLT", "I_BNE", "I_BNEZ", "I_CALL", "I_ECALL", 
			"I_J", "I_JR", "I_JALR", "I_LA", "I_LD", "I_LW", "I_LH", "I_LB", "I_LBU", 
			"I_LI", "I_LUI", "I_MUL", "I_MV", "I_NOP", "I_NOT", "I_RET", "I_SRLI", 
			"I_SLLI", "I_SD", "I_SW", "I_SH", "I_SB", "I_WFI", "REG_ZERO_ABI", "REG_RA_ABI", 
			"REG_SP_ABI", "REG_GP_ABI", "REG_TP_ABI", "REG_T0_ABI", "REG_T1_ABI", 
			"REG_T2_ABI", "REG_T3_ABI", "REG_T4_ABI", "REG_T5_ABI", "REG_T6_ABI", 
			"REG_FP_ABI", "REG_A0_ABI", "REG_A1_ABI", "REG_A2_ABI", "REG_A3_ABI", 
			"REG_A4_ABI", "REG_A5_ABI", "REG_A6_ABI", "REG_A7_ABI", "REG_S0_ABI", 
			"REG_S1_ABI", "REG_S2_ABI", "REG_S3_ABI", "REG_S4_ABI", "REG_S5_ABI", 
			"REG_S6_ABI", "REG_S7_ABI", "REG_S8_ABI", "REG_S9_ABI", "REG_S10_ABI", 
			"REG_S11_ABI", "REG_ZERO", "REG_RA", "REG_SP", "REG_GP", "REG_TP", "REG_T0", 
			"REG_T1", "REG_T2", "REG_S0", "REG_S1", "REG_A0", "REG_A1", "REG_A2", 
			"REG_A3", "REG_A4", "REG_A5", "REG_A6", "REG_A7", "REG_S2", "REG_S3", 
			"REG_S4", "REG_S5", "REG_S6", "REG_S7", "REG_S8", "REG_S9", "REG_S10", 
			"REG_S11", "REG_T3", "REG_T4", "REG_T5", "REG_T6", "PERCENT", "DOT", 
			"COLON", "COMMA", "OPENING_BRACKET", "CLOSING_BRACKET", "ID", "WS"
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

	@Override
	public String getGrammarFileName() { return "RISCVASMParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RISCVASMParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class Asm_fileContext extends ParserRuleContext {
		public TerminalNode I_ADDI() { return getToken(RISCVASMParser.I_ADDI, 0); }
		public Asm_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_asm_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterAsm_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitAsm_file(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitAsm_file(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Asm_fileContext asm_file() throws RecognitionException {
		Asm_fileContext _localctx = new Asm_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_asm_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2);
			match(I_ADDI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\u0082\7\4\2\t\2\3"+
		"\2\3\2\3\2\2\2\3\2\2\2\2\5\2\4\3\2\2\2\4\5\7\26\2\2\5\3\3\2\2\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}