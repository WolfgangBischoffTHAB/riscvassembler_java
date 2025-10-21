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
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.assembler.RiscVAssembler;
import com.mycompany.common.ByteArrayUtil;
import com.mycompany.cpu.CPU;
import com.mycompany.cpu.SingleCycle32BitCPU;
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
import com.mycompany.memory.Memory64;
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

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    // public static final int XLEN = 32;
    public static final int XLEN = 64;

    private static final String MAIN_ENTRY_POINT_LABEL = "main";

    private static final int MEMORY_SIZE_IN_BYTE = 1024 * 2;

    private static final String INTERMEDIATE_FILE = "build/preprocessed.s";

    private static final boolean WAIT_FOR_INPUT = false;

    // plain .s assembler source code
    private static final boolean MACHINE_CODE_SOURCE_ASSEMBLY_FILE = true;
    private static final boolean MACHINE_CODE_SOURCE_ELF_FILE = false;
    private static final boolean LINUX_BOOT_IMAGE_FILE = false;

    // // .elf file
    // private static final boolean MACHINE_CODE_SOURCE_ASSEMBLY_FILE = false;
    // private static final boolean MACHINE_CODE_SOURCE_ELF_FILE = true;
    // private static final boolean LINUX_BOOT_IMAGE_FILE = false;

    // // Linux Kernel image
    // private static final boolean MACHINE_CODE_SOURCE_ASSEMBLY_FILE = false;
    // private static final boolean MACHINE_CODE_SOURCE_ELF_FILE = false;
    // private static final boolean LINUX_BOOT_IMAGE_FILE = true;

    public static void main(String[] args) throws IOException {

        // RV32IBaseIntegerInstructionSetDecoder decoder = new
        // RV32IBaseIntegerInstructionSetDecoder();
        // AsmLine asmLine = decoder.decode(0xE0E7D863);
        // int imm = asmLine.numeric_2.intValue();
        // int immValSignExtended = (int) NumberParseUtil
        // .sign_extend_12_bit_to_int32_t(imm);
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
        // String inputFile =
        // "src/test/resources/riscvasm/examples/blinky_memory_mapped_LED.s";

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
        // String inputFile =
        // "src/test/resources/riscvasm/examples/recursive_sum_of_n.s";
        // String inputFile =
        // "src/test/resources/riscvasm/examples/recursive_sum_of_n_other_syntax.s";

        // String inputFile = "src/test/resources/riscvasm/examples/sample_1.s";

        // String inputFile =
        // "src/test/resources/riscvasm/examples/vector_mult_with_masking.s";
        // String inputFile =
        // "src/test/resources/riscvasm/examples/vector_add_example.s";

        // String inputFile = "src/test/resources/riscvasm/instructions/rvv/vmv_v_i.s";

        // String inputFile = "src/test/resources/riscvasm/rvv_testing/vaadd_vv-0.S";

        // String inputFile = "src/test/resources/riscvasm/instructions/rv64i/addiw.s";
        // String inputFile = "src/test/resources/riscvasm/examples/add_sample_2.s";
        // String inputFile = "src/test/resources/riscvasm/examples/add_sample_3.s";
        // String inputFile = "src/test/resources/riscvasm/examples/add_sample_7.s";
        // String inputFile = "src/test/resources/riscvasm/examples/add_sample_11.s";
        // String inputFile = "src/test/resources/riscvasm/examples/add_sample_16.s";

        // String inputFile = "src/test/resources/riscvasm/examples/addi_sample_10.s";

        // String inputFile = "src/test/resources/riscvasm/rvv_testing/compute_vadd_without_rvv.s";
        // String inputFile = "src/test/resources/riscvasm/rvv_testing/compute_vadd.s";
        String inputFile = "src/test/resources/riscvasm/instructions/rvv/vle64_v.s";

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

        // write preprocessed output into the INTERMEDIATE_FILE which is a hardcoded
        // fixed file
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

        // use the fixed, hardcoded intermediate file which is the result of
        // preprocessing
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

        Memory memory = null;
        if (XLEN == 32) {
            memory = new DefaultMemory();
        }
        if (XLEN == 64) {
            memory = new Memory64();
        }

        long startAddress = 0;
        int globalPointerValue = 0;
        int stackPointerValue = 0x00020000;

        if (MACHINE_CODE_SOURCE_ASSEMBLY_FILE) {

            //
            // assemble to machine code
            //

            // byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);

            assembler.assemble(sectionMap, asmInputFile);

            // initialize current position
            for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
                Section section = entry.getValue();
                if (section.outputSection == null) {
                    continue;
                }
                section.outputSection.currentPosition = section.outputSection.memorySpecOrigin;
            }

            // copy data from the sections into memory
            for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {

                Section section = entry.getValue();

                logger.info("-- Section: " + section.name + " ----------------------");

                if (section.outputSection != null) {

                    logger.info("-- Address: " + ByteArrayUtil.byteToHex(section.outputSection.currentPosition) + "");

                    byte[] machineCode = section.byteArrayOutStream.toByteArray();

                    // DEBUG output the byte array to the console
                    // ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
                    // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
                    // outputHexMachineCode(byteArray, byteOrder);

                    long curPos = section.outputSection.currentPosition;
                    memory.copy(curPos, machineCode, 0L, (long) machineCode.length);
                    section.outputSection.currentPosition += machineCode.length;
                }

                logger.info("");

            }

            if ((assembler.labelAddressMap == null)
                    || (!assembler.labelAddressMap.containsKey(MAIN_ENTRY_POINT_LABEL))) {
                throw new RuntimeException("No '" + MAIN_ENTRY_POINT_LABEL
                        + "' label found! Do not know where to execute the application from!");
            }

            // set start address
            startAddress = assembler.labelAddressMap.get(MAIN_ENTRY_POINT_LABEL);

            // set global pointer
            globalPointerValue = 0x00004000;

            // set stack pointer
            stackPointerValue = 0x00020000;

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

            // // elf.setFile("/Users/lapto/dev/riscv/libc_test/a.out");
            // // elf.setFile("src/test/resources/riscvelf/factorial.out");
            // // elf.setFile("C:/Users/lapto/dev/c/zork/a.out");
            // elf.setFile("C:/Users/lapto/dev/riscv/zork_riscv/zork.elf");

            // elf.load();

            // https://github.com/riscv-ovpsim/imperas-riscv-tests/blob/v20230724/riscv-ovpsim/examples/fibonacci/fibonacci.RISCV32.elf
            // elf.setFile("C:/Users/lapto/Downloads/fibonacci.RISCV32.elf");

            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-add");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-addi");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-and");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-andi");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-auipc");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-beq");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bge");
            // OK:
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bgeu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-blt");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bltu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-bne");
            // TODO: Self modifying code!
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-fence_i");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-jal");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-jalr");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lb");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lbu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-ld_st");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lh");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lhu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lui");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-lw");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-ma_data");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-or");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-ori");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sb");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sh");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-simple");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sll");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-slli");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-slt");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-slti");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sltiu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sltu");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-sra");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-srai");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-srl");
            // OK
            // elf.setFile("C:/Users/lapto/dev/riscv/riscv-tests/isa/rv32ui-p-srli");
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
            //elf.memory = memory;
            elf.memory = new Memory64();

            memory = elf.memory;

            //elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-add"); // OK
            //elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-addi"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-addiw"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-addw"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-and"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-andi"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-auipc"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-beq"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-bge"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-bgeu"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-blt"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-bltu"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-bne"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-fence_i"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-jal"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-jalr"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-lb"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-lbu"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-ld"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-ld_st"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-lh"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-lhu"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-lui"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-lw"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-lwu"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-ma_data"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-or"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-ori"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sb"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sd"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sh"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-simple"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sll"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-slli"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-slliw"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sllw"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-slt"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-slti"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sltiu"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sltu"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sra"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-srai"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sraiw"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sraw"); // ERROR

            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-srl"); // ERROR
            // <---- I do not understand test 18
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-srli"); // ERROR
            // <----

            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-srliw"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-srlw"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-st_ld"); //
            // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sub"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-subw"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-sw"); // ERROR
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-xor"); // OK
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-p-xori"); // OK

            //
            // v??? what is v?
            //

            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-add");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-addi");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-addiw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-addw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-and");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-andi");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-auipc");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-beq");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-bge");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-bgeu");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-blt");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-bltu");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-bne");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-fence_i");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-jal");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-jalr");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-lb");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-lbu");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-ld");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-ld_st");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-lh");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-lhu");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-lui");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-lw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-lwu");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-ma_data");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-or");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-ori");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sb");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sd");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sh");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-simple");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sll");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-slli");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-slliw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sllw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-slt");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-slti");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sltiu");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sltu");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sra");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-srai");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sraiw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sraw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-srl");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-srli");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-srliw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-srlw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-st_ld");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sub");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-subw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-sw");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-xor");
            // elf.setFile("src/test/resources/riscvelf/rv64_tests/rv64ui-v-xori");

            // src\test\resources\riscvelf\rv64_tests\rv64um-p-div
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-divu
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-divuw
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-divw
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-mul
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-mulh
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-mulhsu
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-mulhu
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-mulw
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-rem
            // src\test\resources\riscvelf\rv64_tests\rv64um-p-remu

            elf.load();

            // DEBUG
            // byte[] machineCode = elf64.getMachineCode();
            // DEBUG - output machine code as hex
            // ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
            // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
            // BaseAssembler.outputHexMachineCode(machineCode, byteOrder);
            /*
             * //
             * // startAddress (32 bit)
             * //
             * // look for the symbol called "main" or "_start" inside the SHT_SYMTAB
             * // the spice simulator uses the _start symbol
             * Optional<Elf32Sym> optionalSymbol = elf.getSymbolFromSymbolTable("main");
             * Elf32Sym mainEntryPointSymbol = null;
             * if (optionalSymbol.isPresent()) {
             * mainEntryPointSymbol = optionalSymbol.get();
             * } else {
             * optionalSymbol = elf.getSymbolFromSymbolTable("_start");
             * if (optionalSymbol.isPresent()) {
             * mainEntryPointSymbol = optionalSymbol.get();
             * }
             * }
             * startAddress = mainEntryPointSymbol.st_value;
             * 
             * //
             * // set the global pointer register
             * //
             * globalPointerValue = elf.globalPointerValue;
             */

            // if (XLEN == 32) {

            //     //
            //     // startAddress 32 bit
            //     //
            //     // look for the symbol called "main" or "_start" inside the SHT_SYMTAB
            //     // the spice simulator uses the _start symbol
            //     Optional<Elf32Sym> optionalSymbol = elf.getSymbolFromSymbolTable("main");
            //     Elf32Sym mainEntryPointSymbol = null;
            //     if (optionalSymbol.isPresent()) {
            //         mainEntryPointSymbol = optionalSymbol.get();
            //     } else {
            //         optionalSymbol = elf.getSymbolFromSymbolTable("_start");
            //         if (optionalSymbol.isPresent()) {
            //             mainEntryPointSymbol = optionalSymbol.get();
            //         }
            //     }
            //     startAddress = mainEntryPointSymbol.st_value & 0x00000000FFFFFFFFL;
            // }

            if (XLEN == 64) {

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
                startAddress = mainEntryPointSymbol.st_value & 0x00000000FFFFFFFFL;
            }

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

        CPU cpu = emulate(memory, startAddress, globalPointerValue, stackPointerValue);

        //
        // post emulation
        //

        // DEBUG output all registers
        long[] registerFile = cpu.getRegisterFile();
        for (int i = 0; i < 32; i++) {
            System.out.println("x" + (i) + ": " + registerFile[i]);
        }

        if (cpu instanceof SingleCycle64BitCPU) {

            SingleCycle64BitCPU singleCycleCPU = (SingleCycle64BitCPU) cpu;

            // // DEBUG output all registers
            // for (int i = 0; i < 32; i++) {
            // System.out.println("x" + (i) + ": " + singleCycleCPU.registerFile[i]);
            // }

            // DEBUG
            // BaseAssembler.outputHexMachineCode(singleCycleCPU.memory, byteOrder);

            long value = singleCycleCPU.memory.readLong(0x10040, ByteOrder.LITTLE_ENDIAN);
            logger.info(ByteArrayUtil.byteToHex(value));

            value = singleCycleCPU.memory.readLong(0x10048, ByteOrder.LITTLE_ENDIAN);
            logger.info(ByteArrayUtil.byteToHex(value));

            value = singleCycleCPU.memory.readLong(0x10050, ByteOrder.LITTLE_ENDIAN);
            logger.info(ByteArrayUtil.byteToHex(value));

            value = singleCycleCPU.memory.readLong(0x10058, ByteOrder.LITTLE_ENDIAN);
            logger.info(ByteArrayUtil.byteToHex(value));

        }

        System.out.println("done");
    }

    public static int high(long x) {
        return (int) (x >> 32);
    }

    public static int low(long x) {
        return (int) x;
    }

    private static CPU emulate(final Memory memory, final long main_entry_point_address, int globalPointerValue,
            int stackPointerValue) throws IOException {

        // if (XLEN != 32) {
        //     throw new RuntimeException("change CPU to correct XLEN!");
        // }
        if (XLEN != 64) {
            throw new RuntimeException("change CPU to correct XLEN!");
        }

        // SingleCycle32BitCPU cpu = new SingleCycle32BitCPU();
        SingleCycle64BitCPU cpu = new SingleCycle64BitCPU();
        // PipelinedCPU cpu = new PipelinedCPU();

        // DEBUG main entry point address
        logger.trace("Main Entry Point: " + ByteArrayUtil.byteToHex((int) main_entry_point_address, null, "%1$08X"));
        // logger.trace("" + main_entry_point_address);
        
        if (XLEN == 32) {
            // for 32 bit
            cpu.pc = (int) (main_entry_point_address & 0x00000000FFFFFFFFL);
        }
        if (XLEN == 64) {
            // for 64 bit
            cpu.pc = main_entry_point_address;
        }

        // logger.info("" + cpu.pc);
        // cpu.pc = Integer.toUnsignedInt(main_entry_point_address);
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
        // cpu.registerFile[RISCVRegister.REG_SP.getIndex()] = machineCode.length - 4;
        cpu.registerFile[RISCVRegister.REG_SP.getIndex()] = stackPointerValue;

        //
        // frame-pointer (s0/fp, x8 register)
        //
        cpu.registerFile[RISCVRegister.REG_FP.getIndex()] = 0;

        // cpu.memory = new byte[machineCode.length + 8];
        // cpu.memory[80] = 1;
        // cpu.memory[81] = 2;
        // cpu.memory[82] = 3;
        // cpu.memory[83] = 4;

        cpu.memory = memory;

        // System.arraycopy(machineCode, 0, cpu.memory, 0, machineCode.length);

        // cpu.memory[machineCode.length + 4] = (byte) 0xFF;
        // cpu.memory[machineCode.length + 5] = (byte) 0xFF;
        // cpu.memory[machineCode.length + 6] = (byte) 0xFF;
        // cpu.memory[machineCode.length + 7] = (byte) 0xFF;

        // // FOR DEBUGGING MULTISTAGE PIPELINE APPS
        // // preload values into registers
        // //
        // for (int i = 0; i < 32; i++) {
        // cpu.registerFile[i] = i;
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

        cpu.shutdown();

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
     * public static void mainMIPS(String[] args) throws IOException {
     * 
     * //
     * // global variables
     * //
     * 
     * // the GCC compiler adds a funny line: .section .note.GNU-stack,"",@progbits
     * // The section .note.GNU-stack is not defined
     * // To not break the code, a dummy section is inserted which is used as a
     * // catch-all
     * // for all sections that are not defined
     * Section dummySection = new Section();
     * dummySection.name = "dummy-section";
     * 
     * //
     * // preprocess
     * //
     * 
     * // create build folder
     * Files.createDirectories(Paths.get("build"));
     * 
     * // the first step is always to let the preprocessor resolve .include
     * // instructions. Let the compiler run on the combined file in a second step!
     * 
     * String inputFile = args[0];
     * String outputFile = INTERMEDIATE_FILE;
     * preprocess(inputFile, outputFile);
     * 
     * //
     * // linker script
     * //
     * 
     * Map<String, Section> sectionMap = new HashMap<>();
     * sectionMap.put(dummySection.name, dummySection);
     * 
     * LinkerScriptParser linkerScriptParser = new LinkerScriptParser();
     * linkerScriptParser.parseLinkerScript(sectionMap);
     * 
     * //
     * // assemble
     * //
     * 
     * MIPSAssembler assembler = new MIPSAssembler(sectionMap, dummySection);
     * 
     * String asmInputFile = INTERMEDIATE_FILE;
     * 
     * // the raw listener just prints the AST to the console
     * // RawOutputListener listener = new RawOutputListener();
     * 
     * //
     * // assemble to machine code
     * //
     * 
     * byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);
     * 
     * ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
     * // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
     * BaseAssembler.outputHexMachineCode(machineCode, byteOrder);
     * }
     */
}
