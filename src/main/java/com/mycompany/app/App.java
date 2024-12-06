package com.mycompany.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

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

import com.mycompany.data.AsmLine;

import riscvasm.RISCVASMLexer;
import riscvasm.RISCVASMParser;
import riscvasm.RISCVASMParser.Asm_fileContext;

/**
 * Created with:
 *
 * <pre>
 * mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
 * </pre>
 *
 * Antlr extensions:
 *
 * open a .g4 file > CTRL + SHIFT + P > antlr.call-graph
 *
 * Clean java language server workspace:
 * Ctrl + Shift + P > Clean java language server workspace > Clean and Restart
 */
public class App {

    public static void main(String[] args) throws IOException {

        System.out.println("Lexing ...");

        String file = "src/test/resources/riscvasm/test.s";

        // TODO: first step is always to let the preprocessor resolve .include instructions
        // Let the compiler run on the combined file!

        final CharStream charStream = CharStreams.fromFileName(file);

        final RISCVASMLexer lexer = new RISCVASMLexer(charStream);

        // create a buffer of tokens pulled from the lexer
        final CommonTokenStream tokens = new CommonTokenStream(lexer);

        System.out.println("Parsing ...");

        final RISCVASMParser parser = new RISCVASMParser(tokens);
        parser.addErrorListener(new ANTLRErrorListener() {

            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                    int charPositionInLine, String msg, RecognitionException e) {
                throw new UnsupportedOperationException("Unimplemented method 'syntaxError'");
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
                    BitSet ambigAlts, ATNConfigSet configs) {
                //throw new UnsupportedOperationException("Unimplemented method 'reportAmbiguity'");
            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                    BitSet conflictingAlts, ATNConfigSet configs) {
                //System.out.println("startIndex: " + startIndex + " stopIndex: " + stopIndex);
                //throw new UnsupportedOperationException("Unimplemented method 'reportAttemptingFullContext'");
            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                    int prediction, ATNConfigSet configs) {
                throw new UnsupportedOperationException("Unimplemented method 'reportContextSensitivity'");
            }

        });

        // parse
        Asm_fileContext root = parser.asm_file();

        List<AsmLine> asmLines = new ArrayList<>();

        // RawOutputListener listener = new RawOutputListener();
        ExtractingOutputListener listener = new ExtractingOutputListener();
        listener.asmLines = asmLines;

        // create a generic parse tree walker that can trigger callbacks
        final ParseTreeWalker walker = new ParseTreeWalker();

        // walk the tree created during the parse, trigger callbacks
        walker.walk(listener, root);

        // DEBUG
        for (AsmLine asmLine : asmLines) {
            System.out.println(asmLine);
        }

    }

}
