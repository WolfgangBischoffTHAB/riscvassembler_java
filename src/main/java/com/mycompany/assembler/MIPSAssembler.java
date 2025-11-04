package com.mycompany.assembler;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.data.AsmLine;
import com.mycompany.data.MIPSRegister;
import com.mycompany.data.Section;
import com.mycompany.encoder.Encoder;
import com.mycompany.encoder.MIPSEncoder;
import com.mycompany.visitor.MIPSASMExtractingOutputListener;

import mipsasm.MIPSLexer;
import mipsasm.MIPSParser;

public class MIPSAssembler extends BaseAssembler<MIPSRegister> {

    private static final Logger logger = LoggerFactory.getLogger(MIPSAssembler.class);

    private CharStream asmCharStream;

    private MIPSLexer lexer;

    private MIPSParser parser;

    private MIPSEncoder encoder = new MIPSEncoder();

    public MIPSAssembler(Map<String, Section> sectionMap, Section dummySection) {

        currentSection = sectionMap.get(".text");

        // set up the visitor
        // the extractor assembles AsmLineS by visiting the antlr4 AST
        MIPSASMExtractingOutputListener asmListener = new MIPSASMExtractingOutputListener();
        asmListener.dummySection = dummySection;
        asmListener.asmLines = asmLines;
        asmListener.sectionMap = sectionMap;
        asmListener.currentSection = currentSection;

        this.asmListener = asmListener;
    }

    @Override
    public void assemble(Map<String, Section> sectionMap, String asmInputFile) throws IOException {
        
        // create a buffer of tokens pulled from the lexer
        final CommonTokenStream asmTokens = new CommonTokenStream(getLexer(asmInputFile));

        final Parser asmParser = getParser(asmTokens);
        asmParser.addErrorListener(new ANTLRErrorListener() {

            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                    int charPositionInLine, String msg, RecognitionException e) {
                throw new UnsupportedOperationException(
                        "SyntaxError (line:col) (" + line + ":" + charPositionInLine + ")");
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
                    BitSet ambigAlts, ATNConfigSet configs) {
                // throw new UnsupportedOperationException("Unimplemented method
                // 'reportAmbiguity'");
            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                    BitSet conflictingAlts, ATNConfigSet configs) {
                // System.out.println("startIndex: " + startIndex + " stopIndex: " + stopIndex);
                // throw new UnsupportedOperationException("Unimplemented method
                // 'reportAttemptingFullContext'");
            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                    int prediction, ATNConfigSet configs) {
                throw new UnsupportedOperationException("Unimplemented method 'reportContextSensitivity'");
            }

        });

        //
        // parse
        //

        System.out.println("Parsing ...");

        ParserRuleContext asmRoot = getRoot();

        System.out.println("Parsing done.");

        // create a generic parse tree walker that can trigger callbacks
        final ParseTreeWalker asmWalker = new ParseTreeWalker();

        // walk the tree created during the parse, trigger callbacks
        asmWalker.walk(asmListener, asmRoot);

        // List<AsmLine<?>> asmLines = getAsmLines();

        // DEBUG
        for (AsmLine<?> asmLine : asmLines) {
            System.out.println(asmLine);
        }
    }

    @Override
    public TokenSource getLexer(String asmInputFile) throws IOException {
        asmCharStream = CharStreams.fromFileName(asmInputFile);
        lexer = new MIPSLexer(asmCharStream);
        return lexer;
    }

    @Override
    public Parser getParser(CommonTokenStream asmTokens) {
        parser = new MIPSParser(asmTokens);
        return parser;
    }

    @Override
    public ParserRuleContext getRoot() {
        return parser.asm_file();
    }

    @Override
    public Encoder getEncoder() {
        return encoder;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

}
