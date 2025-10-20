package com.mycompany.decoder;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mycompany.data.AsmLine;

public class RV32IBaseIntegerInstructionSetDecoderTest {

    public static final int XLEN = 64;

    @Test
    public void testDecodeVSetVli() {

        //
        // Arrange
        //

        RV32IBaseIntegerInstructionSetDecoder rv32iBaseIntegerInstructionSetDecoder = new RV32IBaseIntegerInstructionSetDecoder(
                XLEN);

        //
        // Act
        //

        //int machineCode = 0xd7728301;
        int machineCode = 0x018372d7;
        List<AsmLine<?>> asmLine = rv32iBaseIntegerInstructionSetDecoder.decode(machineCode);

        //
        // Assert
        //

        Assert.assertNull(asmLine);
    }

    @Test
    public void testTwosComplement() {

    }
}
