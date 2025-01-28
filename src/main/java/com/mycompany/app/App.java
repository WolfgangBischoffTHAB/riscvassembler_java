package com.mycompany.app;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.mycompany.assembler.MIPSAssembler;
import com.mycompany.assembler.RiscVAssembler;
import com.mycompany.cpu.CPU;
import com.mycompany.cpu.PipelinedCPU;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.data.Section;
import com.mycompany.linkerscriptparser.LinkerScriptParser;
import com.mycompany.preprocessing.IncludePreprocessor;

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

    private static final String INTERMEDIATE_FILE = "build/preprocessed.s";

    public static void main(String[] args) throws IOException {

        //
        // RISC V
        //

        args = new String[1];
        //String inputFile = "src/test/resources/riscvasm/pipeline_hazards/data_hazard.s";
        String inputFile = "src/test/resources/riscvasm/pipeline_hazards/data_hazard_2.s";

        //String inputFile = "src/test/resources/riscvasm/examples/argmax.s";
        //String inputFile = "src/test/resources/riscvasm/examples/blinker.s";
        //String inputFile = "src/test/resources/riscvasm/examples/memory.s";
        //String inputFile = "src/test/resources/riscvasm/examples/uart.s";
        //String inputFile = "src/test/resources/riscvasm/examples/modifiers.s";
        //String inputFile = "src/test/resources/riscvasm/examples/hello_world.s";
        //String inputFile = "src/test/resources/projects/snake/Main.asm";
        //String inputFile = "src/test/resources/riscvasm/test.s";
        //String inputFile = "src/test/resources/riscvasm/examples/riscvtest_orig.s";
        //String inputFile = "src/test/resources/riscvasm/instructions/add.s";
        //String inputFile = "src/test/resources/riscvasm/instructions/sw.s";
        args[0] = inputFile;
        mainRISCV(args);

        //
        // MIPS
        //

        // args = new String[1];
        // //String inputFile = "src/test/resources/mipsasm/examples/basic_example.asm";
        // String inputFile = "src/test/resources/mipsasm/instructions/add.asm";
        // args[0] = inputFile;
        // mainMIPS(args);
    }

    public static void mainMIPS(String[] args) throws IOException {

        //
        // global variables
        //

        // the GCC compiler adds a funny line: .section	.note.GNU-stack,"",@progbits
        // The section .note.GNU-stack is not defined
        // To not break the code, a dummy section is inserted which is used as a catch-all
        // for all sections that are not defined
        Section dummySection = new Section();
        dummySection.name = "dummy-section";

        //
        // preprocess
        //

        // create build folder
        Files.createDirectories(Paths.get("build"));

        // the first step is always to let the preprocessor resolve .include
        // instructions. Let the compiler run on the combined file in a second step!

        String inputFile = args[0];
        String outputFile = INTERMEDIATE_FILE;
        preprocess(inputFile, outputFile);

        //
        // linker script
        //

        Map<String, Section> sectionMap = new HashMap<>();
        sectionMap.put(dummySection.name, dummySection);

        LinkerScriptParser linkerScriptParser = new LinkerScriptParser();
        linkerScriptParser.parseLinkerScript(sectionMap);

        //
        // assemble
        //

        MIPSAssembler assembler = new MIPSAssembler(sectionMap, dummySection);

        String asmInputFile = INTERMEDIATE_FILE;

        // the raw listener just prints the AST to the console
        //RawOutputListener listener = new RawOutputListener();

        //
        // assemble to machine code
        //

        byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        //ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        assembler.outputHexMachineCode(machineCode, byteOrder);

    }

    public static void mainRISCV(String[] args) throws IOException {

        //
        // global variables
        //

        // the GCC compiler adds a funny line: .section	.note.GNU-stack,"",@progbits
        // The section .note.GNU-stack is not defined
        // To not break the code, a dummy section is inserted which is used as a catch-all
        // for all sections that are not defined
        Section dummySection = new Section();
        dummySection.name = "dummy-section";

        //
        // preprocess
        //

        // create build folder
        Files.createDirectories(Paths.get("build"));

        // the first step is always to let the preprocessor resolve .include
        // instructions. Let the compiler run on the combined file in a second step!

        String inputFile = args[0];
        String outputFile = INTERMEDIATE_FILE;
        preprocess(inputFile, outputFile);

        //
        // linker script
        //

        Map<String, Section> sectionMap = new HashMap<>();
        sectionMap.put(dummySection.name, dummySection);

        LinkerScriptParser linkerScriptParser = new LinkerScriptParser();
        linkerScriptParser.parseLinkerScript(sectionMap);

        //
        // assemble
        //

        RiscVAssembler assembler = new RiscVAssembler(sectionMap, dummySection);

        String asmInputFile = INTERMEDIATE_FILE;

        // // set up the visitor
        // // the extractor assembles AsmLineS by visiting the antlr4 AST
        // RISCASMExtractingOutputListener asmListener = new RISCASMExtractingOutputListener();
        // asmListener.dummySection = dummySection;
        // asmListener.asmLines = assembler.asmLines;
        // asmListener.sectionMap = sectionMap;
        // asmListener.currentSection = assembler.currentSection;

        // the raw listener just prints the AST to the console
        // RawOutputListener listener = new RawOutputListener();

        //
        // assemble to machine code
        //

        byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        //ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        assembler.outputHexMachineCode(machineCode, byteOrder);

        //
        // emulate
        //

        emulate(machineCode);

        System.out.println("done");
    }

    private static void emulate(byte[] machineCode) {

        //CPU cpu = new CPU();
        PipelinedCPU cpu = new PipelinedCPU();
        cpu.pc = 0;
        cpu.memory = machineCode;

        // preload values into registers
        //
        cpu.registerFile[RISCVRegister.REG_SP.ordinal()] = 5;
        cpu.registerFile[RISCVRegister.REG_GP.ordinal()] = 10;

        // //
        // // single cycle processor
        // //

        // for (int i = 0; i < 100; i++) {
        //     cpu.step();
        // }

        //
        // pipelined processor
        //

        int lastCycle = 8;

        for (int i = 0; i < lastCycle; i++) {
            cpu.step();
        }
    }

    private static void preprocess(final String inputFile, final String outputFile) throws FileNotFoundException, IOException {

        System.out.println("Precprocessing input file ...");

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {

            IncludePreprocessor includePreprocessor = new IncludePreprocessor();
            includePreprocessor.preprocess(inputFile, bufferedWriter);

            bufferedWriter.flush();
        }

        System.out.println("Precprocessing input done ...");
    }
}
