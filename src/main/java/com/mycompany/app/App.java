package com.mycompany.app;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.assembler.RiscVAssembler;
import com.mycompany.common.ByteArrayUtil;
import com.mycompany.cpu.CPU;
import com.mycompany.cpu.SingleCycle64BitCPU;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;
import com.mycompany.elf.Elf32;
import com.mycompany.elf.Elf32Sym;
import com.mycompany.elf.Elf64;
import com.mycompany.elf.Elf64Sym;
import com.mycompany.linkerscriptparser.LinkerScriptParser;
import com.mycompany.linuxbootimage.LinuxBootImage;
import com.mycompany.memory.DefaultMemory;
import com.mycompany.memory.Memory;
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

    private static final String MAIN_ENTRY_POINT_LABEL = "main";

    private static final int MEMORY_SIZE_IN_BYTE = 1024 * 2;

    private static final String INTERMEDIATE_FILE = "build/preprocessed.s";

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static final boolean WAIT_FOR_INPUT = false;

    // private static final boolean MACHINE_CODE_SOURCE_ASSEMBLY_FILE = true;
    private static final boolean MACHINE_CODE_SOURCE_ASSEMBLY_FILE = false;

    private static final boolean MACHINE_CODE_SOURCE_ELF_FILE = true;
    // private static final boolean MACHINE_CODE_SOURCE_ELF_FILE = false;

    // private static final boolean LINUX_BOOT_IMAGE_FILE = true;
    private static final boolean LINUX_BOOT_IMAGE_FILE = false;

    public static void main(String[] args) throws IOException {

        // RV32IBaseIntegerInstructionSetDecoder decoder = new RV32IBaseIntegerInstructionSetDecoder();
        // AsmLine asmLine = decoder.decode(0xE0E7D863);
        // int imm = asmLine.numeric_2.intValue();
        // int immValSignExtended = (int) NumberParseUtil
        //                     .sign_extend_12_bit_to_int32_t(imm);
        // System.out.println(asmLine);

        // AsmLine asmLine = new AsmLine<>();
        // asmLine.mnemonic = Mnemonic.I_BGE;
        // asmLine.register_0 = RISCVRegister.REG_X15;
        // asmLine.register_1 = RISCVRegister.REG_X14;
        // asmLine.numeric_2 = (long) -2544;
        // RISCVEncoder encoder = new RISCVEncoder();
        // long machineCode = encoder.encode(asmLine, null, null, 100);
        // System.out.println(ByteArrayUtil.byteToHex((int) machineCode));

        logger.debug("Start of Application");

        //
        // RISC V
        //

        args = new String[1];

        // String inputFile =
        // "src/test/resources/riscvasm/pipeline_hazards/data_hazard.s";
        // String inputFile =
        // "src/test/resources/riscvasm/pipeline_hazards/data_hazard_2.s";
        // String inputFile =
        // "src/test/resources/riscvasm/pipeline_hazards/data_hazard_3.s";
        // String inputFile =
        // "src/test/resources/riscvasm/pipeline_hazards/data_hazard_requires_stall.s";

        // String inputFile = "src/test/resources/riscvasm/examples/fibonacci_rvcc.s";
        // String inputFile = "src/test/resources/riscvasm/examples/argmax.s";
        // String inputFile = "src/test/resources/riscvasm/examples/blinker.s";
        // String inputFile = "src/test/resources/riscvasm/examples/memory.s";
        // String inputFile = "src/test/resources/riscvasm/examples/uart.s";
        // String inputFile = "src/test/resources/riscvasm/examples/modifiers.s";
        // String inputFile = "src/test/resources/riscvasm/examples/hello_world.s";
        // String inputFile = "src/test/resources/riscvasm/examples/riscvtest_orig.s";
        // String inputFile = "src/test/resources/riscvasm/examples/for_loop_2.s";
        // String inputFile =
        // "src/test/resources/riscvasm/examples/square_with_driver.s";
        // String inputFile = "src/test/resources/riscvasm/examples/if.s";
        // String inputFile = "src/test/resources/riscvasm/examples/riscvtest.s";
        // String inputFile =
        // "src/test/resources/riscvasm/examples/riscvtest_harris_harris.s";
        // String inputFile =
        // "src/test/resources/riscvasm/examples/while_true_endless_loop.s";
        // String inputFile =
        // "src/test/resources/riscvasm/examples/while_true_endless_loop_writeMem.s";
        // String inputFile =
        // "src/test/resources/riscvasm/examples/function_call_c_abi.s";
        // String inputFile = "src/test/resources/riscvasm/examples/quicksort.s";
        // String inputFile = "src/test/resources/riscvasm/examples/quicksort_2.s";
        // String inputFile = "src/test/resources/riscvasm/examples/quicksort_clang.s";
        // String inputFile = "src/test/resources/riscvasm/examples/blinky_memory_mapped_LED.s";

        // String inputFile = "src/test/resources/riscvasm/instructions/beq.s";
        // String inputFile = "src/test/resources/riscvasm/instructions/m/remu.s";

        // String inputFile = "src/test/resources/projects/snake/Main.asm";

        // String inputFile = "src/test/resources/riscvasm/instructions/add.s";
        // String inputFile = "src/test/resources/riscvasm/instructions/sw.s";
        // String inputFile = "src/test/resources/riscvasm/instructions/sw.s";
        // String inputFile = "src/test/resources/riscvasm/instructions/lw.s";

        // String inputFile = "src/test/resources/riscvasm/examples/printf.s";
        // String inputFile = "src/test/resources/riscvasm/examples/add_sample.s";
        // String inputFile = "src/test/resources/riscvasm/examples/string_length.s";
        // String inputFile = "src/test/resources/riscvasm/examples/slti.s";
        // String inputFile = "src/test/resources/riscvasm/examples/bltu.s";
        // String inputFile = "src/test/resources/riscvasm/instructions/la.s";
        // String inputFile = "src/test/resources/riscvasm/examples/fib.s";
        // String inputFile = "src/test/resources/riscvasm/examples/expression.s";

        // String inputFile = "src/test/resources/riscvelf/factorial/factorial.s";
        // String inputFile = "src/test/resources/riscvasm/examples/gcd.s";
        // String inputFile = "src/test/resources/riscvasm/examples/div.s";

        //String inputFile = "src/test/resources/riscvasm/examples/vector_mult_with_masking.s";
        //String inputFile = "src/test/resources/riscvasm/examples/vector_add_example.s";

        // String inputFile = "src/test/resources/riscvasm/instructions/rvv/vmv_v_i.s";

        String inputFile = "src/test/resources/riscvasm/rvv_testing/vaadd_vv-0.S";

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

    public static void mainRISCV(String[] args) throws IOException {

        //
        // global variables
        //

        // the GCC compiler adds a funny line: .section .note.GNU-stack,"",@progbits
        // The section .note.GNU-stack is not defined
        // To not break the code, a dummy section is inserted which is used as a
        // catch-all
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

        // write preprocessed output into the INTERMEDIATE_FILE which is a hardcoded fixed file
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

        // use the fixed, hardcoded intermediate file which is the result of preprocessing
        String asmInputFile = INTERMEDIATE_FILE;

        // // set up the visitor
        // // the extractor assembles AsmLineS by visiting the antlr4 AST
        // RISCASMExtractingOutputListener asmListener = new
        // RISCASMExtractingOutputListener();
        // asmListener.dummySection = dummySection;
        // asmListener.asmLines = assembler.asmLines;
        // asmListener.sectionMap = sectionMap;
        // asmListener.currentSection = assembler.currentSection;

        // the raw listener just prints the AST to the console
        // RawOutputListener listener = new RawOutputListener();

        DefaultMemory memory = new DefaultMemory();

        long startAddress = 0;
        int globalPointerValue = 0;

        if (MACHINE_CODE_SOURCE_ASSEMBLY_FILE) {

            //
            // assemble to machine code
            //

            byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);

            memory.copy(0, machineCode, 0, machineCode.length);

            if ((assembler.labelAddressMap == null) 
                || (!assembler.labelAddressMap.containsKey(MAIN_ENTRY_POINT_LABEL))) {
                throw new RuntimeException("No '" + MAIN_ENTRY_POINT_LABEL
                        + "' label found! Do not know where to execute the application from!");
            }

            startAddress = assembler.labelAddressMap.get(MAIN_ENTRY_POINT_LABEL).intValue();

        }

        // read the machine code from an elf file
        if (MACHINE_CODE_SOURCE_ELF_FILE) {

            //
            // elf to machine code
            //

            // //
            // // RV32
            // //

            // Elf32 elf = new Elf32();
            // elf.memory = memory;
            
            // //elf.setFile("src/test/resources/riscvelf/factorial.out");
            // //elf.setFile("C:/Users/lapto/dev/c/zork/a.out");
            // elf.setFile("C:/Users/lapto/dev/riscv/zork_riscv/zork.elf");

            // elf.load();

            // https://github.com/riscv-ovpsim/imperas-riscv-tests/blob/v20230724/riscv-ovpsim/examples/fibonacci/fibonacci.RISCV32.elf
            //elf.setFile("C:/Users/lapto/Downloads/fibonacci.RISCV32.elf");
            
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-add");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-addi");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-and");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-andi");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-auipc");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-beq");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bge");
            // OK:
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bgeu");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-blt");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bltu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bne");
            // TODO: Self modifying code!
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-fence_i");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-jal");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-jalr");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lb");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lbu");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-ld_st");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lh");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lhu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lui");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lw");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-ma_data");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-or");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-ori");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sb");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sh");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-simple");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sll");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-slli");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-slt");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-slti");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sltiu");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sltu");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sra");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-srai");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-srl");
            // OK
            //elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-srli");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-st_ld");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sub");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sw");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-xor");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-xori");

            //
            // RV64
            //

            Elf64 elf = new Elf64();
            elf.memory = memory;

            elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv64ui-p-add");
            
            elf.load();

            // DEBUG
            // byte[] machineCode = elf64.getMachineCode();
            // DEBUG - output machine code as hex
            //ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
            // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
            //BaseAssembler.outputHexMachineCode(machineCode, byteOrder);
/*
            //
            // startAddress (32 bit)
            //
            // look for the symbol called "main" or "_start" inside the SHT_SYMTAB
            // the spice simulator uses the _start symbol
            Optional<Elf32Sym> optionalSymbol = elf.getSymbolFromSymbolTable("main");
            Elf32Sym mainEntryPointSymbol = null;
            if (optionalSymbol.isPresent()) {
                mainEntryPointSymbol = optionalSymbol.get();
            } else {
                optionalSymbol = elf.getSymbolFromSymbolTable("_start");
                if (optionalSymbol.isPresent()) {
                    mainEntryPointSymbol = optionalSymbol.get();
                }
            }
            startAddress = mainEntryPointSymbol.st_value;

            //
            // set the global pointer register
            //
            globalPointerValue = elf.globalPointerValue;
 */

            //
            // startAddress 64 bit
            //
            // look for the symbol called "main" or "_start" inside the SHT_SYMTAB
            // the spice simulator uses the _start symbol
            Optional<Elf64Sym> optionalSymbol = elf.getSymbolFromSymbolTable("main");
            Elf64Sym mainEntryPointSymbol = null;
            if (optionalSymbol.isPresent()) {
                mainEntryPointSymbol = optionalSymbol.get();
            } else {
                optionalSymbol = elf.getSymbolFromSymbolTable("_start");
                if (optionalSymbol.isPresent()) {
                    mainEntryPointSymbol = optionalSymbol.get();
                }
            }
            startAddress = mainEntryPointSymbol.st_value;

            //
            // set the global pointer register
            //
            globalPointerValue = (int) elf.globalPointerValue;

        }

        if (LINUX_BOOT_IMAGE_FILE) {

            LinuxBootImage linuxBootImage = new LinuxBootImage();
            linuxBootImage.memory = memory;
            
            linuxBootImage.setFile("src/test/resources/linux/Linux_image_6_1_14_RV32IMA_NoMMU");
            linuxBootImage.load();

            startAddress = LinuxBootImage.START_ADDRESS_OF_RAM;

        }

        //
        // emulate
        //

        CPU cpu = emulate(memory, startAddress, globalPointerValue);

        //
        // post emulation
        //

        if (cpu instanceof SingleCycle64BitCPU) {

            SingleCycle64BitCPU singleCycleCPU = (SingleCycle64BitCPU) cpu;

            // DEBUG output all registers
            for (int i = 0; i < 32; i++) {
                System.out.println("x" + (i) + ": " + singleCycleCPU.registerFile[i]);
            }

            // DEBUG
            //BaseAssembler.outputHexMachineCode(singleCycleCPU.memory, byteOrder);

        }

        System.out.println("done");
    }

    private static CPU emulate(final Memory memory, final long main_entry_point_address, int globalPointerValue) throws IOException {

        SingleCycle64BitCPU cpu = new SingleCycle64BitCPU();
        //PipelinedCPU cpu = new PipelinedCPU();

        // DEBUG main entry point address
        logger.info("Main Entry Point: " + ByteArrayUtil.byteToHex(main_entry_point_address));

        cpu.pc = main_entry_point_address;
        cpu.registerFile[RISCVRegister.REG_GP.getIndex()] = globalPointerValue;

        //
        // return address (ra, x1 register)
        //
        // the initial return address is retrieved from the application loader
        // so that the app can return to that address
        // Without loader, we set it to 0xCAFEBABE = 3405691582 dec
        cpu.registerFile[RISCVRegister.REG_RA.getIndex()] = 0xCAFEBABE;

        //
        // stack-pointer (sp, x2) register:
        //
        // should not point into the source code (menomics in memory!)
        // because using the stack will then override the application code!
        //
        // stack should grow down, so set it to the highest memory address possible
        //cpu.registerFile[RISCVRegister.REG_SP.getIndex()] = machineCode.length - 4;
        cpu.registerFile[RISCVRegister.REG_SP.getIndex()] = 0x20000;

        //
        // frame-pointer (s0/fp, x8 register)
        //
        cpu.registerFile[RISCVRegister.REG_FP.getIndex()] = 0;

        //cpu.memory = new byte[machineCode.length + 8];
        // cpu.memory[80] = 1;
        // cpu.memory[81] = 2;
        // cpu.memory[82] = 3;
        // cpu.memory[83] = 4;

        cpu.memory = memory;

        //System.arraycopy(machineCode, 0, cpu.memory, 0, machineCode.length);

        //cpu.memory[machineCode.length + 4] = (byte) 0xFF;
        //cpu.memory[machineCode.length + 5] = (byte) 0xFF;
        //cpu.memory[machineCode.length + 6] = (byte) 0xFF;
        //cpu.memory[machineCode.length + 7] = (byte) 0xFF;

        // // FOR DEBUGGING MULTISTAGE PIPELINE APPS
        // // preload values into registers
        // //
        // for (int i = 0; i < 32; i++) {
        //     cpu.registerFile[i] = i;
        // }

        if (WAIT_FOR_INPUT) {
            logger.info("Enter any text to start emulation!");
            System.out.println("Enter any text to start emulation!");
            
            // this code reads the users input from stdin / System.in
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            @SuppressWarnings("unused")
            String sourceLine = scanner.nextLine();
        }

        boolean limited = false;
        if (limited) {
            int lastCycle = 100;
            for (int i = 0; i < lastCycle; i++) {
                cpu.step();
            }
        } else {
            boolean done = false;
            while (!done) {
                done = !cpu.step();
            }
        }

        return cpu;
    }

    private static void preprocess(final String inputFile, final String outputFile)
            throws FileNotFoundException, IOException {

        System.out.println("Precprocessing input file ...");

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {

            IncludePreprocessor includePreprocessor = new IncludePreprocessor();
            includePreprocessor.preprocess(inputFile, bufferedWriter);

            bufferedWriter.flush();
        }

        System.out.println("Precprocessing input done ...");
    }
    
/*
    public static void mainMIPS(String[] args) throws IOException {

        //
        // global variables
        //

        // the GCC compiler adds a funny line: .section .note.GNU-stack,"",@progbits
        // The section .note.GNU-stack is not defined
        // To not break the code, a dummy section is inserted which is used as a
        // catch-all
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
        // RawOutputListener listener = new RawOutputListener();

        //
        // assemble to machine code
        //

        byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        BaseAssembler.outputHexMachineCode(machineCode, byteOrder);
    }
 */
}
