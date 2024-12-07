package com.mycompany.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

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

import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;
import com.mycompany.optimize.BaseOptimizer;
import com.mycompany.optimize.CallOptimizer;
import com.mycompany.optimize.LiOptimizer;
import com.mycompany.pseudo.combine.LiCombiner;
import com.mycompany.pseudo.resolve.BgtResolver;
import com.mycompany.pseudo.resolve.BnezResolver;
import com.mycompany.pseudo.resolve.CallResolver;
import com.mycompany.pseudo.resolve.JResolver;
import com.mycompany.pseudo.resolve.JrResolver;
import com.mycompany.pseudo.resolve.LiResolver;
import com.mycompany.pseudo.resolve.MvResolver;
import com.mycompany.pseudo.resolve.NopResolver;

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

        // TODO: first step is always to let the preprocessor resolve .include
        // instructions
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
        Asm_fileContext root = parser.asm_file();

        List<AsmLine> asmLines = new ArrayList<>();

        // RawOutputListener listener = new RawOutputListener();
        ExtractingOutputListener listener = new ExtractingOutputListener();
        listener.asmLines = asmLines;

        // create a generic parse tree walker that can trigger callbacks
        final ParseTreeWalker walker = new ParseTreeWalker();

        // walk the tree created during the parse, trigger callbacks
        walker.walk(listener, root);

        // // DEBUG
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        //
        // Combine
        //

        LiCombiner liCombiner = new LiCombiner();
        liCombiner.modify(asmLines);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {
        // if (asmLine.mnemonic == Mnemonic.I_LI) {
        // System.out.println("Bug");
        // }
        // System.out.println(asmLine);
        // }

        // LaCombiner laCombiner = new LaCombiner();
        // laCombiner.modify(asmLines);

        //
        // Build table of .equ
        //

        Map<String, Object> equMap = new HashMap<>();

        for (AsmLine asmLine : asmLines) {
            if (asmLine.asmInstruction == AsmInstruction.EQU) {
                equMap.put(asmLine.identifier_0, asmLine.numeric_1);
            }
        }

        // // DEBUG
        // for (Map.Entry<String, Object> mapEntry : equMap.entrySet()) {
        //     System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        // }

        for (AsmLine asmLine : asmLines) {

            //System.out.println(asmLine);

            if (asmLine.asmInstruction == AsmInstruction.EQU) {
                continue;
            }

            if (asmLine.identifier_0 != null) {
                Object value = equMap.get(asmLine.identifier_0);
                if (value != null) {
                    asmLine.identifier_0 = null;
                    asmLine.numeric_0 = (Long) value;
                }
            }
            if (asmLine.identifier_1 != null) {
                Object value = equMap.get(asmLine.identifier_1);
                if (value != null) {
                    asmLine.identifier_1 = null;
                    asmLine.numeric_1 = (Long) value;
                }
            }
            if (asmLine.identifier_2 != null) {
                Object value = equMap.get(asmLine.identifier_2);
                if (value != null) {
                    asmLine.identifier_2 = null;
                    asmLine.numeric_2 = (Long) value;
                }
            }

            if (asmLine.offsetLabel_0 != null) {
                if (equMap.containsKey(asmLine.offsetLabel_0)) {
                    asmLine.offset_0 = (Long) equMap.get(asmLine.offsetLabel_0);
                }
            }
            if (asmLine.offsetLabel_1 != null) {
                if (equMap.containsKey(asmLine.offsetLabel_1)) {
                    asmLine.offset_1 = (Long) equMap.get(asmLine.offsetLabel_1);
                }
            }
            if (asmLine.offsetLabel_2 != null) {
                if (equMap.containsKey(asmLine.offsetLabel_2)) {
                    asmLine.offset_2 = (Long) equMap.get(asmLine.offsetLabel_2);
                }
            }
        }

        //
        // Resolve - Replace pseudo instructions
        //

        LiResolver liResolver = new LiResolver();
        liResolver.modify(asmLines);

        CallResolver callResolver = new CallResolver();
        callResolver.modify(asmLines);

        NopResolver nopResolver = new NopResolver();
        nopResolver.modify(asmLines);

        MvResolver mvResolver = new MvResolver();
        mvResolver.modify(asmLines);

        BgtResolver bgtResolver = new BgtResolver();
        bgtResolver.modify(asmLines);

        BnezResolver bnezResolver = new BnezResolver();
        bnezResolver.modify(asmLines);

        JResolver jResolver = new JResolver();
        jResolver.modify(asmLines);

        JrResolver jrResolver = new JrResolver();
        jrResolver.modify(asmLines);

        // // DEBUG
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        //
        // Check for leftover pseudo instructions
        //

        for (AsmLine asmLine : asmLines) {
            if (asmLine.mnemonic != null && asmLine.mnemonic.isPseudo()) {
                // throw new RuntimeException("Pseudo detected: " + asmLine.mnemonic);
                System.out.println("Pseudo detected: " + asmLine.mnemonic);
                System.out.println("Pseudo detected: " + asmLine.mnemonic);
                System.out.println("Pseudo detected: " + asmLine.mnemonic);
                System.out.println("Pseudo detected: " + asmLine.mnemonic);
                System.out.println("Pseudo detected: " + asmLine.mnemonic);
            }
        }
        System.out.println("No pseudo instructions left!");

        //
        // Optimization - resolve all pseudo instructions to the minimal amount
        // of instructions necessary
        //
        // - first assume maximum amount of instructions for each pseudo instruction
        // - build a label table
        // - check if modifiers %hi and %lo resolve to 0. If so check if the
        // instructions
        // can be removed/optimized away
        // - it is only possible to use a label if there is no unoptimized pseudo
        // instruction between the current instruction and the label! Only
        // offsets over true instructions make sense!
        // - if the deletion of an instruction is exactly on the 12-bit boundary
        // throw an exception for now
        //

        LiOptimizer liOptimizer = new LiOptimizer();
        liOptimizer.modify(asmLines);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {

        // if (asmLine.mnemonic == Mnemonic.I_LI) {
        // System.out.println("Bug");
        // }
        // System.out.println(asmLine);
        // }

        CallOptimizer callOptimizer = new CallOptimizer();
        callOptimizer.modify(asmLines);

        // // DEBUG
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        // check

        for (AsmLine asmLine : asmLines) {
            if (asmLine.pseudoInstructionAsmLine != null) {
                if (!asmLine.pseudoInstructionAsmLine.optimized) {
                    throw new RuntimeException("Unoptimized instruction detected! " + asmLine.mnemonic);
                }
            }
        }
        System.out.println("No unoptimized instructions found!");

        // DEBUG
        System.out.println("\n\n\n");
        for (AsmLine asmLine : asmLines) {
            System.out.println(asmLine);
        }

        BaseOptimizer.updateAddresses(asmLines);

        Map<String, Long> map = new HashMap<>();
        BaseOptimizer.buildLabelTable(asmLines, map);




        // TODO: resolve all modifiers

        for (AsmLine asmLine : asmLines) {

            if (asmLine.modifier_0 != null) {

                long newValue = 0L;
                String label = asmLine.offsetLabel_0;

                Long value = map.get(label);

                switch (asmLine.modifier_0) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_0 = null;
                asmLine.modifier_0 = null;

                if ((asmLine.register_0 == null) || (asmLine.register_0 == Register.REG_UNKNOWN)) {
                    asmLine.numeric_0 = newValue;
                } else {
                    asmLine.offset_0 = newValue;
                }
            }

            if (asmLine.modifier_1 != null) {

                long newValue = 0L;
                String label = asmLine.offsetLabel_1;

                Long value = map.get(label);

                switch (asmLine.modifier_1) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_1 = null;
                asmLine.modifier_1 = null;

                if ((asmLine.register_1 == null) || (asmLine.register_1 == Register.REG_UNKNOWN)) {
                    asmLine.numeric_1 = newValue;
                } else {
                    asmLine.offset_1 = newValue;
                }
            }

            if (asmLine.modifier_2 != null) {

                long newValue = 0L;
                String label = asmLine.offsetLabel_2;

                Long value = map.get(label);

                switch (asmLine.modifier_2) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_2 = null;
                asmLine.modifier_2 = null;

                if ((asmLine.register_2 == null) || (asmLine.register_2 == Register.REG_UNKNOWN)) {
                    asmLine.numeric_2 = newValue;
                } else {
                    asmLine.offset_2 = newValue;
                }
            }
        }

        // TODO: resolve all labels

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic == Mnemonic.I_BNE) {
                System.out.println(asmLine);
            }

            if (asmLine.offsetLabel_0 != null) {
                Long value = map.get(asmLine.offsetLabel_0);
                if (value != null) {
                    asmLine.numeric_0 = value - asmLine.address;
                    asmLine.offsetLabel_0 = null;
                }
            }
            if (asmLine.identifier_0 != null) {
                Long value = map.get(asmLine.identifier_0);
                if (value != null) {
                    asmLine.numeric_0 = value - asmLine.address;
                    asmLine.identifier_0 = null;
                }
            }

            if (asmLine.offsetLabel_1 != null) {
                Long value = map.get(asmLine.offsetLabel_1);
                if (value != null) {
                    asmLine.numeric_1 = value - asmLine.address;
                    asmLine.offsetLabel_1 = null;
                }
            }
            if (asmLine.identifier_1 != null) {
                Long value = map.get(asmLine.identifier_1);
                if (value != null) {
                    asmLine.numeric_1 = value - asmLine.address;
                    asmLine.identifier_1 = null;
                }
            }

            if (asmLine.offsetLabel_2 != null) {
                Long value = map.get(asmLine.offsetLabel_2);
                if (value != null) {
                    asmLine.numeric_2 = value - asmLine.address;
                    asmLine.offsetLabel_2 = null;
                }
            }
            if (asmLine.identifier_2 != null) {
                Long value = map.get(asmLine.identifier_2);
                if (value != null) {
                    asmLine.numeric_2 = value - asmLine.address;
                    asmLine.identifier_2 = null;
                }
            }

        }

        // DEBUG
        System.out.println("\n\n\n");
        for (AsmLine asmLine : asmLines) {
            System.out.println(asmLine);
        }

        // TODO encode everything that has a mnemonic

        Encoder encoder = new Encoder();
        for (AsmLine asmLine : asmLines) {
            System.out.println(asmLine);

            encoder.encode(asmLine);
        }
    }

}
