package com.mycompany.decoder;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.memory.Memory;

/**
 * Decodes https://github.com/riscv/riscv-opcodes/blob/master/extensions/rv_m
 */
public class MDecoder implements Decoder {

    private static final Logger logger = LoggerFactory.getLogger(MDecoder.class);

    private static final int M_TYPE = 0b0110011;

    public Memory memory;

    @Override
    public List<AsmLine<?>> decode(long address) {

        logger.trace("PC: " + ByteArrayUtil.byteToHex(address));

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

        // for 32 bit cast to int
        final int instruction = memory.readWord((int) address, byteOrder);

        if ((instruction == 0x00000000) || (instruction == 0xFFFFFFFF)) {
            logger.trace("instruction is 0x00 or 0xFF. Aborting CPU run!");
            return null;
        }

        // DEBUG
        logger.trace("instruction: " + ByteArrayUtil.byteToHex(instruction, null, "%1$08X"));

        List<AsmLine<?>> result = new ArrayList<>();

        int funct7 = (instruction >> 25) & 0b1111111;
        if (funct7 != 1) {
            return null;
        }
        int opcode = (instruction >> 0) & 0b1111111;
        if (opcode != M_TYPE) {
            return null;
        }

        int funct3 = (instruction >> 12) & 0b111;

        int rd = (instruction >> 7) & 0b11111;
        int rs1 = (instruction >> 15) & 0b11111;
        int rs2 = (instruction >> 20) & 0b11111;

        AsmLine<Register> asmLine = new AsmLine<>();
        asmLine.instruction = instruction;
        result.add(asmLine);

        switch (funct3) {

            case 0b000:
                asmLine.mnemonic = Mnemonic.I_MUL;
                break;

            case 0b001:
                asmLine.mnemonic = Mnemonic.I_MULH;
                break;

            case 0b010:
                asmLine.mnemonic = Mnemonic.I_MULHSU;
                break;

            case 0b011:
                asmLine.mnemonic = Mnemonic.I_MULHU;
                break;

            case 0b100:
                asmLine.mnemonic = Mnemonic.I_DIV;
                break;

            case 0b101:
                asmLine.mnemonic = Mnemonic.I_DIVU;
                break;

            case 0b110:
                asmLine.mnemonic = Mnemonic.I_REM;
                break;

            case 0b111:
                asmLine.mnemonic = Mnemonic.I_REMU;
                break;

            default:
                return null;
        }

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);
        asmLine.register_2 = RISCVRegister.fromInt(rs2);

        asmLine.encodedLength = 4;

        return result;
    }

}
