package com.mycompany.assembler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.parse.v3TreeGrammarException;
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

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;
import com.mycompany.encoder.AsmInstructionEncoder;
import com.mycompany.encoder.Encoder;
import com.mycompany.encoder.MnemonicEncoder;
import com.mycompany.encoder.RISCVEncoder;
import com.mycompany.encoder.RISCVMnemonicEncoder;
import com.mycompany.optimize.BaseOptimizer;
import com.mycompany.optimize.CallOptimizer;
import com.mycompany.optimize.LiOptimizer;
import com.mycompany.pseudo.combine.LiCombiner;
import com.mycompany.pseudo.resolve.BeqzResolver;
import com.mycompany.pseudo.resolve.BgezResolver;
import com.mycompany.pseudo.resolve.BgtResolver;
import com.mycompany.pseudo.resolve.BgtuResolver;
import com.mycompany.pseudo.resolve.BleResolver;
import com.mycompany.pseudo.resolve.BleuResolver;
import com.mycompany.pseudo.resolve.BnezResolver;
import com.mycompany.pseudo.resolve.CallResolver;
import com.mycompany.pseudo.resolve.CsrrResolver;
import com.mycompany.pseudo.resolve.JResolver;
import com.mycompany.pseudo.resolve.JrResolver;
import com.mycompany.pseudo.resolve.LaResolver;
import com.mycompany.pseudo.resolve.LiResolver;
import com.mycompany.pseudo.resolve.MvResolver;
import com.mycompany.pseudo.resolve.NopResolver;
import com.mycompany.pseudo.resolve.RetResolver;
import com.mycompany.pseudo.resolve.SeqzResolver;
import com.mycompany.visitor.RISCASMExtractingOutputListener;

import riscvasm.RISCVASMLexer;
import riscvasm.RISCVASMParser;

public class RiscVAssembler extends BaseAssembler<RISCVRegister> {

    private static final Logger logger = LoggerFactory.getLogger(RiscVAssembler.class);

    // private static final boolean OUTPUT_MACHINE_CODE = false;
    private static final boolean OUTPUT_MACHINE_CODE = true;

    private static final boolean RESOLVE_NOP_TO_NONSENSE_ADDI = true;

    private CharStream asmCharStream;

    private RISCVASMLexer lexer;

    private RISCVASMParser parser;

    public RISCVEncoder encoder = new RISCVEncoder();

    /**
     * ctor
     *
     * @param sectionMap
     * @param dummySection
     */
    public RiscVAssembler(Map<String, Section> sectionMap, Section dummySection) {

        encoder.sectionMap = sectionMap;

        // start in the .text section
        encoder.currentSection = sectionMap.get(".text");

        // set up the visitor
        // the extractor assembles AsmLineS by visiting the antlr4 AST
        RISCASMExtractingOutputListener asmListener = new RISCASMExtractingOutputListener();
        asmListener.dummySection = dummySection;
        asmListener.asmLines = asmLines;
        asmListener.sectionMap = sectionMap;
        asmListener.currentSection = encoder.currentSection;

        this.asmListener = asmListener;

        RISCVMnemonicEncoder riscvMnemonicEncoder = new RISCVMnemonicEncoder();
        riscvMnemonicEncoder.riscvEncoder = encoder;
        encoder.mnemonicEncoder = riscvMnemonicEncoder;

        AsmInstructionEncoder asmInstructionEncoder = new AsmInstructionEncoder();
        asmInstructionEncoder.riscvEncoder = encoder;
        encoder.asmInstructionEncoder = asmInstructionEncoder;
    }

    public void assemble(Map<String, Section> sectionMap, String asmInputFile) throws IOException {

        // set default section
        encoder.currentSection = sectionMap.get(".text");

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

        getLogger().info("Parsing ...");

        ParserRuleContext asmRoot = getRoot();

        getLogger().info("Parsing done.");

        // create a generic parse tree walker that can trigger callbacks
        final ParseTreeWalker asmWalker = new ParseTreeWalker();

        // walk the tree created during the parse, trigger callbacks
        asmWalker.walk(asmListener, asmRoot);

        getLogger().info("ASMLine count: " + asmLines.size());

        // // DEBUG - output the entire program as parsed from the input file
        // for (AsmLine<?> asmLine : asmLines) {
        // getLogger().info(asmLine);
        // }

        //
        // Combine
        //

        // Do not use the LI combiner because the sample uart.s
        // sample is also not combined by https://riscvasm.lucasteske.dev/#
        // uart.s --> does not use combiner
        // square_and_print.s --> uses combiner
        //
        LiCombiner<RISCVRegister> liCombiner = new LiCombiner<>();
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
            // System.out.println(asmLine);

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

        // // DEBUG - output intermediate assembly after removing pseudo instructions
        // // but before removing modifiers and before calling the optimizers
        // System.out.println("\n\n\n");
        // System.out.println(
        //         "DEBUG - output intermediate assembly after removing pseudo instructions but before removing modifiers and before calling the optimizers");

        // DEBUG
        // for (AsmLine<?> asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        // DEBUG write input assembly to file
        String filename = "build//assembly_from_compiler.s";
        printAssemblyToFile(filename, true);

        CallOptimizer<RISCVRegister> callOptimizer = new CallOptimizer<>();

        //
        // resolve modifiers
        //

        // BaseOptimizer.resolveModifiers(asmLines, labelAddressMap);

        //
        // Resolve - Replace pseudo instructions by individual, real instructions
        //

        LiResolver liResolver = new LiResolver();
        liResolver.modify(asmLines, sectionMap);

        LaResolver<RISCVRegister> laResolver = new LaResolver<>();
        laResolver.modify(asmLines, sectionMap);

        CallResolver callResolver = new CallResolver();
        callResolver.modify(asmLines, sectionMap);

        if (RESOLVE_NOP_TO_NONSENSE_ADDI) {
            NopResolver nopResolver = new NopResolver();
            nopResolver.modify(asmLines, sectionMap);
        }

        MvResolver mvResolver = new MvResolver();
        mvResolver.modify(asmLines, sectionMap);

        BleResolver bleResolver = new BleResolver();
        bleResolver.modify(asmLines, sectionMap);

        BleuResolver bleuResolver = new BleuResolver();
        bleuResolver.modify(asmLines, sectionMap);

        BgtResolver bgtResolver = new BgtResolver();
        bgtResolver.modify(asmLines, sectionMap);

        BgtuResolver bgtuResolver = new BgtuResolver();
        bgtuResolver.modify(asmLines, sectionMap);

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

        CsrrResolver csrrResolver = new CsrrResolver();
        csrrResolver.modify(asmLines, sectionMap);

        SeqzResolver seqzResolver = new SeqzResolver();
        seqzResolver.modify(asmLines, sectionMap);




        // reset the offsets in the sectionMap
        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
            entry.getValue().setCurrentOffset(0);
        }




        labelAddressMap = new HashMap<>();
        // callOptimizer.buildLabelTable(asmLines, labelAddressMap, null, sectionMap);




        // // DEBUG - output intermediate assembly after removing pseudo instructions
        // but before removing modifiers and before calling the optimizers
        // System.out.println("\n\n\n");
        // System.out.println("DEBUG - output intermediate assembly after removing
        // pseudo instructions but before removing modifiers and before calling the
        // optimizers");
        // for (AsmLine<?> asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        filename = "build//assembly_pseudo_resolved.s";
        printAssemblyToFile(filename, true);

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
        // instructions can be removed/optimized away
        // - it is only possible to use a label if there is no unoptimized pseudo
        // instruction between the current instruction and the label! Only
        // offsets over true instructions make sense!
        // - if the deletion of an instruction is exactly on the 12-bit boundary
        // throw an exception for now
        //

        @SuppressWarnings("rawtypes")
        LiOptimizer liOptimizer = new LiOptimizer();
        liOptimizer.modify(asmLines, sectionMap);

        // // DEBUG - output after the LI optimizer
        // System.out.println("\n\n\n");
        // System.out.println("DEBUG - output after the LI optimizer");
        // for (AsmLine<?> asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        if (USE_CALL_OPTIMIZER) {
            callOptimizer.modify(asmLines, sectionMap);
        }

        // // DEBUG - output after the LI and CALL optimizer
        // System.out.println("\n\n\n");
        // System.out.println("DEBUG - output after the LI and CALL optimizer");
        // for (AsmLine<?> asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        //
        // Check for unoptimized instructions
        //

        for (AsmLine<?> asmLine : asmLines) {
            if (asmLine.pseudoInstructionAsmLine != null) {
                if (!asmLine.pseudoInstructionAsmLine.optimized) {
                    // DEBUG print the asmLine where the condition occured
                    System.out.println(asmLine.toString());
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

        // CallOptimizer<RISCVRegister> callOptimizer = new CallOptimizer<>();
        callOptimizer.updateAddresses(asmLines, sectionMap);

        // // DEBUG - output code after addresses have been updated
        // System.out.println("\n\n\n");
        // System.out.println("DEBUG - output code after addresses have been updated");
        // for (AsmLine<?> asmLine : asmLines) {
        //     System.out.println(asmLine);
        // }

        filename = "build//assembly_updated_addresses.s";
        printAssemblyToFile(filename, false);

        // labelAddressMap = new HashMap<>();
        callOptimizer.buildLabelTable(asmLines, labelAddressMap, null, sectionMap);

        // DEBUG
        BaseOptimizer.outputLabelAddressMap(labelAddressMap);

        // // DEBUG
        // System.out.println("\n\n\n");
        // for (final AsmLine asmLine<?> : asmLines) {
        // System.out.println(asmLine);
        // }

        //
        // evaluate expressions
        //

        for (final AsmLine<?> asmLine : asmLines) {

            try {

                if (asmLine.expr_0 != null) {
                    asmLine.numeric_0 = asmLine.expr_0.evaluate();
                }
                if (asmLine.expr_1 != null) {
                    asmLine.numeric_1 = asmLine.expr_1.evaluate();
                }
                if (asmLine.expr_2 != null) {
                    asmLine.numeric_2 = asmLine.expr_2.evaluate();
                }

            } catch (Exception e) {

                getLogger().info("Cannot process: " + asmLine + "");
                getLogger().info(e.getMessage(), e);

                return;

            }
        }

        //
        // resolve modifiers
        //

        BaseOptimizer.resolveModifiers(asmLines, labelAddressMap);

        // // DEBUG - after resolving modifiers
        // System.out.println("\n\n\n");
        // System.out.println("DEBUG - after resolving modifiers");
        // for (AsmLine<?> asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        filename = "build//assembly_modifiers_resolved.s";
        printAssemblyToFile(filename, false);

        //
        // resolve all labels
        //

        callOptimizer.resolveLabels(asmLines, labelAddressMap);

        // // DEBUG - after resolving labels
        // System.out.println("\n\n\n");
        // System.out.println("DEBUG - after resolving labels");
        // for (AsmLine asmLine : asmLines) {
        // System.out.println(asmLine);
        // }

        // DEBUG output label address map
        getLogger().trace("\n\n\n");
        getLogger().trace("* LABEL-ADDRESS-MAP **************************");
        for (Map.Entry<String, Long> entry : labelAddressMap.entrySet()) {
            getLogger().trace("Key: " + entry.getKey() + " Value: " + entry.getValue() + " "
                    + ByteArrayUtil.longToHex(entry.getValue()));
        }
        getLogger().trace("***************************");

        // write label map to map_file.txt
        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("build//map_file.txt"))) {
            for (Map.Entry<String, Long> entry : labelAddressMap.entrySet()) {
                bufferedWriter.write("Key: " + entry.getKey() + " Value: " + entry.getValue() + " "
                        + ByteArrayUtil.longToHex(entry.getValue()) + "\n");
            }
            bufferedWriter.flush();
        }

        // outputAssemblyToFile();

        //
        // Encode
        //
        // encode everything that has a mnemonic or is a
        // .dword, .word, .half, .byte, .string, .asciz, .ascii assembler instruction
        //

        addressSourceAsmLineMap = new HashMap<>();

        Encoder encoder = getEncoder();

        int lineNumber = 0;

        // reset the offsets in the sectionMap
        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
            entry.getValue().setCurrentOffset(0);
        }

        AsmLine<?> errorAsmLine = null;
        try {

            for (final AsmLine<?> asmLine : asmLines) {

                // save the line for later error output
                errorAsmLine = asmLine;

                // DEBUG
                // System.out.println(asmLine);

                long spaceUsed = encoder.encode(asmLine, labelAddressMap, addressSourceAsmLineMap);

                // advance the current index for the section after using space within that
                // section
                asmLine.section.setCurrentOffset(asmLine.section.getCurrentOffset() + spaceUsed);

                lineNumber++;
            }

            // flush buffered information
            AsmLine tempAsmLine = asmLines.get(0);
            encoder.finalize(tempAsmLine.section.byteArrayOutStream);

            // append zeroes so that the machine code is a multiple of word size (word = 4
            // Byte)
            for (int i = 0; i < tempAsmLine.section.byteArrayOutStream.size() % 4; i++) {
                tempAsmLine.section.byteArrayOutStream.write(0x00);
            }

        } catch (Exception e) {

            getLogger().info(e.getMessage(), e);
            getLogger().info("Failure while encoding line #" + lineNumber + ": " + errorAsmLine);

            throw new RuntimeException("Error during encoding!");

            // encoder.encode(errorAsmLine, labelAddressMap, addressSourceAsmLineMap,
            // currentAddress);
        }

        getLogger().info("ASMLine count: " + asmLines.size());

        outputAssemblyToFile();

        if (OUTPUT_MACHINE_CODE) {

            // DEBUG print to console
            // for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
            //     Section section = entry.getValue();
            //     getLogger().info("-- Section: " + section.name + " ----------------------");
            //     // DEBUG output the byte array to the console
            //     ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
            //     byte[] byteArray = section.byteArrayOutStream.toByteArray();
            //     outputHexMachineCode(byteArray, byteOrder);
            //     getLogger().info("");
            // }

            //
            // DEBUG: output machine code for easy comparison with GNU riscv 32 bit elf
            // toolchain or online assemblers
            //

            outputMachineCodeToTextFile(sectionMap);
            outputMachineCodeToBinaryFile(sectionMap);
        }

        // byte[] byteArray = encoder.getByteArrayOutStream().toByteArray();

        // DEBUG output the byte array to the console
        // //ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        // outputHexMachineCode(byteArray, byteOrder);

        // return byteArray;
        // throw new RuntimeException();
    }

    private void outputMachineCodeToTextFile(Map<String, Section> sectionMap) throws IOException {

        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {

            Section section = entry.getValue();
            outputMachineCodeToFile(section);
        }
    }

    private void outputMachineCodeToBinaryFile(Map<String, Section> sectionMap) throws IOException {

        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {

            Section section = entry.getValue();

            byte[] byteArray = section.byteArrayOutStream.toByteArray();

            String filename = "build//" + section.name + ".bin";

            try (FileOutputStream stream = new FileOutputStream(filename)) {
                stream.write(byteArray);
            }
        }
    }

    private void printAssemblyToFile(String filename, boolean assignAddresses) throws IOException {

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(filename))) {

            int offset = 0;

            for (AsmLine<?> asmLine : asmLines) {

                // DEBUG
                // if (asmLine.mnemonic == Mnemonic.I_ADDI) {
                //     System.out.println(asmLine.toString());
                // }

                if (asmLine.mnemonic != null) {
                    // only change the address if the user requested it
                    if (assignAddresses) {
                        asmLine.setOffset(offset);
                        offset += 4;
                    }
                }

                String asmLineAsString = asmLine.toString();
                bufferedWriter.write(asmLineAsString);

                // // output machine code
                // bufferedWriter.write(" [" + ByteArrayUtil.byteToHex(asmLine.machineCode,
                // null, "%1$02X") + "]");

                if (asmLine.mnemonic != null) {
                    // output offset
                    int delta = 50 - asmLineAsString.length();
                    if (delta < 0) {
                        delta = 0;
                    }
                    String filler = spaces(delta);
                    bufferedWriter.write(
                            filler + "# " + ByteArrayUtil.byteToHex(asmLine.getOffset()) + " (" + asmLine.getOffset()
                                    + ")");
                }

                bufferedWriter.write("\n");
            }

            bufferedWriter.flush();
        }
    }

    /**
     * Iterate over all sections. Write each section to a file having
     * the name of the section
     *
     * @param section
     * @throws IOException
     */
    private void outputMachineCodeToFile(Section section) throws IOException {

        String sectionName = section.name;

        while ((section.byteArrayOutStream.size() % 4) != 0) {
            section.byteArrayOutStream.write(0x00);
        }

        byte[] sectionByteArray = section.byteArrayOutStream.toByteArray();

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter("build//" + sectionName + ".s"))) {

            byte tempBuffer[] = new byte[4];

            for (int i = 0; i < sectionByteArray.length; i += 4) {

                System.arraycopy(sectionByteArray, i, tempBuffer, 0, 4);

                // System.out.println(ByteArrayUtil.bytesToHexLittleEndian(tempBuffer));

                String sectionByteArrayAsString = ByteArrayUtil.bytesToHexLittleEndian(tempBuffer);
                bufferedWriter.write(sectionByteArrayAsString);
                bufferedWriter.write("\n");
            }

            bufferedWriter.flush();
        }
    }

    /**
     * To a file, output raw assembly (modifiers resolved, pseudo instructions
     * resolved to real instructions)
     *
     * @throws IOException
     */
    private void outputAssemblyToFile() throws IOException {

        String filename = "build//resolved_assembly.s";

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(filename))) {

            // DEBUG
            if (getLogger().isTraceEnabled()) {
                getLogger().trace("\n\n\n");
                getLogger().trace("***************************");
            }

            for (AsmLine<?> asmLine : asmLines) {

                // DEBUG
                if (getLogger().isTraceEnabled()) {
                    try {
                        // DEBUG
                        // System.out.print(" SourceLine: " + asmLine.sourceLine);
                        getLogger().trace(asmLine.toString());
                        getLogger().trace("");
                    } catch (Throwable e) {
                        getLogger().error(e.getMessage(), e);
                        getLogger().info("error!");
                    }
                }

                String asmLineAsString = asmLine.toString();
                bufferedWriter.write(asmLineAsString);

                // output machine code
                bufferedWriter.write(" [" + ByteArrayUtil.byteToHex(asmLine.machineCode, null, "%1$02X") + "]");

                // output offset
                int delta = 50 - asmLineAsString.length();
                if (delta < 0) {
                    delta = 0;
                }
                String filler = spaces(delta);
                bufferedWriter.write(
                        filler + "# " + ByteArrayUtil.byteToHex(asmLine.getOffset()) + " (" + asmLine.getOffset()
                                + ")");

                bufferedWriter.write("\n");
            }
            if (getLogger().isTraceEnabled()) {
                getLogger().trace("***************************");
            }

            bufferedWriter.flush();
        }
    }

    @Override
    public TokenSource getLexer(String asmInputFile) throws IOException {

        asmCharStream = CharStreams.fromFileName(asmInputFile);
        lexer = new RISCVASMLexer(asmCharStream);

        return lexer;
    }

    @Override
    public Parser getParser(CommonTokenStream asmTokens) {
        parser = new RISCVASMParser(asmTokens);
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
