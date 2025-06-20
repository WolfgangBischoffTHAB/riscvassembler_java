package com.mycompany.assembler;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Section;
import com.mycompany.encoder.Encoder;
import com.mycompany.optimize.BaseOptimizer;
import com.mycompany.optimize.CallOptimizer;
import com.mycompany.optimize.LiOptimizer;
import com.mycompany.preprocessing.IncludePreprocessor;
import com.mycompany.pseudo.combine.LiCombiner;
import com.mycompany.pseudo.resolve.BeqzResolver;
import com.mycompany.pseudo.resolve.BgezResolver;
import com.mycompany.pseudo.resolve.BgtResolver;
import com.mycompany.pseudo.resolve.BleResolver;
import com.mycompany.pseudo.resolve.BnezResolver;
import com.mycompany.pseudo.resolve.CallResolver;
import com.mycompany.pseudo.resolve.JResolver;
import com.mycompany.pseudo.resolve.JrResolver;
import com.mycompany.pseudo.resolve.LaResolver;
import com.mycompany.pseudo.resolve.LiResolver;
import com.mycompany.pseudo.resolve.MvResolver;
import com.mycompany.pseudo.resolve.NopResolver;
import com.mycompany.pseudo.resolve.RetResolver;

public abstract class BaseAssembler {

    /** Combine far calls (auipc+jalr) into near calls (jal) if possible */
    public static final boolean USE_CALL_OPTIMIZER = true;
    // public static final boolean USE_CALL_OPTIMIZER = false;

    public List<AsmLine<?>> asmLines = new ArrayList<>();

    public ParseTreeListener asmListener;

    public Section currentSection;

    public abstract TokenSource getLexer(String asmInputFile) throws IOException;

    public abstract Parser getParser(CommonTokenStream asmTokens);

    public abstract ParserRuleContext getRoot();

    public abstract Encoder getEncoder();

    public Map<String, Long> labelAddressMap;

    /** maps an address to the AsmLine the data at that address was encoded from */
    public Map<Long, AsmLine<?>> addressSourceAsmLineMap;

    public byte[] assemble(Map<String, Section> sectionMap, String asmInputFile) throws IOException {

        //
        // set default section
        //

        currentSection = sectionMap.get(".text");
        // setCurrentSection(currentSection);

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

        for (AsmLine<?> asmLine : asmLines) {
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

        for (AsmLine<?> asmLine : asmLines) {

            // DEBUG
            System.out.println(asmLine);

            if (asmLine.asmInstruction == AsmInstruction.EQU) {
                continue;
            }

            //
            // try to replace constants by their values
            // If an .equ konstant is used as a label, also replace the label
            //

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

            //
            // replace labels with definitions
            //

            if (asmLine.offsetLabel_0 != null) {
                if (equMap.containsKey(asmLine.offsetLabel_0)) {
                    asmLine.offset_0 = (Long) equMap.get(asmLine.offsetLabel_0);
                    asmLine.offsetLabel_0 = null;
                }
            }
            if (asmLine.offsetLabel_1 != null) {
                if (equMap.containsKey(asmLine.offsetLabel_1)) {
                    asmLine.offset_1 = (Long) equMap.get(asmLine.offsetLabel_1);
                    asmLine.offsetLabel_1 = null;
                }
            }
            if (asmLine.offsetLabel_2 != null) {
                if (equMap.containsKey(asmLine.offsetLabel_2)) {
                    asmLine.offset_2 = (Long) equMap.get(asmLine.offsetLabel_2);
                    asmLine.offsetLabel_2 = null;
                }
            }

            //
            // replace identifier and labels in expressions
            //
            // recursively traverse the expr tree of ASTNodes and replace
            // each occurence of a equ by the real value so that the expression
            // can be evaluated
            //

            if (asmLine.expr_0 != null) {
                asmLine.expr_0.replace(equMap);
            }
            if (asmLine.expr_1 != null) {
                asmLine.expr_1.replace(equMap);
            }
            if (asmLine.expr_2 != null) {
                asmLine.expr_2.replace(equMap);
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

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        //
        // Replace pseudo instruction by real instructions
        //

        NopResolver nopResolver = new NopResolver();
        nopResolver.modify(asmLines, sectionMap);

        MvResolver mvResolver = new MvResolver();
        mvResolver.modify(asmLines, sectionMap);

        BleResolver bleResolver = new BleResolver();
        bleResolver.modify(asmLines, sectionMap);

        BgtResolver bgtResolver = new BgtResolver();
        bgtResolver.modify(asmLines, sectionMap);

        BnezResolver bnezResolver = new BnezResolver();
        bnezResolver.modify(asmLines, sectionMap);

        BeqzResolver beqzResolver = new BeqzResolver();
        beqzResolver.modify(asmLines, sectionMap);

        BgezResolver bgezResolver = new BgezResolver();
        bgezResolver.modify(asmLines, sectionMap);

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

        boolean leftoverPseudoInstructionsFound = false;
        for (AsmLine<?> asmLine : asmLines) {
            if (asmLine.mnemonic != null && asmLine.mnemonic.isPseudo()) {
                // throw new RuntimeException("Pseudo detected: " + asmLine.mnemonic);
                System.out.println("ERROR! Leftover pseudo instruction detected: " + asmLine.mnemonic);
                System.out.println("ERROR! Leftover pseudo instruction detected: " + asmLine.mnemonic);
                System.out.println("ERROR! Leftover pseudo instruction detected: " + asmLine.mnemonic);
                System.out.println("ERROR! Leftover pseudo instruction detected: " + asmLine.mnemonic);
                System.out.println("ERROR! Leftover pseudo instruction detected: " + asmLine.mnemonic);

                leftoverPseudoInstructionsFound = true;
            }
        }

        if (!leftoverPseudoInstructionsFound) {
            System.out.println("[OK] No pseudo instructions left!");
        }

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
        // System.out.println(asmLine);
        // }

        if (USE_CALL_OPTIMIZER) {
            CallOptimizer callOptimizer = new CallOptimizer();
            callOptimizer.modify(asmLines, sectionMap);
        }

        // // DEBUG
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        //
        // Check for unoptimized instructions
        //

        for (AsmLine<?> asmLine : asmLines) {
            if (asmLine.pseudoInstructionAsmLine != null) {
                if (!asmLine.pseudoInstructionAsmLine.optimized) {
                    throw new RuntimeException("Unoptimized instruction detected! " + asmLine.mnemonic);
                }
            }
        }
        System.out.println("[OK] No unoptimized instructions found!");

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }
        // System.out.println("");
        // System.out.println("");

        BaseOptimizer.updateAddresses(asmLines, sectionMap);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        labelAddressMap = new HashMap<>();
        BaseOptimizer.buildLabelTable(asmLines, labelAddressMap, sectionMap);

        // DEBUG
        // BaseOptimizer.outputLabelAddressMap(labelAddressMap);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (final AsmLine asmLine<?> : asmLines) {
        // System.out.println(asmLine);
        // }


        //
        // evaluate expressions
        //

        for (final AsmLine<?> asmLine : asmLines) {

            if (asmLine.expr_0 != null) {
                asmLine.numeric_0 = asmLine.expr_0.evaluate();
            }
            if (asmLine.expr_1 != null) {
                asmLine.numeric_1 = asmLine.expr_1.evaluate();
            }
            if (asmLine.expr_2 != null) {
                asmLine.numeric_2 = asmLine.expr_2.evaluate();
            }
        }

        //
        // resolve modifiers
        //

        BaseOptimizer.resolveModifiers(asmLines, labelAddressMap);

        //
        // resolve all labels
        //

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        BaseOptimizer.resolveLabels(asmLines, labelAddressMap);

        // DEBUG output label address map
        System.out.println("\n\n\n");
        System.out.println("* LABEL-ADDRESS-MAP **************************");
        for (Map.Entry<String, Long> entry : labelAddressMap.entrySet()) {

            System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue() + " "
                    + ByteArrayUtil.longToHex(entry.getValue()));
        }
        System.out.println("***************************");

        // write label map to map_file.txt
        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("build//map_file.txt"))) {
            for (Map.Entry<String, Long> entry : labelAddressMap.entrySet()) {
                bufferedWriter.write("Key: " + entry.getKey() + " Value: " + entry.getValue() + " "
                        + ByteArrayUtil.longToHex(entry.getValue()) + "\n");
            }
            bufferedWriter.flush();
        }

        // DEBUG
        System.out.println("\n\n\n");
        System.out.println("***************************");
        for (AsmLine<?> asmLine : asmLines) {
            try {
                System.out.print(asmLine);
                System.out.println(" SourceLine: " + asmLine.sourceLine);
            } catch (Throwable e) {
                System.out.println("error!");
            }
        }
        System.out.println("***************************");

        //
        // encode everything that has a mnemonic or is a
        // .dword, .word, .half, .byte, .string, .asciz, .ascii assembler instruction
        //

        addressSourceAsmLineMap = new HashMap<>();

        Encoder encoder = getEncoder();

        long currentAddress = 0;

        AsmLine<?> errorAsmLine = null;
        try {

            for (final AsmLine<?> asmLine : asmLines) {

                // save the line for later error output
                errorAsmLine = asmLine;

                // // DEBUG
                // //System.out.println(asmLine);
                // if (asmLine.label != null) {
                // System.out.println("Label: " + asmLine.label);
                // }

                currentAddress = asmLine.section.address;
                asmLine.section.address += encoder.encode(asmLine, labelAddressMap, addressSourceAsmLineMap,
                        asmLine.section.address);
            }

        } catch (Exception e) {
            System.out.println("Failure while encoding: " + errorAsmLine);
            encoder.encode(errorAsmLine, labelAddressMap, addressSourceAsmLineMap, currentAddress);
        }

        //
        // Produce output for easy comparison with GNU riscv 32 bit elf toolchain or
        // online assemblers
        //

        byte[] byteArray = encoder.getByteArrayOutStream().toByteArray();

        //
        // DEBUG output the byte array to the console
        //

        // //ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        // outputHexMachineCode(byteArray, byteOrder);

        return byteArray;
    }

    public static void outputHexMachineCode(final byte[] byteArray, final ByteOrder byteOrder) {

        // DEBUG
        // System.out.println(ByteArrayUtil.bytesToHex(byteArray));

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

        int data;
        int container = 0;
        int container_index = 0;
        while ((data = (int) byteArrayInputStream.read()) != -1) {

            container <<= 8;
            container += data;

            container_index++;

            if (container_index == 4) {

                byte[] temp = ByteArrayUtil.intToFourByte(container, byteOrder);

                System.out.print(ByteArrayUtil.bytesToHexLowerCase(temp));
                System.out.println("");

                container_index = 0;
                container = 0;
            }

        }
        System.out.println("");
    }

    // @Override
    public List<AsmLine<?>> getAsmLines() {
        return asmLines;
    }

}
