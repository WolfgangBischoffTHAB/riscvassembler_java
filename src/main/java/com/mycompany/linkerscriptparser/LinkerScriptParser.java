package com.mycompany.linkerscriptparser;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.mycompany.app.LINKERSCRIPTLANGUAGEExtractingOutputListener;
import com.mycompany.data.MemorySpecifier;
import com.mycompany.data.Section;

import linkerscriptlanguage.LINKERSCRIPTLANGUAGELexer;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.ProgramContext;

public class LinkerScriptParser {

    public void parseLinkerScript(Map<String, Section> sectionMap) throws IOException {

        Map<String, MemorySpecifier> memorySpecifierMap = new HashMap<>();

        System.out.println("Parsing linker file ...");

        String linkerFile = "src/test/resources/linker_script/standard.ld";
        final CharStream linkerCharStream = CharStreams.fromFileName(linkerFile);
        final LINKERSCRIPTLANGUAGELexer linkerLexer = new LINKERSCRIPTLANGUAGELexer(linkerCharStream);
        // create a buffer of tokens pulled from the lexer
        final CommonTokenStream linkerTokenStream = new CommonTokenStream(linkerLexer);

        final LINKERSCRIPTLANGUAGEParser linkerParser = new LINKERSCRIPTLANGUAGEParser(linkerTokenStream);
        linkerParser.addErrorListener(new ANTLRErrorListener() {

            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                    int charPositionInLine, String msg, RecognitionException e) {
                throw new UnsupportedOperationException("Unimplemented method 'syntaxError'");
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

        // parse
        ProgramContext linkerScriptRootNode = linkerParser.program();

        // System.out.println(linkerRoot);

        // LINKERSCRIPTLANGUAGERawOutputListener linkerScriptlistener = new
        // LINKERSCRIPTLANGUAGERawOutputListener();
        // linkerScriptlistener.linkerParser = linkerParser;

        LINKERSCRIPTLANGUAGEExtractingOutputListener linkerScriptlistener = new LINKERSCRIPTLANGUAGEExtractingOutputListener();
        linkerScriptlistener.sectionMap = sectionMap;
        linkerScriptlistener.memorySpecifierMap = memorySpecifierMap;

        // create a generic parse tree walker that can trigger callbacks
        final ParseTreeWalker linkerScriptWalker = new ParseTreeWalker();

        // walk the tree created during the parse, trigger callbacks
        linkerScriptWalker.walk(linkerScriptlistener, linkerScriptRootNode);

        //
        // Set address into sections
        //

        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {

            MemorySpecifier memorySpecifier = memorySpecifierMap.get(entry.getValue().memspec);
            entry.getValue().address = memorySpecifier.memorySpecOrigin;
        }

        System.out.println("Parsing linker file done.");
    }
    
}
