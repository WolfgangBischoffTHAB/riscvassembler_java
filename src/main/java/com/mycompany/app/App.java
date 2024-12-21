package com.mycompany.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
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

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.MemorySpecifier;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;
import com.mycompany.data.Section;
import com.mycompany.encoder.Encoder;
import com.mycompany.optimize.BaseOptimizer;
import com.mycompany.optimize.CallOptimizer;
import com.mycompany.optimize.LiOptimizer;
import com.mycompany.pseudo.combine.LiCombiner;
import com.mycompany.pseudo.resolve.BgtResolver;
import com.mycompany.pseudo.resolve.BnezResolver;
import com.mycompany.pseudo.resolve.CallResolver;
import com.mycompany.pseudo.resolve.JResolver;
import com.mycompany.pseudo.resolve.JrResolver;
import com.mycompany.pseudo.resolve.LaResolver;
import com.mycompany.pseudo.resolve.LiResolver;
import com.mycompany.pseudo.resolve.MvResolver;
import com.mycompany.pseudo.resolve.NopResolver;
import com.mycompany.pseudo.resolve.RetResolver;

import linkerscriptlanguage.LINKERSCRIPTLANGUAGELexer;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.ProgramContext;
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
        ProgramContext linkerRoot = linkerParser.program();

        // System.out.println(linkerRoot);

        // LINKERSCRIPTLANGUAGERawOutputListener linkerScriptlistener = new LINKERSCRIPTLANGUAGERawOutputListener();
        // linkerScriptlistener.linkerParser = linkerParser;

        Map<String, Section> sectionMap = new HashMap<>();
        Map<String, MemorySpecifier> memorySpecifierMap = new HashMap<>();

        LINKERSCRIPTLANGUAGEExtractingOutputListener linkerScriptlistener = new LINKERSCRIPTLANGUAGEExtractingOutputListener();
        linkerScriptlistener.sectionMap = sectionMap;
        linkerScriptlistener.memorySpecifierMap = memorySpecifierMap;

        // create a generic parse tree walker that can trigger callbacks
        final ParseTreeWalker linkerScriptWalker = new ParseTreeWalker();

        // walk the tree created during the parse, trigger callbacks
        linkerScriptWalker.walk(linkerScriptlistener, linkerRoot);

        System.out.println("Parsing linker file done.");



        //
        // Set address into sections
        //

        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {

            MemorySpecifier memorySpecifier = memorySpecifierMap.get(entry.getValue().memspec);
            entry.getValue().address = memorySpecifier.memorySpecOrigin;
        }



        //
        // Set default section
        //

        Section currentSection = sectionMap.get(".text");



        System.out.println("Lexing ...");

        String asmFile = "src/test/resources/riscvasm/test.s";

        // TODO: first step is always to let the preprocessor resolve .include
        // instructions
        // Let the compiler run on the combined file!

        final CharStream asmCharStream = CharStreams.fromFileName(asmFile);

        final RISCVASMLexer asmLexer = new RISCVASMLexer(asmCharStream);

        // create a buffer of tokens pulled from the lexer
        final CommonTokenStream asmTokens = new CommonTokenStream(asmLexer);

        System.out.println("Parsing ...");

        final RISCVASMParser asmParser = new RISCVASMParser(asmTokens);
        asmParser.addErrorListener(new ANTLRErrorListener() {

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
        Asm_fileContext asmRoot = asmParser.asm_file();

        List<AsmLine> asmLines = new ArrayList<>();

        // RawOutputListener listener = new RawOutputListener();

        RISCASMExtractingOutputListener asmListener = new RISCASMExtractingOutputListener();
        asmListener.asmLines = asmLines;
        asmListener.sectionMap = sectionMap;
        asmListener.currentSection = currentSection;

        // create a generic parse tree walker that can trigger callbacks
        final ParseTreeWalker asmWalker = new ParseTreeWalker();

        // walk the tree created during the parse, trigger callbacks
        asmWalker.walk(asmListener, asmRoot);

        // // DEBUG
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        //
        // Combine
        //

        // Do not use the LI combiner because the sample uart.s
        // sample is also not combined by https://riscvasm.lucasteske.dev/#
        // uart.s --> does not use combiner
        // square_and_print.s --> uses combiner
        //
        LiCombiner liCombiner = new LiCombiner();
        liCombiner.modify(asmLines, sectionMap);

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
        // System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        // }

        //
        // replace all .equ values in instructions
        //

        for (AsmLine asmLine : asmLines) {

            // System.out.println(asmLine);

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
        // Resolve - Replace pseudo instructions by individual instructions
        //

        LiResolver liResolver = new LiResolver();
        liResolver.modify(asmLines, sectionMap);

        LaResolver laResolver = new LaResolver();
        laResolver.modify(asmLines, sectionMap);

        CallResolver callResolver = new CallResolver();
        callResolver.modify(asmLines, sectionMap);

        NopResolver nopResolver = new NopResolver();
        nopResolver.modify(asmLines, sectionMap);

        MvResolver mvResolver = new MvResolver();
        mvResolver.modify(asmLines, sectionMap);

        BgtResolver bgtResolver = new BgtResolver();
        bgtResolver.modify(asmLines, sectionMap);

        BnezResolver bnezResolver = new BnezResolver();
        bnezResolver.modify(asmLines, sectionMap);

        JResolver jResolver = new JResolver();
        jResolver.modify(asmLines, sectionMap);

        JrResolver jrResolver = new JrResolver();
        jrResolver.modify(asmLines, sectionMap);

        RetResolver retResolver = new RetResolver();
        retResolver.modify(asmLines, sectionMap);

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
        liOptimizer.modify(asmLines, sectionMap);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {

        // if (asmLine.mnemonic == Mnemonic.I_LI) {
        // System.out.println("Bug");
        // }
        // System.out.println(asmLine);
        // }

        CallOptimizer callOptimizer = new CallOptimizer();
        callOptimizer.modify(asmLines, sectionMap);

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
        System.out.println("");
        System.out.println("");

        BaseOptimizer.updateAddresses(asmLines, sectionMap);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {
        //     System.out.println(asmLine);
        // }

        Map<String, Long> labelAddressMap = new HashMap<>();
        BaseOptimizer.buildLabelTable(asmLines, labelAddressMap, sectionMap);

        // DEBUG
        System.out.println("\n\n\n");
        for (AsmLine asmLine : asmLines) {
            System.out.println(asmLine);
        }

        //
        // resolve modifiers
        //

        resolveModifiers(asmLines, labelAddressMap);

        //
        // resolve all labels
        //

        // DEBUG
        System.out.println("\n\n\n");
        for (AsmLine asmLine : asmLines) {
            System.out.println(asmLine);
        }

        resolveLabels(asmLines, labelAddressMap);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {
        //     System.out.println(asmLine);
        // }

        // encode everything that has a mnemonic or is a
        // .dword, .word, .half, .byte, .string, .asciz, .ascii assembler instruction

        Encoder encoder = new Encoder();

        long currentAddress = 0;

        AsmLine errorAsmLine = null;
        try {
            for (AsmLine asmLine : asmLines) {

                // save the line for later error output
                errorAsmLine = asmLine;

                // DEBUG
                // System.out.println(asmLine);
                if (asmLine.label != null) {
                    System.out.println(asmLine.label);
                }

                currentAddress += encoder.encode(asmLine, labelAddressMap, currentAddress);
            }
        } catch (Exception e) {
            System.out.println("Failure while encoding: " + errorAsmLine);
            encoder.encode(errorAsmLine, labelAddressMap, currentAddress);
        }

        byte[] byteArray = encoder.byteArrayOutStream.toByteArray();

        // DEBUG
        //System.out.println(ByteArrayUtil.bytesToHex(byteArray));

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

        int data = 0;
        int container = 0;
        int container_index = 0;
        while ((data = (int)byteArrayInputStream.read()) != -1) {

            container <<= 8;
            container += data;

            //container >>= 8;
            //container += (data << 24);

            container_index++;

            if (container_index == 4) {

                //byte[] temp = ByteArrayUtil.intToFourByte(container, ByteOrder.LITTLE_ENDIAN);
                byte[] temp = ByteArrayUtil.intToFourByte(container, ByteOrder.BIG_ENDIAN);

                System.out.print(ByteArrayUtil.bytesToHexUpperCase(temp));
                System.out.println("");

                container_index = 0;
                container = 0;
            }

        }
        System.out.println("");

    }

    /**
     *
     * @param asmLines
     * @param labelAddressMap
     */
    private static void resolveLabels(List<AsmLine> asmLines, Map<String, Long> labelAddressMap) {

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic == Mnemonic.I_BNE) {
                System.out.println("stest");
            }

            if ((asmLine.pseudoInstructionAsmLine != null) && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA) && (asmLine.mnemonic == Mnemonic.I_AUIPC)) {
                continue;
            }
            if ((asmLine.pseudoInstructionAsmLine != null) && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA) && (asmLine.mnemonic == Mnemonic.I_ADDI)) {
                continue;
            }

            if (asmLine.offsetLabel_0 != null) {
                Long value = labelAddressMap.get(asmLine.offsetLabel_0);
                if (value != null) {
                    asmLine.numeric_0 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.offsetLabel_0 = null;
                }
            }
            if (asmLine.identifier_0 != null) {
                Long value = labelAddressMap.get(asmLine.identifier_0);
                if (value != null) {
                    asmLine.numeric_0 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.identifier_0 = null;
                }
            }

            if (asmLine.offsetLabel_1 != null) {
                Long value = labelAddressMap.get(asmLine.offsetLabel_1);
                if (value != null) {
                    asmLine.numeric_1 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.offsetLabel_1 = null;
                }
            }
            if (asmLine.identifier_1 != null) {
                Long value = labelAddressMap.get(asmLine.identifier_1);
                if (value != null) {
                    asmLine.numeric_1 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.identifier_1 = null;
                }
            }

            if (asmLine.offsetLabel_2 != null) {
                Long value = labelAddressMap.get(asmLine.offsetLabel_2);
                if (value != null) {
                    asmLine.numeric_2 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.offsetLabel_2 = null;
                }
            }
            if (asmLine.identifier_2 != null) {
                Long value = labelAddressMap.get(asmLine.identifier_2);
                if (value != null) {
                    asmLine.numeric_2 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.identifier_2 = null;
                }
            }

        }
    }

    /**
     * Resolve all modifiers
     *
     * @param asmLines
     * @param map
     */
    private static void resolveModifiers(List<AsmLine> asmLines, Map<String, Long> map) {

        for (AsmLine asmLine : asmLines) {

            if ((asmLine.pseudoInstructionAsmLine != null) && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA) && (asmLine.mnemonic == Mnemonic.I_AUIPC)) {
                continue;
            }
            if ((asmLine.pseudoInstructionAsmLine != null) && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA) && (asmLine.mnemonic == Mnemonic.I_ADDI)) {
                continue;
            }

            if (asmLine.modifier_0 != null) {

                long newValue = 0L;
                String label = asmLine.offsetLabel_0;

                Long value = map.get(label);
                // value = 0x10000L;

                switch (asmLine.modifier_0) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        //newValue = -4;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        //newValue = 16;
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
                //value = 0x10000L;

                switch (asmLine.modifier_1) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        //newValue = -4;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        //newValue = 16;
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
                // value = 0x10000L;

                switch (asmLine.modifier_2) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        //newValue = -4; // here (-4 = b 1111 1111 1100)
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        //newValue = 16;
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
    }

}
