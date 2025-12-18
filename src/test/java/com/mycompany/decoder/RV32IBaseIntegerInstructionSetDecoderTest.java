package com.mycompany.decoder;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;

public class RV32IBaseIntegerInstructionSetDecoderTest {

    // public static final int XLEN = 64;

    /**
     * Encoding:
     * 31-29 | 28 | 27-26 | 25 | 24-20 | 19-15 | 14-12 | 11-7 | 6-0
     * 000   |  0 |    00 | vm | 00000 |   rs1 | width |   vd | 0000111
     *
     * vle8.v   vd, (rs1), vm # 8-bit unit-stride load with width == '000'
     * vle16.v   vd, (rs1), vm # 16-bit unit-stride load with width == '101'
     * vle32.v   vd, (rs1), vm # 32-bit unit-stride load with width == '110'
     * vle64.v   vd, (rs1), vm # 64-bit unit-stride load with width == '111'

     * Example: https://github.com/vproc/vicuna/blob/main/test/alu/vadd_8.S
     *
     */
    @Test
    public void testDecodeVle8v() {

        // ARRANGE

        // vle8.v   vd, (rs1), vm
        // vle8.v   v0, 0(a0), 1
        int vm = 0b1; // vmasking
        int rs1 = RISCVRegister.REG_A0.getIndex();
        int width = 0b000;
        int vd = RISCVRegister.REG_V0.getIndex();
        int opcode = 0b0000111;

        int instruction = (0b000 << 29) | (0b0 << 28) | (0b00 << 26) | (vm << 25) | (0b00000 << 20) | (rs1 << 15) | (width << 12) | (vd << 7) | (opcode << 0);

        int XLEN = 32;
        RV32IBaseIntegerInstructionSetDecoder decoder = new RV32IBaseIntegerInstructionSetDecoder(XLEN);

        // ACT

        AsmLine<Register> asmLine = new AsmLine<>();
        decoder.processUncompressedInstruction(asmLine, instruction);

        // ASSERT

        assertEquals(asmLine.instruction, instruction);
        assertEquals(asmLine.machineCode, instruction);
        assertEquals(asmLine.mnemonic, Mnemonic.I_VLE8_V);
        assertEquals(asmLine.rvvMasking, vm == 1 ? true : false);
        assertEquals(asmLine.register_0, RISCVRegister.REG_V0);
        assertEquals(asmLine.register_1, RISCVRegister.REG_A0);

        // DEBUG

        System.out.println(asmLine);
    }

    // @Test
    // public void testDecodeVSetVli() {

    //     //
    //     // Arrange
    //     //

    //     RV32IBaseIntegerInstructionSetDecoder rv32iBaseIntegerInstructionSetDecoder = new RV32IBaseIntegerInstructionSetDecoder(
    //             XLEN);

    //     //
    //     // Act
    //     //

    //     //int machineCode = 0xd7728301;
    //     int machineCode = 0x018372d7;
    //     List<AsmLine<?>> asmLine = rv32iBaseIntegerInstructionSetDecoder.decode(machineCode);

    //     //
    //     // Assert
    //     //

    //     Assert.assertNull(asmLine);
    // }

    // @Test
    // public void testTwosComplement() {

    // }
}
