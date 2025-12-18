package com.mycompany.cpu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.RISCVRegister;
import com.mycompany.decoder.DelegatingDecoder;
import com.mycompany.decoder.RawPrintingDecoder;
import com.mycompany.filehandling.FileHandling;
import com.mycompany.memory.Memory;

public class SingleCycle64BitCPU extends AbstractCPU {

    /** This CSR (Configuration and Status register) stores VLEN */
    private static final int RVV_CSR_VLENB = 0xFFF;

    /**
     * This is the hardware implementation specific VLEN value. It says how many
     * bits
     * one of the V-Extension register (v0, v1, ..., v31) has.
     */
    private static final long VLEN = 32;

    private static final Logger logger = LoggerFactory.getLogger(SingleCycle64BitCPU.class);

    public long pc;

    public Memory memory;

    public DelegatingDecoder decoder = new DelegatingDecoder();

    public RawPrintingDecoder rawPrintingDecoder = new RawPrintingDecoder();

    public FileHandling fileHandling = new FileHandling();

    // Zicsr extension register id
    private long heartIdCSRRegister = 0x00;

    private boolean singleStepping;

    private boolean debugASMLineOutput;

    // print instructions inside the instruction executors
    private boolean printInstructions = true;
    // private boolean printInstructions = false;

    private Random random = new Random();

    public long[] registerFile = new long[32];

    private BufferedWriter traceBufferedWriter;

    // Vector Extension (RVV) Register file

    public byte[] v0 = new byte[4 * 64];
    public byte[] v1 = new byte[4 * 64];
    public byte[] v2 = new byte[4 * 64];

    @SuppressWarnings("unused")
    private int lMultiplier;

    /**
     * Selected Element Width
     */
    private int sew;

    /**
     * application vector length - using the I_VSETVLI, I_VSETIVLI instructions,
     * this value is specified by the user in each iteration of strip mining. The
     * user will specify the total remaining elements to process. The RISC-V Vector
     * Engine will determine how many elements it can process this iteration of
     * strip-mining which frequently will be a smaller value and return that smaller
     * value in the first (destinatio) register as well as write that value into the
     * vl register. The strip mining process continues until the vectors are
     * processed.
     */
    private long avl;

    /**
     * Vector Length set by setvl, setvli.
     * https://github.com/riscvarchive/riscv-v-spec/blob/master/v-spec.adoc#vector-length-register-vl
     *
     * The vl register holds an unsigned integer specifying the number of elements
     * to be updated with results from a vector instruction (in this iteration of
     * the strip mining loop), as further detailed in Section Prestart, Active,
     * Inactive, Body, and Tail Element Definitions.
     */
    private long vl;

    /**
     * ctor
     */
    public SingleCycle64BitCPU() {
        // set zero
        registerFile[0] = 0;
    }

    public long readRegisterFile(int index) {

        // register zero is hardcoded zero
        if (index == 0) {
            return 0;
        }

        // set the value
        return registerFile[index];
    }

    public void writeRegisterFile(final int register_index, final long value) {

        // write to zero register has no effect
        if (register_index == RISCVRegister.REG_ZERO.getIndex()) {
            return;
        }

        // set the value
        registerFile[register_index] = value;
    }

    @Override
    public void printRegisterFile() {
        long[] registerFile = getRegisterFile();
        for (int i = 0; i < 32; i++) {
            System.out.println("x" + (i) + ": " + registerFile[i]);
        }
    }

    /**
     * https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html
     *
     * @throws IOException
     */
    public boolean step() throws IOException {

        if ((pc == 0xFFFFFFFF) || (pc == 0xCAFEBABE)) {
            logger.info("PC is 0xFFFFFFFF! End of application!");

            return false;
        }

        // ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        // FETCH - use PC to load instruction from memory
        // final int instruction = ByteArrayUtil.fourByteToInt(memory[pc + 0], memory[pc
        // + 1], memory[pc + 2],
        // memory[pc + 3], byteOrder);

        // DEBUG
        if (logger.isTraceEnabled()) {
            logger.trace("PC: " + ByteArrayUtil.byteToHex(pc));
        }

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        final int instruction = memory.readWord(pc, byteOrder);

        // if ((instruction == 0x00000000) || (instruction == 0xFFFFFFFF)) {
        // logger.info("instruction is 0x00 or 0xFF. Aborting CPU run!");
        // // throw new RuntimeException("Done");
        // return false;
        // }

        // if (pc == 0x10154) {
        // System.out.println("test");
        // }

        // logger.info("PC: " + ByteArrayUtil.byteToHex(pc) + " Instruction: " +
        // ByteArrayUtil.byteToHex(instruction));

        // DECODE - use decoder to turn 32 bits into an instruction ASM Line including
        // parameters and opcode
        decoder.memory = memory;
        List<AsmLine<?>> asmLines = decoder.decode(pc);

        if (null == asmLines) {
            throw new RuntimeException("No machine code retrieved!");
        }

        boolean result = true;
        for (AsmLine<?> asmLine : asmLines) {

            if (asmLine.mnemonic == null) {

                logger.trace(asmLine.toString());
                throw new RuntimeException(
                        "Decoding instruction without mnemonic! " + ByteArrayUtil.byteToHex(asmLine.instruction)
                                + " PC: " + ByteArrayUtil.byteToHex(pc));
            }

            result &= executeAsmLine(asmLine);
        }

        return result;
    }

    private boolean executeAsmLine(AsmLine<?> asmLine) throws IOException {

        // prepare trace file
        if (traceBufferedWriter == null) {
            Files.createDirectories(Paths.get("log"));
            traceBufferedWriter = new BufferedWriter(new FileWriter("log/cpu_trace.log"));
        }

        // DEBUG output ASM line
        // debugASMLineOutput = true;
        debugASMLineOutput = false;
        if (debugASMLineOutput) {

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("PC: ");
            stringBuilder.append(pc);
            stringBuilder.append(" (");
            stringBuilder.append(ByteArrayUtil.longToHex("%08x", pc));
            stringBuilder.append("). Loaded Instr: HEX: ");
            stringBuilder.append(ByteArrayUtil.intToHex("%08x", asmLine.instruction));
            stringBuilder.append(" ");
            stringBuilder.append(asmLine.toString());

            String tempData = stringBuilder.toString();
            traceBufferedWriter.append(tempData).append("\n");

            // DEBUG
            logger.info(tempData);
        }

        // // DEBUG do not forget the trailing L because PC is now an long register!
        // if (pc == 0x800006b8L) {
        // logger.info("test");
        // }

        // singleStepping = true;
        singleStepping = false;
        if (singleStepping) {
            printMemoryAroundPC(5);
            System.out.println("");
        }

        boolean debugASMLineOutput2 = false;
        // boolean debugASMLineOutput2 = true;
        if (debugASMLineOutput2) {
            logger.info(ByteArrayUtil.byteToHex(pc) + ": " + asmLine.toString() + "         ["
                    + ByteArrayUtil.byteToHexLowerCase(asmLine.machineCode) + "]");
        }

        // https://projectf.io/posts/riscv-cheat-sheet/
        // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html

        int addr;
        int value;
        long value_l;
        long register_0_value;
        int register_1_value;
        int register_2_value;
        byte[] let;
        StringBuilder stringBuilder;

        long register_0_value_l;
        long register_1_value_l;
        long register_2_value_l;

        long addressA0;
        long addressA1;
        long addressA2;
        int fileHandle;

        int immValSignExtended;
        int result;
        long result_w;

        int csrId;
        long csrValue;

        byte[] rvvReg;

        switch (asmLine.mnemonic) {

            case I_PRINT_REG:
                if (printInstructions) {
                    logger.info("PC: " + ByteArrayUtil.byteToHex(pc) + " add: " + asmLine);
                }
                String registerName = RISCVRegister.toStringAbi((RISCVRegister) asmLine.register_0);
                register_0_value = readRegisterFile(asmLine.register_0.getIndex());
                logger.info("Register " + registerName + " = " + ByteArrayUtil.byteToHex(register_0_value) + " ("
                        + register_0_value + ")");

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ADD:
                logger.trace("add: " + asmLine);

                // register_0_value = (int) readRegisterFile(asmLine.register_1.getIndex());
                // register_1_value = (int) readRegisterFile(asmLine.register_2.getIndex());
                // value = register_0_value + register_1_value;
                // logger.trace(ByteArrayUtil.byteToHex(value));
                // writeRegisterFile(asmLine.register_0.getIndex(), value);

                register_0_value_l = readRegisterFile(asmLine.register_1.getIndex());
                register_1_value_l = readRegisterFile(asmLine.register_2.getIndex());
                value_l = register_0_value_l + register_1_value_l;
                writeRegisterFile(asmLine.register_0.getIndex(), value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ADDI:
                // rd = rs1 + imm
                logger.trace("addi: " + asmLine);

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                long immediate_value = asmLine.numeric_2.longValue();

                if (logger.isTraceEnabled()) {
                    logger.trace("1st Register Name: " + asmLine.register_1.toStringAbi());
                    logger.trace("1st Register Value: " + register_1_value_l);
                    logger.trace("2nd Immediate Value: " + immediate_value);
                    logger.trace("dest Register Name: " + asmLine.register_0.toStringAbi());
                }

                if ((asmLine.register_0 == RISCVRegister.REG_ZERO) && (asmLine.register_1 == RISCVRegister.REG_ZERO)
                        && (immediate_value == 0)) {

                    // this is a nop instruction

                    printRegisterFile();

                    // DEBUG print stack frame
                    // printStackFrame();
                    System.out.println("");
                }

                // Java(TM) automatically performs sign extend during conversion to long!
                result_w = register_1_value_l + immediate_value;

                // System.out.println("New: " + ByteArrayUtil.byteToHex(result));
                // result_w = signExtend32BitTo64Bit(result);

                writeRegisterFile(asmLine.register_0.getIndex(), result_w);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_AND:
                logger.trace("and: " + asmLine);
                // Performs bitwise AND on registers rs1 and rs2 and place the result in rd
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        & readRegisterFile(asmLine.register_2.getIndex()));

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ANDI:
                logger.trace("andi: " + asmLine);
                // Format: andi rd, rs1, imm
                // Description: Performs bitwise AND on register rs1 and the sign-extended
                // 12-bit
                // immediate and place the result in rd. This is the basic bitwise AND operator
                // but
                // instead of using two registers, it uses a 12-bit immediate which is sign
                // extended.
                // Implementation: x[rd] = x[rs1] & sext(immediate)
                // value = (int) readRegisterFile(asmLine.register_1.getIndex())
                // & ((int)
                // NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));
                // writeRegisterFile(asmLine.register_0.getIndex(), value);

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                immediate_value = NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                value_l = register_1_value_l & immediate_value;
                writeRegisterFile(asmLine.register_0.getIndex(), value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LUI:
                // rd <- imm20 << 12

                // LUI (load upper immediate) is used to build 32-bit constants and uses the
                // U-type format. LUI places the 32-bit U-immediate value into the destination
                // register rd, filling in the lowest 12 bits with zeros.

                // Java(TM) automatically performs sign extend during conversion to long!
                long luiImmediate = (asmLine.numeric_1.intValue() << 12L);
                // luiImmediate = signExtend32BitTo64Bit(luiImmediate);

                // logger.info(ByteArrayUtil.byteToHex(luiImmediate));

                writeRegisterFile(asmLine.register_0.getIndex(), luiImmediate);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_AUIPC:
                // Add upper immediate to PC (and store the result into rd)
                // auipc rd, imm
                // rd <- PC + imm20 << 12; pc += 4;
                logger.trace("auipc");
                writeRegisterFile(asmLine.register_0.getIndex(), (pc + (asmLine.numeric_1 << 12L)));

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_JAL:
                // rd = pc+4; pc += imm
                // logger.trace("jal");

                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);
                long jumpDistance = asmLine.numeric_1;

                // ???
                asmLine.branchTaken = true;

                // increment PC
                pc += jumpDistance;
                break;

            case I_JALR:
                // format: jalr rd, imm(rs1)
                // example: jalr x0, 0(x0)
                //
                // format: jalr t0, t1 ---> jalr t0, t1, 0 ---> jalr t0, 0(t1)
                //
                // rd = pc + 4
                // pc = rs1 + imm

                if (logger.isTraceEnabled()) {
                    logger.trace("jalr: " + asmLine);
                }

                // DEBUG
                if (logger.isTraceEnabled()) {
                    logger.trace("register_1 content: "
                            + ByteArrayUtil.byteToHex(readRegisterFile(asmLine.register_1.getIndex())));
                }

                // pc = rs1 + imm
                immValSignExtended = (int) NumberParseUtil
                        .sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());
                int pcReplacement = (int) readRegisterFile(asmLine.register_1.getIndex()) + immValSignExtended;

                // rd = pc + 4;
                // registerFile[asmLine.register_0.getIndex()] = pc + 4;
                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);

                // DEBUG
                logger.trace("Current PC: " + ByteArrayUtil.byteToHex(pc));
                logger.trace("New PC: " + ByteArrayUtil.byteToHex(pcReplacement));

                asmLine.branchTaken = true;

                if (pc == pcReplacement) {
                    throw new RuntimeException(
                            "RET instruction or Endless Loop Detected at PC = 0x" + ByteArrayUtil.longToHex(pc));
                }

                // increment PC
                pc = pcReplacement;
                break;

            case I_BEQ:
                // Take the branch if registers rs1 and rs2 are equal.
                // if (x[rs1] == x[rs2]) pc += sext(offset)
                logger.trace("beq");
                if (readRegisterFile(asmLine.register_0.getIndex()) == readRegisterFile(
                        asmLine.register_1.getIndex())) {
                    asmLine.branchTaken = true;
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BNE:
                logger.trace("bne: " + asmLine);

                // Perform full register compares! 64 bit on RV64, 32 bit on RV32
                register_0_value_l = readRegisterFile(asmLine.register_0.getIndex());
                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                if (register_0_value_l != register_1_value_l) {
                    asmLine.branchTaken = true;
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BLT:
                // if (rs1 < rs2) pc += imm
                // DEBUG
                long blt_a = readRegisterFile(asmLine.register_0.getIndex());
                long blt_b = readRegisterFile(asmLine.register_1.getIndex());
                // Implementation
                if (blt_a < blt_b) {
                    logger.trace("blt " + blt_a + " < " + blt_b + ". Branch is taken!");

                    // pc += asmLine.numeric_2.intValue();

                    // The offset is sign-extended and added to the address of the branch
                    // instruction to give the target address.
                    // immValSignExtended = (int) NumberParseUtil
                    // .sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                    // logger.info("I_BGE: " + immValSignExtended);

                    // pc += immValSignExtended;

                    asmLine.branchTaken = true;

                    pc += asmLine.numeric_2.intValue();
                } else {
                    logger.trace("blt " + blt_a + " < " + blt_b + ". Branch is NOT taken!");
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BGE:
                // bge rs1, sr2, imm
                // if (rs1 >= rs2) pc += imm
                register_0_value = (int) readRegisterFile(asmLine.register_0.getIndex());
                register_1_value = (int) readRegisterFile(asmLine.register_1.getIndex());
                // DEBUG
                logger.trace("bge " + register_0_value + " >= " + register_1_value);
                // Implementation
                if (register_0_value >= register_1_value) {

                    // The offset is sign-extended and added to the address of the branch
                    // instruction to give the target address.
                    // immValSignExtended = (int) NumberParseUtil
                    // .sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                    // logger.info("I_BGE: " + immValSignExtended);

                    // pc += immValSignExtended;

                    asmLine.branchTaken = true;

                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BLTU:
                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html#bltu
                // Branch Less Than Unsigned
                //
                // Format: bltu rs1, rs2, offset
                // Description: Take the branch if registers rs1 is less than
                // rs2, using unsigned comparison.
                // Implementation: if (x[rs1] <u x[rs2]) pc += sext(offset)
                //
                // Unsigned means that the register content are interpreted as
                // being two unsigned numbers!
                //
                // Imagine X_LEN is 4
                // 1. Erste Stelle anschauen: wenn Ziffer = 1: Zahl negativ, Ziffer = 0: Zahl
                // positiv.
                // 2. Zahl ist positiv: Umrechnung vom Binärsystem ins Dezimalsystem ist bereits
                // möglich;
                // 3. Zahl ist negativ: Man subtrahiert 1 und negiert die einzelnen Ziffern.
                // (Dieser Schritt lässt sich für den Menschen vereinfachen: Man negiert zuerst
                // die einzelnen Ziffern und addiert hinterher 1, was zum selben Ergebnis
                // führt.)
                // 4. Die entstandene, entsprechend positive Zahl im Binärsystem rechnet man ins
                // Dezimalsystem um.
                // 5. Wenn negativ, ein „−“ vor die Zahl setzen.
                //
                // https://www.exploringbinary.com/twos-complement-converter/
                //
                // 1. 0010
                // 3. 0010 = 2d
                //
                // 1. 1010
                // 3. 1010 - 1 -> 1001 -> 0110 = 6d
                // 5. 6d -> -6d
                //
                // 1. 1001
                // 3. 1001 - 1 -> 1000 -> 0111 = 7d
                // 5. 7d -> -7d
                //
                // Imagine X_LEN is 4.
                //
                // rs1 = 1001 which is signed: -7d and unsigned: 9d
                // rs2 = 0010 which is signed: 2d and unsigned: 2d
                //
                // blt 1001, 0010, <offset> --> blt -7d < 2d is TRUE, branch is taken
                // bltu 1001, 0010, <offset> --> bltu 9d < 2d is FALSE, branch is NOT taken
                //
                // main:
                // li t0, -3
                // lui t1, 2
                // bltu t0, t1, -8
                // blt t0, t1, -12
                //
                // Although both instructions, look at the same operands, bltu does not
                // perform the jump whereas blt performs the jump (creating an endless loop)
                //
                // Why?
                //
                // bltu interprets the operands as unsigned values.
                // -3 (0xFFFFFFFD) as unsigned is not interpreted as twos-complement and
                // becomes a very large positive number (4294967293)
                // 2 remains the value 2.
                // bltu 4294967293, 2, -8 performs the comparison 4294967293 < 2 which is false
                // and the jump is not taken
                //
                // blt interprets the operands as signed values
                // -3 remains -3, 2 remains 2
                // blt -3, 2, -12 performs the comparison -3 < 2 and the jump is taken

                // https://stackoverflow.com/questions/9578639/best-way-to-convert-a-signed-integer-to-an-unsigned-long
                register_0_value_l = readRegisterFile(asmLine.register_0.getIndex()) & 0x00000000ffffffffL;
                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                if (register_0_value_l < register_1_value_l) {
                    asmLine.branchTaken = true;
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BGEU:
                // https://stackoverflow.com/questions/9578639/best-way-to-convert-a-signed-integer-to-an-unsigned-long
                // register_0_value_l = readRegisterFile(asmLine.register_0.getIndex()) &
                // 0x00000000ffffffffL;
                // register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) &
                // 0x00000000ffffffffL;
                register_0_value_l = readRegisterFile(asmLine.register_0.getIndex());
                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                if (register_0_value_l >= register_1_value_l) {
                    asmLine.branchTaken = true;
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_LB:
                // logger.trace(asmLine.toString());

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                logger.trace("addr: " + addr);
                // read from memory (MEMORY STAGE)
                let = new byte[1];
                let[0] = (byte) memory.getByte(addr + 0);
                // let[1] = memory[addr + 1];
                // let[2] = memory[addr + 2];
                // let[3] = memory[addr + 3];
                // WRITE BACK STAGE
                // place read value into the destination register
                // value = ByteArrayUtil.fourByteToInt(let, ByteOrder.BIG_ENDIAN);
                // value = ByteArrayUtil.fourByteToInt(let, ByteOrder.LITTLE_ENDIAN);
                writeRegisterFile(asmLine.register_0.getIndex(), (int) let[0]);
                // DEBUG
                stringBuilder = new StringBuilder();
                stringBuilder.append("lb");
                stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                // stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                // stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                // stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);
                logger.trace(stringBuilder.toString());

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LH:
                // LH loads a 16-bit value from memory, then sign-extends to 32-bits before
                // storing in rd

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                // read from memory (MEMORY STAGE)
                let = new byte[2];
                let[0] = (byte) memory.getByte(addr + 0);
                let[1] = (byte) memory.getByte(addr + 1);

                // WB: I do not know why it works with BigEndian here although the ELF if little
                // endian!
                int lhVal = ByteArrayUtil.decodeInt16FromArrayBigEndian(let, 0);
                int lhValSignExtended = (int) NumberParseUtil.sign_extend_16_bit_to_int32_t(lhVal);

                // System.out.println(ByteArrayUtil.byteToHex(lhVal));

                // WRITE BACK STAGE
                // place read value into the destination register
                writeRegisterFile(asmLine.register_0.getIndex(), lhValSignExtended);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LHU:
                // LH loads a 16-bit value from memory, then sign-extends to 32-bits before
                // storing in rd

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                // read from memory (MEMORY STAGE)
                let = new byte[2];
                let[0] = (byte) memory.getByte(addr + 0);
                let[1] = (byte) memory.getByte(addr + 1);

                // WB: I do not know why it works with BigEndian here although the ELF if little
                // endian!
                int lhuVal = ByteArrayUtil.decodeInt16FromArrayBigEndian(let, 0);
                // int lhValSignExtended = (int)
                // NumberParseUtil.sign_extend_16_bit_to_int32_t(lhVal);

                // System.out.println(ByteArrayUtil.byteToHex(lhuVal));

                // WRITE BACK STAGE
                // place read value into the destination register
                writeRegisterFile(asmLine.register_0.getIndex(), lhuVal);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LW:
                // logger.trace(asmLine.toString());

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                logger.trace("addr: " + addr);

                // read from memory (MEMORY STAGE)
                let = new byte[4];
                let[0] = (byte) memory.getByte((long) (addr + 0));
                let[1] = (byte) memory.getByte((long) (addr + 1));
                let[2] = (byte) memory.getByte((long) (addr + 2));
                let[3] = (byte) memory.getByte((long) (addr + 3));

                // WRITE BACK STAGE
                // place read value into the destination register
                // value = ByteArrayUtil.fourByteToInt(let, ByteOrder.BIG_ENDIAN);
                value = ByteArrayUtil.fourByteToInt(let, ByteOrder.LITTLE_ENDIAN);
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                // DEBUG
                stringBuilder = new StringBuilder();
                stringBuilder.append("lw");
                stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);
                logger.trace(stringBuilder.toString());

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LBU:
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                // DEBUG
                logger.trace("addr: " + ByteArrayUtil.byteToHex(addr) + " " + addr);

                // 32 bit
                value = memory.getByte((int) addr);

                // 64 bit
                // value = memory.getByte((long)addr);

                writeRegisterFile(asmLine.register_0.getIndex(), value);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LBW:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_SB:
                logger.trace("GP: " + readRegisterFile(RISCVRegister.REG_GP.getIndex()));

                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                logger.trace("Address: " + ByteArrayUtil.byteToHex(addr));

                // retrieve the value to write into the address
                value = (int) readRegisterFile(asmLine.register_0.getIndex());
                logger.trace("Value: " + ByteArrayUtil.byteToHex(value));

                // memory.print(0x80002000, 0x80002020, ByteOrder.LITTLE_ENDIAN);
                memory.storeByte((long) (addr + 0), (byte) (value & 0xFF));
                // memory.print(0x80002000, 0x80002020, ByteOrder.LITTLE_ENDIAN);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SH:
                // Store 16-bit, values from the low bits of register rs2 to memory.
                // sh rs2, offset(rs1)
                // M[x[rs1] + sext(offset)] = x[rs2][15:0]
                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                // retrieve the value to write into the address
                value = (int) readRegisterFile(asmLine.register_0.getIndex());
                let = ByteArrayUtil.longToTwoByte(value, ByteOrder.LITTLE_ENDIAN);
                // write value into memory (MEMORY STAGE)
                memory.storeByte(addr + 0, let[0]);
                memory.storeByte(addr + 1, let[1]);
                // Increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SW:
                logger.trace("sw: " + asmLine);
                // Store 32-bit, values from the low bits of register rs2 to memory.
                // sw rs2, offset(rs1)
                // M[x[rs1] + sext(offset)] = x[rs2][31:0]
                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                // retrieve the value to write into the address
                value = (int) readRegisterFile(asmLine.register_0.getIndex());
                let = ByteArrayUtil.intToFourByte((int) value, ByteOrder.LITTLE_ENDIAN);

                // write value into memory (MEMORY STAGE)

                // 64bit
                memory.storeByte((long) (addr + 0), let[0]);
                memory.storeByte((long) (addr + 1), let[1]);
                memory.storeByte((long) (addr + 2), let[2]);
                memory.storeByte((long) (addr + 3), let[3]);

                // // 64 bit
                // memory.storeByte((long)(addr + 0), let[0]);
                // memory.storeByte((long)(addr + 1), let[1]);
                // memory.storeByte((long)(addr + 2), let[2]);
                // memory.storeByte((long)(addr + 3), let[3]);

                // DEBUG
                if (logger.isTraceEnabled()) {

                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sw");
                    stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                    stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                    stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                    stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);

                    logger.trace(stringBuilder.toString());
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLTI:
                logger.trace("slti: " + asmLine);
                // set less than immediate
                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html#slti
                //
                // Format: slti rd, rs1, imm
                //
                // Description: Place the constant value 1 (literally a 1)
                // in register rd if register rs1
                // is less than the signextended immediate when both are treated
                // as signed numbers, else 0 is written to rd.
                //
                // Implementation: x[rd] = x[rs1] <s sext(immediate)
                value = (int) readRegisterFile(asmLine.register_1.getIndex());
                immediate_value = (int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());
                writeRegisterFile(asmLine.register_0.getIndex(), (value < immediate_value) ? 1 : 0);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLTIU:
                // throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/sltiu.html

                // sltiu rd, rs1, imm

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                // register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) &
                // 0x00000000ffffffffL;
                long immediate_value_long = asmLine.numeric_2.intValue() & 0x00000000ffffffffL;

                if (register_1_value_l < immediate_value_long) {
                    writeRegisterFile(asmLine.register_0.getIndex(), 1);
                } else {
                    writeRegisterFile(asmLine.register_0.getIndex(), 0);
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_XORI:
                logger.trace("XORI");

                // xori rd,rs1,imm
                // x[rd] = x[rs1] ^ sext(immediate)

                value = (int) readRegisterFile(asmLine.register_1.getIndex())
                        ^ ((int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ORI:

                long par1 = readRegisterFile(asmLine.register_1.getIndex());
                long par2 = (NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));

                logger.trace(ByteArrayUtil.byteToHex((int) par1));
                logger.trace(ByteArrayUtil.byteToHex((int) par2));

                long oriResult = par1 | par2;

                logger.trace(ByteArrayUtil.byteToHex((int) oriResult));

                writeRegisterFile(asmLine.register_0.getIndex(), oriResult);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLLI:
                // SLLI (Shift Left Logical Immediate)
                // slli rd, rs1, imm # rd = rs1 << imm
                logger.trace("SLLI: " + asmLine);

                immediate_value = (int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                // value = (int) readRegisterFile(asmLine.register_1.getIndex()) <<
                // immediate_value;
                value_l = readRegisterFile(asmLine.register_1.getIndex()) << immediate_value;

                writeRegisterFile(asmLine.register_0.getIndex(), value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SRLI:
                logger.trace("I_SRLI: " + asmLine);

                immediate_value = NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                // immediate_value = Math.abs(immediate_value);

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                value_l = register_1_value_l >> immediate_value;
                writeRegisterFile(asmLine.register_0.getIndex(), (int) value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SRAI:
                // SRAI (Shift Right Arithmetic Immediate)
                // srai rd, rs1, shamt
                // example: srai a5, a5, 1
                // definition: x[rd] = x[rs1] >>s shamt
                //
                // The difference to the logical form SRLI (Shift Right Logical Immediate)
                // is that SRLI always shifts in zeroes from the left while SRAI shifts in
                // the sign-bit from the left to keep the sign of the number correct
                long registerValue = readRegisterFile(asmLine.register_1.getIndex());
                int signBit = registerValue >= 0 ? 0 : 1;
                for (int i = 0; i < asmLine.numeric_2; i++) {
                    registerValue >>= 1;
                    registerValue |= (signBit << 63);
                }
                writeRegisterFile(asmLine.register_0.getIndex(), registerValue);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SUB:
                logger.trace("sub: " + asmLine);
                // Subtracts the register rs2 from rs1 and stores the result
                // in rd. Arithmetic overflow is ignored and the result is
                // simply the low XLEN bits of the result.
                // sub rd,rs1,rs2
                // x[rd] = x[rs1] - x[rs2]

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());

                result_w = register_1_value_l - register_2_value_l;

                writeRegisterFile(asmLine.register_0.getIndex(), result_w);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLL:
                long shiftValue = readRegisterFile(asmLine.register_2.getIndex());

                value_l = readRegisterFile(asmLine.register_1.getIndex()) << shiftValue;
                writeRegisterFile(asmLine.register_0.getIndex(), value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLT:
                // Set if less than
                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html#slt
                //
                // Place the constant value 1 in register rd if register rs1 is less
                // than register rs2 when both are treated as signed numbers,
                // else 0 is written to rd.
                //
                // slt rd, rs1, rs2
                // x[rd] = x[rs1] <s x[rs2]
                if (readRegisterFile(asmLine.register_1.getIndex()) < readRegisterFile(asmLine.register_2.getIndex())) {
                    writeRegisterFile(asmLine.register_0.getIndex(), 1);
                } else {
                    writeRegisterFile(asmLine.register_0.getIndex(), 0);
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLTU:
                // https://msyksphinz-self.github.io/riscv-isadoc/#_sltu
                //
                // Place the value 1 in register rd if register rs1 is less than
                // register rs2 when both are treated as unsigned numbers, else 0 is written to
                // rd.
                //
                // sltu rd,rs1,rs2
                // x[rd] = x[rs1] <u x[rs2]

                // make both registers unsigned
                // register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) &
                // 0x00000000ffffffffL;
                // register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) &
                // 0x00000000ffffffffL;

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());

                // make both registers unsigned
                register_1_value_l = Math.abs(register_1_value_l);
                register_2_value_l = Math.abs(register_2_value_l);

                if (register_1_value_l < register_2_value_l) {
                    writeRegisterFile(asmLine.register_0.getIndex(), 1);
                } else {
                    writeRegisterFile(asmLine.register_0.getIndex(), 0);
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_XOR:
                // xor rd,rs1,rs2
                // x[rd] = x[rs1] ^ x[rs2]

                // value = (int) readRegisterFile(asmLine.register_1.getIndex())
                // ^ (int) readRegisterFile(asmLine.register_2.getIndex());

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());

                value_l = register_1_value_l ^ register_2_value_l;

                logger.trace(ByteArrayUtil.byteToHex(value_l));

                writeRegisterFile(asmLine.register_0.getIndex(), value_l);

                // increment pc
                pc += asmLine.encodedLength;
                break;

            case I_SRL:
                logger.trace("srl: " + asmLine);

                // Shift right logical (logical == shift in zero, arithmetic == shift in sign
                // bit)
                // format: srl rd, rs1, rs2

                // register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) &
                // 0x00000000ffffffffL;
                // register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) &
                // 0x00000000ffffffffL;

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());

                register_2_value_l = Math.abs(register_2_value_l);

                logger.info(ByteArrayUtil.byteToHex(register_1_value_l));
                logger.info(ByteArrayUtil.byteToHex(register_2_value_l));

                // Java(TM) operator >>> is the logical shift!
                // logical shift, shifts in zeroes from the left
                value_l = register_1_value_l >>> register_2_value_l;

                logger.info(ByteArrayUtil.byteToHex(value_l));

                writeRegisterFile(asmLine.register_0.getIndex(), value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SRA:
                logger.trace("sra: " + asmLine);

                // Shift right arithmetic
                // format: sra rd, rs1, rs2

                value = (int) readRegisterFile(asmLine.register_1.getIndex()) >> readRegisterFile(
                        asmLine.register_2.getIndex());
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_OR:
                logger.trace("or");
                // Performs bitwise OR on registers rs1 and rs2 and place the result in rd
                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // | registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        | readRegisterFile(asmLine.register_2.getIndex()));

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ECALL:
                // a7 describes the service that is called by the ecall
                int regA7Value = (int) readRegisterFile(RISCVRegister.REG_A7.getIndex());
                switch (regA7Value) {

                    // ???
                    case 0x50: // 80dec
                        logger.trace("putchar()");
                        register_1_value = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 0xD6: // (214dec)
                        register_1_value = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 0x40: // (64dec)
                        register_1_value = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 0:
                        logger.warn("Unknown ECALL 0!");
                        break;

                    case 92: // 92dec (pfnStreamWriteBufFunc)
                        logger.trace("ECALL 92 - puts()");
                        register_0_value_l = readRegisterFile(RISCVRegister.REG_A0.getIndex());

                        printStringFromAddress(register_0_value_l);

                        // register_1_value_l = readRegisterFile(RISCVRegister.REG_A1.getIndex());
                        // logger.trace("a1: " + register_1_value_l);

                        register_1_value_l = readRegisterFile(RISCVRegister.REG_T5.getIndex());
                        // logger.info("t5: " + register_1_value_l);
                        break;

                    case 93: // 93dec (0x5D) (exit)
                        // System.out.println("ECALL 93 - exit()");

                        // DEBUG print stack frame
                        printStackFrame();
                        System.out.println("");

                        // the unit tests https://github.com/riscv-software-src/riscv-tests
                        // write a value into a0 that describes success or failure.
                        // 0 = success, non-zero = failure
                        register_0_value_l = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        if (register_0_value_l == 0) {
                            logger.info("Unit Test Success!");
                        } else {
                            // The RISCV unit tests write the index of the unit test into GP but
                            // shift it left and add 1 for some wierd reason
                            long globalPointerValue = readRegisterFile(RISCVRegister.REG_GP.getIndex());
                            globalPointerValue >>= 1;
                            throw new RuntimeException("Unit Test " + globalPointerValue + " Failed!");
                        }
                        return false; // Abortolomeus (engl. Abortholomew)

                    case 0x5E: // 94dec (time)
                        // System.out.println("time()");

                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        // System.out.println("addressA0: " + ByteArrayUtil.byteToHex(addressA0));

                        int dataA0 = memory.readWord(addressA0, ByteOrder.LITTLE_ENDIAN);
                        // System.out.println("dataA0: " + ByteArrayUtil.byteToHex(dataA0));

                        Calendar calendar = Calendar.getInstance();
                        long timeInMillis = calendar.getTimeInMillis();

                        // System.out.println("millis: " + timeInMillis);

                        byte[] timeInMillisAsArray = ByteArrayUtil.longToBytes(timeInMillis);

                        memory.storeByte((addressA0 + 0), (byte) timeInMillisAsArray[7]);
                        memory.storeByte((addressA0 + 1), (byte) timeInMillisAsArray[6]);
                        memory.storeByte((addressA0 + 2), (byte) timeInMillisAsArray[5]);
                        memory.storeByte((addressA0 + 3), (byte) timeInMillisAsArray[4]);
                        memory.storeByte((addressA0 + 4), (byte) timeInMillisAsArray[3]);
                        memory.storeByte((addressA0 + 5), (byte) timeInMillisAsArray[2]);
                        memory.storeByte((addressA0 + 6), (byte) timeInMillisAsArray[1]);
                        memory.storeByte((addressA0 + 7), (byte) timeInMillisAsArray[0]);

                        // System.out.println("[6]: " + (byte) ((int) (timeInMillisAsArray[6]) & 0xFF));
                        // System.out.println("[7]: " + (byte) ((int) (timeInMillisAsArray[7]& 0xFF) ));
                        // memory.storeByte(addressA0 + 0, (byte) 0x78);
                        // memory.storeByte(addressA0 + 1, (byte) 0x56);
                        // memory.storeByte(addressA0 + 2, (byte) 0x34);
                        // memory.storeByte(addressA0 + 3, (byte) 0x12);
                        // memory.storeByte(addressA0 + 4, (byte) 0x78);
                        // memory.storeByte(addressA0 + 5, (byte) 0x56);
                        // memory.storeByte(addressA0 + 6, (byte) 0x34);
                        // memory.storeByte(addressA0 + 7, (byte) 0x12);

                        // writeRegisterFile(RISCVRegister.REG_A0.getIndex(), 18);
                        // writeRegisterFile(RISCVRegister.REG_A1.getIndex(), 18);
                        // writeRegisterFile(RISCVRegister.REG_A5.getIndex(), 18);
                        break;

                    case 0x5F: // 95dec (localtime)
                        // System.out.println("localtime()");

                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        // System.out.println("addressA0: " + ByteArrayUtil.byteToHex(addressA0));

                        byte[] timeInMillisAsArray2 = new byte[8];

                        // timeInMillisAsArray2[7] = (byte) (((int) memory.getByte(addressA0 + 0)) &
                        // 0xFF);
                        // System.out.println("[7]: " + (byte) timeInMillisAsArray2[7]);
                        // timeInMillisAsArray2[6] = (byte) (((int) memory.getByte(address_A0 + 1)) &
                        // 0xFF);
                        // System.out.println("[6]: " + (byte) (int) timeInMillisAsArray2[6]);

                        timeInMillisAsArray2[7] = (byte) memory.getByte((addressA0 + 0));
                        timeInMillisAsArray2[6] = (byte) memory.getByte((addressA0 + 1));
                        timeInMillisAsArray2[5] = (byte) memory.getByte((addressA0 + 2));
                        timeInMillisAsArray2[4] = (byte) memory.getByte((addressA0 + 3));
                        timeInMillisAsArray2[3] = (byte) memory.getByte((addressA0 + 4));
                        timeInMillisAsArray2[2] = (byte) memory.getByte((addressA0 + 5));
                        timeInMillisAsArray2[1] = (byte) memory.getByte((addressA0 + 6));
                        timeInMillisAsArray2[0] = (byte) memory.getByte((addressA0 + 7));

                        long timeInMillisUTC = ByteArrayUtil.bytesToLong(timeInMillisAsArray2);
                        // System.out.println("millis: " + timeInMillisUTC);

                        // struct tm
                        // {
                        // int tm_sec; // seconds after the minute - [0, 60] including leap second
                        // int tm_min; // minutes after the hour - [0, 59]
                        // int tm_hour; // hours since midnight - [0, 23]
                        // int tm_mday; // day of the month - [1, 31]
                        // int tm_mon; // months since January - [0, 11]
                        // int tm_year; // years since 1900
                        // int tm_wday; // days since Sunday - [0, 6]
                        // int tm_yday; // days since January 1 - [0, 365]
                        // int tm_isdst; // daylight savings time flag
                        // };

                        // divide the total milliseconds by 3,600,000 to get the total hours, then find
                        // the remainder
                        int hours = (int) (timeInMillisUTC / 3600000L);
                        hours = hours % 24;
                        // System.out.println("hh: " + hours);

                        int mins = (int) (timeInMillisUTC / 60000L);
                        mins = mins % 60;
                        // System.out.println("mm: " + mins);

                        int seconds = (int) (timeInMillisUTC / 1000L);
                        seconds = seconds % 60;
                        // System.out.println("ss: " + seconds);

                        // static memory for the structure to which the localtime() function
                        // returns a pointer
                        int resultAddress = 0x50000;
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), resultAddress);

                        // tm_sec
                        byte[] temp = ByteArrayUtil.intToFourByte(seconds, ByteOrder.LITTLE_ENDIAN);
                        memory.storeByte((long) (resultAddress + 0), (byte) temp[0]);
                        memory.storeByte((long) (resultAddress + 1), (byte) temp[1]);
                        memory.storeByte((long) (resultAddress + 2), (byte) temp[2]);
                        memory.storeByte((long) (resultAddress + 3), (byte) temp[3]);

                        // tm_min
                        temp = ByteArrayUtil.intToFourByte(mins, ByteOrder.LITTLE_ENDIAN);
                        memory.storeByte((long) (resultAddress + 4), (byte) temp[0]);
                        memory.storeByte((long) (resultAddress + 5), (byte) temp[1]);
                        memory.storeByte((long) (resultAddress + 6), (byte) temp[2]);
                        memory.storeByte((long) (resultAddress + 7), (byte) temp[3]);

                        // tm_sec
                        temp = ByteArrayUtil.intToFourByte(hours, ByteOrder.LITTLE_ENDIAN);
                        memory.storeByte((long) (resultAddress + 8), (byte) temp[0]);
                        memory.storeByte((long) (resultAddress + 9), (byte) temp[1]);
                        memory.storeByte((long) (resultAddress + 10), (byte) temp[2]);
                        memory.storeByte((long) (resultAddress + 11), (byte) temp[3]);

                        // System.out.println("done");
                        break;

                    case 0x60: // 96dec (test_func)
                        // System.out.println("test_func()");
                        // https://msyksphinz-self.github.io/riscv-isadoc/#_ecall
                        // writeRegisterFile(RISCVRegister.REG_A0.getIndex(), 15);
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), 14);
                        break;

                    case 97: // 97dec (fseek)
                        // System.out.println("fseek()");
                        fileHandle = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        int newSeekPosition = (int) readRegisterFile(RISCVRegister.REG_A1.getIndex());
                        // System.out.println("fseek() newSeekPosition = " + newSeekPosition);
                        int seekMode = (int) readRegisterFile(RISCVRegister.REG_A2.getIndex());
                        // System.out.println("fseek() seekMode = " + seekMode);

                        // origin Beschreibung
                        // SEEK_SET (0) Der Start des Streams markiert den Ursprung.
                        // SEEK_CUR Aktuelle Position im Stream setzt den Ursprung.
                        // SEEK_END Das Ende des Streams wird als Ursprung gewählt.

                        int fseekResult = fileHandling.fseek(fileHandle, newSeekPosition, seekMode);
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), fseekResult);
                        break;

                    case 98: // 98dec (getc)
                        // System.out.println("getc()");
                        fileHandle = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        // System.out.println("getc() addressA0=" + fileHandle);
                        int resultChar = fileHandling.getc(fileHandle);
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), resultChar);
                        break;

                    case 99: // 99dec (fopen)
                        logger.trace("fopen()");

                        // path to file as string is stored via the address in addressA0
                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        String filepath = toStringFromAddress(addressA0);

                        // DEBUG
                        // logger.trace("fopen() filepath=\"" + filepath + "\"");

                        // file mode as string is stored via the address in addressA1
                        addressA1 = readRegisterFile(RISCVRegister.REG_A1.getIndex());
                        String filemode = toStringFromAddress(addressA1);

                        // DEBUG
                        // logger.trace("fopen() filemode=\"" + filemode + "\"");

                        fileHandle = fileHandling.fopen(filepath, filemode);

                        // return a FILE handle
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), fileHandle);
                        break;

                    case 100: // 100dec (ftell)
                        logger.trace("ftell()");

                        // first parameter is the fileHandle
                        fileHandle = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        int filePos = fileHandling.ftell(fileHandle);

                        // return a FILE position
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), filePos);
                        break;

                    case 101: // 101dec (putchar)
                        logger.trace("putchar()");
                        register_1_value = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 102: // 102dec (fclose)
                        logger.trace("fclose()");

                        // first parameter is the fileHandle
                        fileHandle = (int) readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        fileHandling.fclose(fileHandle);

                        // return 0 on success, EOF on error
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), 0);
                        break;

                    case 103: // 103dec (fgets)
                        logger.trace("fgets()");

                        // first parameter is the byte buffer to fill
                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());

                        // second parameter is the max allowed length defined by the caller
                        addressA1 = readRegisterFile(RISCVRegister.REG_A1.getIndex());

                        // third parameter is the file handle to read from (can also be stdin)
                        addressA2 = readRegisterFile(RISCVRegister.REG_A2.getIndex());

                        if (addressA2 == FileHandling.STDIN) {

                            String inputValue = getInput();
                            int inputValueLength = inputValue.length();

                            inputValue = inputValue.substring(0, (int) Math.min(addressA1, inputValueLength));

                            logger.trace("inputValue: \"" + inputValue + "\"");

                            for (int i = 0; i < inputValue.length(); i++) {
                                char inputValueAsChar = inputValue.charAt(i);
                                memory.storeByte((long) (addressA0 + i), (byte) inputValueAsChar);

                                if (inputValueAsChar == 0) {
                                    break;
                                }
                            }

                        } else {
                            throw new RuntimeException("not implemented yet!");
                        }

                        // return parameter 0
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), addressA0);
                        break;

                    // rand()
                    case 104:
                        int min = 0;
                        int max = 32767;
                        int randomValue = random.nextInt(max + 1 - min) + min;
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), randomValue);
                        break;

                    default:
                        singleStepping = true;
                        printMemoryAroundPC(10);
                        throw new RuntimeException("ECALL " + ByteArrayUtil.byteToHex(regA7Value) + " (" + regA7Value
                                + ") NOT IMPLEMENTED!");
                }
                pc += asmLine.encodedLength;
                break;

            case I_FENCE:
                // TODO: implement FENCE!

                // long gp = readRegisterFile(RISCVRegister.REG_GP.getIndex());
                // logger.info("Unit Test " + gp + " failed!");
                pc += asmLine.encodedLength;
                break;

            // case I_FENCE_I:
            // break;

            case I_EBREAK:
                logger.info("I_EBREAK");

                // singleStepping = true;
                // debugASMLineOutput = true;

                printMemoryAroundPC(5);

                pc += asmLine.encodedLength;
                break;

            // // the NOP pseudo instruction is replaced by addi zero, zero, 0
            // // NOP will never make it into the processor since there is no
            // // encoding for NOP!
            // case I_NOP:
            // logger.trace("mnemonic: NOP");

            // printStackFrame();

            // pc += 4;
            // break;

            case I_BRK:
                // logger.trace("mnemonic: BRK");
                pc += asmLine.encodedLength;
                break;

            // case I_PUTS:
            // logger.info("mnemonic: PUTS");

            // // the start address of the zero terminated string is expected in A0
            // long startAddress = readRegisterFile(RISCVRegister.REG_A0.getIndex());
            // logger.info("startAddress: " + startAddress);

            // stringBuilder = new StringBuilder();
            // while (true) {
            // int tempByte = memory.getByte((int) startAddress);
            // if (tempByte == 0x00) {
            // break;
            // } else {
            // stringBuilder.append((char) tempByte);
            // }

            // startAddress++;
            // }

            // logger.trace(stringBuilder.toString());

            // pc += 4;
            // break;

            //
            // Instruction Set Priv
            //

            case I_MRET:
                logger.warn("I_MRET Not implemented yet!");
                // increment pc
                pc += asmLine.encodedLength;
                break;

            //
            // RV63 I - 64 bit
            //

            case I_ADDIW:
                int first_register_value_w = (int) (readRegisterFile(asmLine.register_1.getIndex()) & 0xFFFFFFFFL);
                int immediate_value_w = asmLine.numeric_2.intValue();

                // logger.trace("1st Register Name: " + asmLine.register_1.toStringAbi());
                // logger.trace("1st Register Value: " + first_register_value);
                // logger.trace("2nd Immediate Value: " + immediate_value);
                // logger.trace("dest Register Name: " + asmLine.register_0.toStringAbi());

                // Java(TM) automatically performs sign extend during conversion to long!
                result_w = first_register_value_w + immediate_value_w;
                // result_w = signExtend32BitTo64Bit(result);

                writeRegisterFile(asmLine.register_0.getIndex(), result_w);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ADDW:
                register_1_value = (int) (readRegisterFile(asmLine.register_1.getIndex()) & 0xFFFFFFFFL);
                register_2_value = (int) (readRegisterFile(asmLine.register_2.getIndex()) & 0xFFFFFFFFL);
                // Java(TM) automatically performs sign extend during conversion to long!
                result_w = register_1_value + register_2_value;
                writeRegisterFile(asmLine.register_0.getIndex(), result_w);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LD:

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                logger.info("addr: " + ByteArrayUtil.byteToHex(addr));

                // read from memory (MEMORY STAGE)
                let = new byte[8];
                let[0] = (byte) memory.getByte((long) addr + 0);
                let[1] = (byte) memory.getByte((long) addr + 1);
                let[2] = (byte) memory.getByte((long) addr + 2);
                let[3] = (byte) memory.getByte((long) addr + 3);
                let[4] = (byte) memory.getByte((long) addr + 4);
                let[5] = (byte) memory.getByte((long) addr + 5);
                let[6] = (byte) memory.getByte((long) addr + 6);
                let[7] = (byte) memory.getByte((long) addr + 7);

                // WRITE BACK STAGE
                // place read value into the destination register
                value_l = ByteArrayUtil.eightByteToLong(let, ByteOrder.LITTLE_ENDIAN);
                writeRegisterFile(asmLine.register_0.getIndex(), value_l);

                // DEBUG
                stringBuilder = new StringBuilder();
                stringBuilder.append("lw");
                stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);
                stringBuilder.append(", mem: " + (addr + 4) + " = " + let[4]);
                stringBuilder.append(", mem: " + (addr + 5) + " = " + let[5]);
                stringBuilder.append(", mem: " + (addr + 6) + " = " + let[6]);
                stringBuilder.append(", mem: " + (addr + 7) + " = " + let[7]);
                logger.info(stringBuilder.toString());

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SD:

                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                logger.info("Storing to Adresse: " + ByteArrayUtil.byteToHex(addr));

                // retrieve the value to write into the address
                value_l = readRegisterFile(asmLine.register_0.getIndex());
                let = ByteArrayUtil.longToEightByte(value_l, ByteOrder.LITTLE_ENDIAN);
                // write value into memory (MEMORY STAGE)
                memory.storeByte((long) addr + 0, let[0]);
                memory.storeByte((long) addr + 1, let[1]);
                memory.storeByte((long) addr + 2, let[2]);
                memory.storeByte((long) addr + 3, let[3]);
                memory.storeByte((long) addr + 4, let[4]);
                memory.storeByte((long) addr + 5, let[5]);
                memory.storeByte((long) addr + 6, let[6]);
                memory.storeByte((long) addr + 7, let[7]);

                // // DEBUG
                // stringBuilder = new StringBuilder();
                // stringBuilder.append("sw");
                // stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                // stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                // stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                // stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);
                // logger.trace(stringBuilder.toString());

                // increment PC
                pc += asmLine.encodedLength;
                break;

            //
            // Zifencei Extension
            //

            case I_FENCEI:
                // TODO

                // increment PC
                pc += asmLine.encodedLength;
                break;

            //
            // Ziscr Extension
            //

            // break;
            // case I_CSRRW:
            // break;
            // case I_CSRRC:
            // break;
            // case I_CSRRWI:
            // break;
            // case I_CSRRSI:
            // break;
            // case I_CSRRCI:
            // break;

            /**
             * 1. Read the value from the CRS register csr and store it into rd
             * 2. Modify the csr value by performing a bit-wise OR between csr and rd
             * 3. Write back the result from step 2 into the CSR register csr
             */
            case I_CSRRS:
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/csrrs.html

                logger.trace("mnemonic: I_CSRRS");

                // System.out.println(asmLine.numeric_1);

                // 1. Read the value from the CRS register csr and store it into rd

                csrId = asmLine.numeric_1.intValue();
                csrValue = readCSRById(csrId);

                // 1. Read the value from the CRS register csr and store it into rd
                writeRegisterFile(asmLine.register_0.getIndex(), csrValue);

                // 2. Modify the csr value by performing a bit-wise OR between csr and rd
                long newCsrValue = csrValue | readRegisterFile(asmLine.register_2.getIndex());

                // 3. Write back the result from step 2 into the CSR register csr
                // writeRegisterFile(asmLine.register_1.getIndex(), newCsrValue);
                heartIdCSRRegister = newCsrValue;

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_CSRRW:
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/csrrw.html

                // 0. If xd=x0, then the instruction shall not read the CSR and shall not cause
                // any of the side effects that might occur on a CSR read.
                if (asmLine.register_0 == RISCVRegister.REG_X0) {
                    pc += asmLine.encodedLength;
                    break;
                }

                // 1. Read the old value of the CSR, zero-extends the value to XLEN bits
                csrId = asmLine.numeric_1.intValue();
                csrValue = readCSRById(csrId);

                // write csrValue to integer register xd
                writeRegisterFile(asmLine.register_0.getIndex(), csrValue);

                // The initial value in xs1 is written to the CSR.
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());
                writeCSRById(asmLine.numeric_1.intValue(), (int) register_2_value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_CSRRWI:
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/csrrwi.html

                // 0. If xd=x0, then the instruction shall not read the CSR and shall not cause
                // any of the side effects that might occur on a CSR read.
                if (asmLine.register_0 == RISCVRegister.REG_X0) {
                    pc += asmLine.encodedLength;
                    break;
                }

                // 1. Read the old value of the CSR, zero-extends the value to XLEN bits
                csrId = asmLine.numeric_1.intValue();
                csrValue = readCSRById(csrId);

                // write csrValue to integer register xd
                writeRegisterFile(asmLine.register_0.getIndex(), csrValue);

                // The initial value in xs1 is written to the CSR.
                register_2_value_l = asmLine.numeric_2;
                writeCSRById(asmLine.numeric_1.intValue(), (int) register_2_value_l);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            //
            // Extension M -- multiplication extension
            //

            case I_MUL:
                logger.trace("mul");
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        * readRegisterFile(asmLine.register_2.getIndex()));

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_MULH:
                logger.trace("mulh: " + asmLine);

                // MUL performs an XLEN-bit×XLEN-bit multiplication of rs1 by rs2 and places the
                // lower XLEN bits in the destination register. MULH, MULHU, and MULHSU perform
                // the same multiplication but return the upper XLEN bits of the full 2×XLEN-bit
                // product, for signed×signed, unsigned×unsigned, and rs1×unsigned rs2
                // multiplication.

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());

                long mulhResult = register_1_value_l * register_2_value_l;
                int mulhResultShifted = (int) ((mulhResult >> 32) & 0xFFFFFFFF);

                writeRegisterFile(asmLine.register_0.getIndex(), mulhResultShifted);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_DIV:
                logger.trace("div");

                register_1_value = (int) readRegisterFile(asmLine.register_1.getIndex());
                register_2_value = (int) readRegisterFile(asmLine.register_2.getIndex());

                long divResult = 0;
                if (register_2_value == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    divResult = 0xFFFFFFFF;
                } else {
                    divResult = register_1_value / register_2_value;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), divResult);

                pc += asmLine.encodedLength;
                break;

            case I_DIVU:
                logger.trace("divu");

                // register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                // register_2_value = readRegisterFile(asmLine.register_2.getIndex());
                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) & 0x00000000ffffffffL;

                // int divuResult = 0;
                long divuResult = 0L;
                // if (register_2_value == 0) {
                if (register_2_value_l == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    divuResult = 0xFFFFFFFF;
                } else {
                    divuResult = register_1_value_l / register_2_value_l;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), (int) divuResult);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_REM:
                register_1_value = (int) readRegisterFile(asmLine.register_1.getIndex());
                register_2_value = (int) readRegisterFile(asmLine.register_2.getIndex());

                long remResult = 0;
                if (register_2_value == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    remResult = 0xFFFFFFFF;
                } else {
                    remResult = register_1_value % register_2_value;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), remResult);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_REMU:
                // Unsigned remainder -
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/remu.html
                // Calculate the remainder of unsigned division of xs1 by xs2, and store the
                // result in xd.
                logger.trace("mnemonic: REMU");

                register_1_value = (int) readRegisterFile(asmLine.register_1.getIndex());
                register_2_value = (int) readRegisterFile(asmLine.register_2.getIndex());

                long remuResult = 0;
                if (register_2_value == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    remuResult = 0xFFFFFFFF;
                } else {
                    remuResult = register_1_value % register_2_value;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), remuResult);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            //
            // F-Extension (Floating Point Extension)
            //

            case I_FSW:
                logger.info("I_FSW: " + asmLine);
                break;

            //
            // V-Extension (RVV Vector Extension)
            //

            // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
            case I_VSETVLI:
                logger.info("I_VSETVLI: " + asmLine);

                // application vector length is specified in a register
                avl = readRegisterFile(asmLine.register_1.getIndex());

                updateVectorEngineConfiguration(asmLine, avl);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSETIVLI:

                // vsetivli rd, uimm, vtypei
                // vsetivli zero,4, e32,m1,ta,ma

                logger.info("I_VSETIVLI: " + asmLine);

                // application vector length is specified as an immediate
                avl = asmLine.numeric_1;

                updateVectorEngineConfiguration(asmLine, avl);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VLE8_V:
                logger.warn("I_VLE8_V not implemented! " + asmLine);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VLE16_V:
                logger.warn("I_VLE16_V not implemented! " + asmLine);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VLE32_V:
                logger.warn("I_VLE32_V not implemented! " + asmLine);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VLE64_V:
                logger.info("I_VLE64_V: " + asmLine);

                // Register 0 is a RVV registers
                // register_0_value_l = readRegisterFile(asmLine.register_0.getIndex());

                switch (asmLine.register_0.getIndex()) {
                    case 0:
                        rvvReg = v0;
                        break;
                    case 1:
                        rvvReg = v1;
                        break;
                    case 2:
                        rvvReg = v2;
                        break;
                    default:
                        throw new RuntimeException("unknown RVV register: " + asmLine.register_0.getIndex());
                }

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());

                // AVL = application vector length
                for (int i = 0; i < avl; i++) {

                    long memoryValue = memory.readLong(register_1_value_l, ByteOrder.LITTLE_ENDIAN);

                    // DEBUG
                    logger.info("" + ByteArrayUtil.byteToHex(memoryValue));

                    memory.readLong(rvvReg, (i * sew) / 8, register_1_value_l, ByteOrder.LITTLE_ENDIAN);

                    // System.arraycopy(let, immValSignExtended, stringBuilder, result, csrId);

                    register_1_value_l += (sew / 8);
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSE8_V:
                logger.warn("I_VSE8_V not implemented! " + asmLine);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSE16_V:
                logger.warn("I_VSE16_V not implemented! " + asmLine);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSE32_V:
                logger.warn("I_VSE32_V not implemented! " + asmLine);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSE64_V:
                logger.info("I_VSE64_V: " + asmLine);

                register_0_value_l = readRegisterFile(asmLine.register_1.getIndex());

                switch (asmLine.register_0.getIndex()) {
                    case 0:
                        rvvReg = v0;
                        break;
                    case 1:
                        rvvReg = v1;
                        break;
                    case 2:
                        rvvReg = v2;
                        break;
                    default:
                        throw new RuntimeException("unknown RVV register: " + asmLine.register_1.getIndex());
                }

                for (int i = 0; i < avl; i++) {

                    // long memoryValue =
                    // logger.info("" + ByteArrayUtil.byteToHex(memoryValue));

                    // memory.readLong(rvvReg, (i*sew) / 8, register_0_value_l,
                    // ByteOrder.LITTLE_ENDIAN);

                    // System.arraycopy(let, immValSignExtended, stringBuilder, result, csrId);

                    long val = ByteArrayUtil.eightByteToLong(rvvReg, (i * sew) / 8, ByteOrder.LITTLE_ENDIAN);

                    memory.storeLong(register_0_value_l, val);

                    register_0_value_l += (sew / 8);
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vmsne
            case I_VMSNE_VI:
                logger.warn("I_VMSNE_VI not implemented! " + asmLine);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VADD_VV:
                logger.info("I_VADD_VV: " + asmLine);

                byte[] rvvRegisterRD = getRVVRegister(asmLine.register_0.getIndex());
                byte[] rvvRegisterRS0 = getRVVRegister(asmLine.register_1.getIndex());
                byte[] rvvRegisterRS1 = getRVVRegister(asmLine.register_1.getIndex());

                for (int i = 0; i < avl; i++) {

                    long a = ByteArrayUtil.eightByteToLong(rvvRegisterRS0, (i * sew) / 8, ByteOrder.LITTLE_ENDIAN);
                    long b = ByteArrayUtil.eightByteToLong(rvvRegisterRS1, (i * sew) / 8, ByteOrder.LITTLE_ENDIAN);

                    long c = a + b;

                    ByteArrayUtil.longToEightByte(rvvRegisterRD, (i * sew) / 8, c, ByteOrder.LITTLE_ENDIAN);
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VADD_VX:
                logger.warn("I_VADD_VX not implemented! " + asmLine);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VADD_VI:
                logger.warn("I_VADD_VI not implemented! " + asmLine);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VMV_V_I:
                logger.warn("I_VMV_V_I not implemented! " + asmLine);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VID:
                logger.warn("I_VID not implemented! " + asmLine);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSLL_VV:
                logger.warn("I_VSLL_VV not implemented! " + asmLine);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSLL_VX:
                logger.warn("I_VSLL_VX not implemented! " + asmLine);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_VSLL_VI:
                logger.warn("I_VSLL_VI not implemented! " + asmLine);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            default:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic + " machine code: "
                        + ByteArrayUtil.byteToHex(asmLine.instruction));
        }

        return true;
    }

    private void updateVectorEngineConfiguration(AsmLine<?> asmLine, final long avl) {
        // LMUL, group size - see
        // https://github.com/riscvarchive/riscv-v-spec/blob/master/v-spec.adoc#41-mapping-for-lmul--1
        switch (asmLine.rvvLmul) {
            case "mf8":
                // System.out.println("mf8");
                lMultiplier = 1 / 8;
                break;
            case "mf4":
                // System.out.println("mf4");
                lMultiplier = 1 / 4;
                break;
            case "mf2":
                // System.out.println("mf2");
                lMultiplier = 1 / 2;
                break;
            case "m1":
                // System.out.println("m1");
                lMultiplier = 1;
                break;
            case "m2":
                // System.out.println("m2");
                lMultiplier = 2;
                break;
            case "m4":
                // System.out.println("m4");
                lMultiplier = 4;
                break;
            case "m8":
                // System.out.println("m8");
                lMultiplier = 8;
                break;
        }

        // selected element width
        switch (asmLine.rvvSew) {

            case "e8":
                // System.out.println("e8");
                sew = 8;
                break;

            case "e16":
                // System.out.println("e16");
                sew = 16;
                break;

            case "e32":
                // System.out.println("e32");
                sew = 32;
                break;

            case "e64":
                // System.out.println("e64");
                sew = 64;
                break;

            default:
                throw new RuntimeException("unknown SEW");

        }

        // compute how many vector registers need to be combined to serve the user
        // request
        long registerCount = (VLEN * lMultiplier) * sew;
        vl = (VLEN * lMultiplier) / sew;

        System.out.println("VLEN = " + VLEN);
        System.out.println("LMUL = " + lMultiplier);
        System.out.println("SEW = " + sew);
        System.out.println("Registers Grouped = " + registerCount + " = VLEN * LMUL / SEW");
        System.out.println("vl = " + vl + " = VLEN * LMUL * SEW");

        // need to stay below half the registers to perform an operation between two
        // vectors batches where each vector batch requires half the total availble
        // registers in the worst case
        if (registerCount > (32 / 2)) {
            throw new RuntimeException("Not enought registers!");
        }

        // TODO return the vl into first register
        // asf
    }

    private byte[] getRVVRegister(int index) {

        switch (index) {
            case 0:
                return v0;
            case 1:
                return v1;
            case 2:
                return v2;
            default:
                throw new RuntimeException("Unknown RVV register " + index);
        }
    }

    private long signExtend32BitTo64Bit(long result_w) {
        logger.info(ByteArrayUtil.byteToHex(result_w));
        result_w = NumberParseUtil.sign_extend_32_bit_to_int64_t(result_w);
        logger.info(ByteArrayUtil.byteToHex(result_w));
        return result_w;
    }

    private void writeCSRById(int index, int register_2_value_l) {
        logger.warn("Has to be implemented!");
    }

    private long readCSRById(int csrId) {

        long csrValue = 0;

        // https://www.five-embeddev.com/riscv-user-isa-manual/latest-adoc/zicsr.html
        switch (csrId) {

            case 0x180:
                break;

            case 0x300:
                break;

            case 0x302:
                break;

            case 0x303:
                break;

            case 0x304:
                break;

            // mtvec 0x305 == 773dec
            case 0x305:
                break;

            case 0x341:
                break;

            case 0x3A0:
                break;

            case 0x3B0:
                break;

            // Number Privilege Name Description
            // 0xF11 MRO mvendorid Vendor ID.
            case 0xF11:
                break;

            // 0xF12 MRO marchid Architecture ID.
            case 0xF12:
                break;

            // 0xF13 MRO mimpid Implementation ID.
            case 0xF13:
                break;

            // 0xF14 MRO mhartid Hardware thread ID.
            case 0xF14:
                // System.out.println(asmLine.numeric_1);
                csrValue = heartIdCSRRegister;
                break;

            // 0xF15 MRO mconfigptr Pointer to configuration data structure
            case 0xF15:
                break;

            case 0x744:
                break;

            case RVV_CSR_VLENB:
                csrValue = VLEN;
                break;

            default:
                // throw new RuntimeException("Unknown CSR with id: " +
                // ByteArrayUtil.byteToHex(csrId));
                logger.error("Unknown CSR with id: " + ByteArrayUtil.byteToHex(csrId));

        }
        return csrValue;
    }

    private void printMemoryAroundPC(int displayDistance) {

        logger.info("---------------------------------------------------------------------");

        // do not access negative memory address when printing close to address 0x00 of
        // RAM
        long startAddress = Math.max(0x00, pc - displayDistance * 4);
        long endAddress = pc + displayDistance * 4;

        memory.setDecoder(decoder);
        memory.print(startAddress, endAddress, ByteOrder.LITTLE_ENDIAN, pc);
    }

    private void printStackFrame() {

        long startAddress = readRegisterFile(RISCVRegister.REG_SP.getIndex());

        rawPrintingDecoder.memory = memory;
        memory.setDecoder(rawPrintingDecoder);

        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        memory.print(startAddress - 0, startAddress + 368, ByteOrder.LITTLE_ENDIAN, pc);
        System.out.println("/\\/\\\\/\\\\/\\\\/\\\\/\\\\/\\\\/\\\\/\\\\/\\\\/\\\\");
    }

    /**
     * prints a zero terminated string starting at the address
     *
     * @param startAddress read a zero terminated string starting at this address
     */
    private void printStringFromAddress(long startAddress) {
        int c = 0xFF;
        do {
            c = memory.getByte(startAddress++);
            System.out.print((char) c);
        } while (c != '\0');
    }

    /**
     * prints a zero terminated string starting at the address
     *
     * @param startAddress read a zero terminated string starting at this address
     */
    private String toStringFromAddress(long startAddress) {
        StringBuilder stringBuilder = new StringBuilder();
        int c = 'a';
        do {
            c = memory.getByte(startAddress++);
            if ((c != '\0') && (c != 0x0A)) {
                stringBuilder.append((char) c);
            }
        } while ((c != '\0') && (c != 0x0A));
        return stringBuilder.toString();
    }

    private static String getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // return reader.lines().collect(Collectors.joining("\n"));
        return reader.readLine();
    }

    @Override
    public long[] getRegisterFile() {
        long[] result = new long[32];
        for (int i = 0; i < 31; i++) {
            result[i] = (long) registerFile[i];
        }
        return result;
    }

    public void shutdown() throws IOException {
        if (traceBufferedWriter != null) {
            traceBufferedWriter.flush();
            traceBufferedWriter.close();
        }
    }

}
