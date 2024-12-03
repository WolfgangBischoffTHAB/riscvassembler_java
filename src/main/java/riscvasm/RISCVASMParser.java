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
		OPENING_BRACKET=125, CLOSING_BRACKET=126, IDENTIFIER=127, WS=128, NEWLINE=129;
	public static final int
		RULE_asm_file = 0, RULE_asm_line = 1, RULE_label = 2, RULE_mnemonic = 3, 
		RULE_params = 4, RULE_param = 5, RULE_expr = 6, RULE_register = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"asm_file", "asm_line", "label", "mnemonic", "params", "param", "expr", 
			"register"
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
			"COLON", "COMMA", "OPENING_BRACKET", "CLOSING_BRACKET", "IDENTIFIER", 
			"WS", "NEWLINE"
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
		public List<Asm_lineContext> asm_line() {
			return getRuleContexts(Asm_lineContext.class);
		}
		public Asm_lineContext asm_line(int i) {
			return getRuleContext(Asm_lineContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(RISCVASMParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(RISCVASMParser.NEWLINE, i);
		}
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
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(20);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case I_ADDI:
					case IDENTIFIER:
						{
						{
						setState(16);
						asm_line();
						setState(17);
						match(NEWLINE);
						}
						}
						break;
					case NEWLINE:
						{
						{
						setState(19);
						match(NEWLINE);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(24);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			setState(32);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case I_ADDI:
			case IDENTIFIER:
				{
				setState(25);
				asm_line();
				}
				break;
			case EOF:
			case NEWLINE:
				{
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==NEWLINE) {
					{
					{
					setState(26);
					match(NEWLINE);
					}
					}
					setState(31);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
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

	public static class Asm_lineContext extends ParserRuleContext {
		public MnemonicContext mnemonic() {
			return getRuleContext(MnemonicContext.class,0);
		}
		public ParamsContext params() {
			return getRuleContext(ParamsContext.class,0);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public TerminalNode COLON() { return getToken(RISCVASMParser.COLON, 0); }
		public Asm_lineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_asm_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterAsm_line(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitAsm_line(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitAsm_line(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Asm_lineContext asm_line() throws RecognitionException {
		Asm_lineContext _localctx = new Asm_lineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_asm_line);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(34);
				label();
				setState(35);
				match(COLON);
				}
			}

			setState(39);
			mnemonic();
			setState(40);
			params();
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

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(RISCVASMParser.IDENTIFIER, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(IDENTIFIER);
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

	public static class MnemonicContext extends ParserRuleContext {
		public TerminalNode I_ADDI() { return getToken(RISCVASMParser.I_ADDI, 0); }
		public MnemonicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mnemonic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterMnemonic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitMnemonic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitMnemonic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MnemonicContext mnemonic() throws RecognitionException {
		MnemonicContext _localctx = new MnemonicContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_mnemonic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
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

	public static class ParamsContext extends ParserRuleContext {
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(RISCVASMParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(RISCVASMParser.COMMA, i);
		}
		public ParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamsContext params() throws RecognitionException {
		ParamsContext _localctx = new ParamsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_params);
		int _la;
		try {
			setState(71);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(47);
				param();
				setState(48);
				match(COMMA);
				setState(49);
				param();
				setState(50);
				match(COMMA);
				setState(51);
				param();
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(52);
					match(COMMA);
					}
				}


				    
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(57);
				param();
				setState(58);
				match(COMMA);
				setState(59);
				param();
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(60);
					match(COMMA);
					}
				}


				    
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(65);
				param();
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(66);
					match(COMMA);
					}
				}


				    
				}
				break;
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

	public static class ParamContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode OPENING_BRACKET() { return getToken(RISCVASMParser.OPENING_BRACKET, 0); }
		public TerminalNode CLOSING_BRACKET() { return getToken(RISCVASMParser.CLOSING_BRACKET, 0); }
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_param);
		try {
			setState(79);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(73);
				expr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(74);
				expr();
				setState(75);
				match(OPENING_BRACKET);
				setState(76);
				expr();
				setState(77);
				match(CLOSING_BRACKET);
				}
				break;
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

	public static class ExprContext extends ParserRuleContext {
		public RegisterContext register() {
			return getRuleContext(RegisterContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			register();
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

	public static class RegisterContext extends ParserRuleContext {
		public TerminalNode REG_ZERO_ABI() { return getToken(RISCVASMParser.REG_ZERO_ABI, 0); }
		public TerminalNode REG_RA_ABI() { return getToken(RISCVASMParser.REG_RA_ABI, 0); }
		public TerminalNode REG_SP_ABI() { return getToken(RISCVASMParser.REG_SP_ABI, 0); }
		public TerminalNode REG_GP_ABI() { return getToken(RISCVASMParser.REG_GP_ABI, 0); }
		public TerminalNode REG_TP_ABI() { return getToken(RISCVASMParser.REG_TP_ABI, 0); }
		public TerminalNode REG_T0_ABI() { return getToken(RISCVASMParser.REG_T0_ABI, 0); }
		public TerminalNode REG_T1_ABI() { return getToken(RISCVASMParser.REG_T1_ABI, 0); }
		public TerminalNode REG_T2_ABI() { return getToken(RISCVASMParser.REG_T2_ABI, 0); }
		public TerminalNode REG_T3_ABI() { return getToken(RISCVASMParser.REG_T3_ABI, 0); }
		public TerminalNode REG_T4_ABI() { return getToken(RISCVASMParser.REG_T4_ABI, 0); }
		public TerminalNode REG_T5_ABI() { return getToken(RISCVASMParser.REG_T5_ABI, 0); }
		public TerminalNode REG_T6_ABI() { return getToken(RISCVASMParser.REG_T6_ABI, 0); }
		public TerminalNode REG_FP_ABI() { return getToken(RISCVASMParser.REG_FP_ABI, 0); }
		public TerminalNode REG_A0_ABI() { return getToken(RISCVASMParser.REG_A0_ABI, 0); }
		public TerminalNode REG_A1_ABI() { return getToken(RISCVASMParser.REG_A1_ABI, 0); }
		public TerminalNode REG_A2_ABI() { return getToken(RISCVASMParser.REG_A2_ABI, 0); }
		public TerminalNode REG_A3_ABI() { return getToken(RISCVASMParser.REG_A3_ABI, 0); }
		public TerminalNode REG_A4_ABI() { return getToken(RISCVASMParser.REG_A4_ABI, 0); }
		public TerminalNode REG_A5_ABI() { return getToken(RISCVASMParser.REG_A5_ABI, 0); }
		public TerminalNode REG_A6_ABI() { return getToken(RISCVASMParser.REG_A6_ABI, 0); }
		public TerminalNode REG_A7_ABI() { return getToken(RISCVASMParser.REG_A7_ABI, 0); }
		public TerminalNode REG_S0_ABI() { return getToken(RISCVASMParser.REG_S0_ABI, 0); }
		public TerminalNode REG_S1_ABI() { return getToken(RISCVASMParser.REG_S1_ABI, 0); }
		public TerminalNode REG_S2_ABI() { return getToken(RISCVASMParser.REG_S2_ABI, 0); }
		public TerminalNode REG_S3_ABI() { return getToken(RISCVASMParser.REG_S3_ABI, 0); }
		public TerminalNode REG_S4_ABI() { return getToken(RISCVASMParser.REG_S4_ABI, 0); }
		public TerminalNode REG_S5_ABI() { return getToken(RISCVASMParser.REG_S5_ABI, 0); }
		public TerminalNode REG_S6_ABI() { return getToken(RISCVASMParser.REG_S6_ABI, 0); }
		public TerminalNode REG_S7_ABI() { return getToken(RISCVASMParser.REG_S7_ABI, 0); }
		public TerminalNode REG_S8_ABI() { return getToken(RISCVASMParser.REG_S8_ABI, 0); }
		public TerminalNode REG_S9_ABI() { return getToken(RISCVASMParser.REG_S9_ABI, 0); }
		public TerminalNode REG_S10_ABI() { return getToken(RISCVASMParser.REG_S10_ABI, 0); }
		public TerminalNode REG_S11_ABI() { return getToken(RISCVASMParser.REG_S11_ABI, 0); }
		public TerminalNode REG_ZERO() { return getToken(RISCVASMParser.REG_ZERO, 0); }
		public TerminalNode REG_RA() { return getToken(RISCVASMParser.REG_RA, 0); }
		public TerminalNode REG_SP() { return getToken(RISCVASMParser.REG_SP, 0); }
		public TerminalNode REG_GP() { return getToken(RISCVASMParser.REG_GP, 0); }
		public TerminalNode REG_TP() { return getToken(RISCVASMParser.REG_TP, 0); }
		public TerminalNode REG_T0() { return getToken(RISCVASMParser.REG_T0, 0); }
		public TerminalNode REG_T1() { return getToken(RISCVASMParser.REG_T1, 0); }
		public TerminalNode REG_T2() { return getToken(RISCVASMParser.REG_T2, 0); }
		public TerminalNode REG_S0() { return getToken(RISCVASMParser.REG_S0, 0); }
		public TerminalNode REG_S1() { return getToken(RISCVASMParser.REG_S1, 0); }
		public TerminalNode REG_A0() { return getToken(RISCVASMParser.REG_A0, 0); }
		public TerminalNode REG_A1() { return getToken(RISCVASMParser.REG_A1, 0); }
		public TerminalNode REG_A2() { return getToken(RISCVASMParser.REG_A2, 0); }
		public TerminalNode REG_A3() { return getToken(RISCVASMParser.REG_A3, 0); }
		public TerminalNode REG_A4() { return getToken(RISCVASMParser.REG_A4, 0); }
		public TerminalNode REG_A5() { return getToken(RISCVASMParser.REG_A5, 0); }
		public TerminalNode REG_A6() { return getToken(RISCVASMParser.REG_A6, 0); }
		public TerminalNode REG_A7() { return getToken(RISCVASMParser.REG_A7, 0); }
		public TerminalNode REG_S2() { return getToken(RISCVASMParser.REG_S2, 0); }
		public TerminalNode REG_S3() { return getToken(RISCVASMParser.REG_S3, 0); }
		public TerminalNode REG_S4() { return getToken(RISCVASMParser.REG_S4, 0); }
		public TerminalNode REG_S5() { return getToken(RISCVASMParser.REG_S5, 0); }
		public TerminalNode REG_S6() { return getToken(RISCVASMParser.REG_S6, 0); }
		public TerminalNode REG_S7() { return getToken(RISCVASMParser.REG_S7, 0); }
		public TerminalNode REG_S8() { return getToken(RISCVASMParser.REG_S8, 0); }
		public TerminalNode REG_S9() { return getToken(RISCVASMParser.REG_S9, 0); }
		public TerminalNode REG_S10() { return getToken(RISCVASMParser.REG_S10, 0); }
		public TerminalNode REG_S11() { return getToken(RISCVASMParser.REG_S11, 0); }
		public TerminalNode REG_T3() { return getToken(RISCVASMParser.REG_T3, 0); }
		public TerminalNode REG_T4() { return getToken(RISCVASMParser.REG_T4, 0); }
		public TerminalNode REG_T5() { return getToken(RISCVASMParser.REG_T5, 0); }
		public TerminalNode REG_T6() { return getToken(RISCVASMParser.REG_T6, 0); }
		public RegisterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_register; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).enterRegister(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RISCVASMParserListener ) ((RISCVASMParserListener)listener).exitRegister(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RISCVASMParserVisitor ) return ((RISCVASMParserVisitor<? extends T>)visitor).visitRegister(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RegisterContext register() throws RecognitionException {
		RegisterContext _localctx = new RegisterContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_register);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << REG_ZERO_ABI) | (1L << REG_RA_ABI) | (1L << REG_SP_ABI) | (1L << REG_GP_ABI) | (1L << REG_TP_ABI) | (1L << REG_T0_ABI) | (1L << REG_T1_ABI) | (1L << REG_T2_ABI))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (REG_T3_ABI - 64)) | (1L << (REG_T4_ABI - 64)) | (1L << (REG_T5_ABI - 64)) | (1L << (REG_T6_ABI - 64)) | (1L << (REG_FP_ABI - 64)) | (1L << (REG_A0_ABI - 64)) | (1L << (REG_A1_ABI - 64)) | (1L << (REG_A2_ABI - 64)) | (1L << (REG_A3_ABI - 64)) | (1L << (REG_A4_ABI - 64)) | (1L << (REG_A5_ABI - 64)) | (1L << (REG_A6_ABI - 64)) | (1L << (REG_A7_ABI - 64)) | (1L << (REG_S0_ABI - 64)) | (1L << (REG_S1_ABI - 64)) | (1L << (REG_S2_ABI - 64)) | (1L << (REG_S3_ABI - 64)) | (1L << (REG_S4_ABI - 64)) | (1L << (REG_S5_ABI - 64)) | (1L << (REG_S6_ABI - 64)) | (1L << (REG_S7_ABI - 64)) | (1L << (REG_S8_ABI - 64)) | (1L << (REG_S9_ABI - 64)) | (1L << (REG_S10_ABI - 64)) | (1L << (REG_S11_ABI - 64)) | (1L << (REG_ZERO - 64)) | (1L << (REG_RA - 64)) | (1L << (REG_SP - 64)) | (1L << (REG_GP - 64)) | (1L << (REG_TP - 64)) | (1L << (REG_T0 - 64)) | (1L << (REG_T1 - 64)) | (1L << (REG_T2 - 64)) | (1L << (REG_S0 - 64)) | (1L << (REG_S1 - 64)) | (1L << (REG_A0 - 64)) | (1L << (REG_A1 - 64)) | (1L << (REG_A2 - 64)) | (1L << (REG_A3 - 64)) | (1L << (REG_A4 - 64)) | (1L << (REG_A5 - 64)) | (1L << (REG_A6 - 64)) | (1L << (REG_A7 - 64)) | (1L << (REG_S2 - 64)) | (1L << (REG_S3 - 64)) | (1L << (REG_S4 - 64)) | (1L << (REG_S5 - 64)) | (1L << (REG_S6 - 64)) | (1L << (REG_S7 - 64)) | (1L << (REG_S8 - 64)) | (1L << (REG_S9 - 64)) | (1L << (REG_S10 - 64)) | (1L << (REG_S11 - 64)) | (1L << (REG_T3 - 64)) | (1L << (REG_T4 - 64)) | (1L << (REG_T5 - 64)) | (1L << (REG_T6 - 64)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\u0083X\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\2"+
		"\7\2\27\n\2\f\2\16\2\32\13\2\3\2\3\2\7\2\36\n\2\f\2\16\2!\13\2\5\2#\n"+
		"\2\3\3\3\3\3\3\5\3(\n\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\5\68\n\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6@\n\6\3\6\3\6\3\6\3\6\5"+
		"\6F\n\6\3\6\3\6\5\6J\n\6\3\7\3\7\3\7\3\7\3\7\3\7\5\7R\n\7\3\b\3\b\3\t"+
		"\3\t\3\t\2\2\n\2\4\6\b\n\f\16\20\2\3\3\2:z\2[\2\30\3\2\2\2\4\'\3\2\2\2"+
		"\6,\3\2\2\2\b.\3\2\2\2\nI\3\2\2\2\fQ\3\2\2\2\16S\3\2\2\2\20U\3\2\2\2\22"+
		"\23\5\4\3\2\23\24\7\u0083\2\2\24\27\3\2\2\2\25\27\7\u0083\2\2\26\22\3"+
		"\2\2\2\26\25\3\2\2\2\27\32\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\"\3"+
		"\2\2\2\32\30\3\2\2\2\33#\5\4\3\2\34\36\7\u0083\2\2\35\34\3\2\2\2\36!\3"+
		"\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 #\3\2\2\2!\37\3\2\2\2\"\33\3\2\2\2\""+
		"\37\3\2\2\2#\3\3\2\2\2$%\5\6\4\2%&\7}\2\2&(\3\2\2\2\'$\3\2\2\2\'(\3\2"+
		"\2\2()\3\2\2\2)*\5\b\5\2*+\5\n\6\2+\5\3\2\2\2,-\7\u0081\2\2-\7\3\2\2\2"+
		"./\7\26\2\2/\t\3\2\2\2\60J\3\2\2\2\61\62\5\f\7\2\62\63\7~\2\2\63\64\5"+
		"\f\7\2\64\65\7~\2\2\65\67\5\f\7\2\668\7~\2\2\67\66\3\2\2\2\678\3\2\2\2"+
		"89\3\2\2\29:\b\6\1\2:J\3\2\2\2;<\5\f\7\2<=\7~\2\2=?\5\f\7\2>@\7~\2\2?"+
		">\3\2\2\2?@\3\2\2\2@A\3\2\2\2AB\b\6\1\2BJ\3\2\2\2CE\5\f\7\2DF\7~\2\2E"+
		"D\3\2\2\2EF\3\2\2\2FG\3\2\2\2GH\b\6\1\2HJ\3\2\2\2I\60\3\2\2\2I\61\3\2"+
		"\2\2I;\3\2\2\2IC\3\2\2\2J\13\3\2\2\2KR\5\16\b\2LM\5\16\b\2MN\7\177\2\2"+
		"NO\5\16\b\2OP\7\u0080\2\2PR\3\2\2\2QK\3\2\2\2QL\3\2\2\2R\r\3\2\2\2ST\5"+
		"\20\t\2T\17\3\2\2\2UV\t\2\2\2V\21\3\2\2\2\f\26\30\37\"\'\67?EIQ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}