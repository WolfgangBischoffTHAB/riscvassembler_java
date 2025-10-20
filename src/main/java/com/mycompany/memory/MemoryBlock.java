package com.mycompany.memory;

import java.nio.ByteOrder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;

public class MemoryBlock {

    public static final int SIZE = 1024 * 1024;

    private static final Logger logger = LoggerFactory.getLogger(MemoryBlock.class);

    public Long address;

    public int size = SIZE;

    public byte[] memory = new byte[SIZE];

    // private RV32IBaseIntegerInstructionSetDecoder decoder = new
    // RV32IBaseIntegerInstructionSetDecoder(App.XLEN);

    public Decoder decoder;

    public void print(int startAddress, int endAddress, ByteOrder byteOrder, int highlightAddress) {

        logger.info("From " + ByteArrayUtil.byteToHex(startAddress) + " to " + ByteArrayUtil.byteToHex(endAddress));

        int tempAddress = startAddress;

        for (int i = 0; i < memory.length / 4; i++) {

            // final int instruction = ByteArrayUtil.fourByteToInt(
            //         memory[(int) (tempAddress - address) + 0],
            //         memory[(int) (tempAddress - address) + 1],
            //         memory[(int) (tempAddress - address) + 2],
            //         memory[(int) (tempAddress - address) + 3], byteOrder);

            // the printer might run into some data area (heap, stack, random data)
            // If it tries to decode a instruction from random memory, it might fail
            try {

                List<AsmLine<?>> asmLines = decoder.decode(tempAddress);

                for (AsmLine<?> asmLine : asmLines) {

                    String asmLineAsString = asmLine.toString();
                    logger.info(
                            ByteArrayUtil.byteToHex(tempAddress) + ": "
                                    + ByteArrayUtil.byteToHex(asmLine.instruction, null, "%1$08X")
                                    + ((tempAddress == highlightAddress) ? " << " : "    ") + "          "
                                    + asmLineAsString);
                }

            } catch (Exception e) {

                e.printStackTrace();

                // logger.info(
                //         ByteArrayUtil.byteToHex(tempAddress) + ": "
                //                 + ByteArrayUtil.byteToHex(instruction, null, "%1$08X")
                //                 + ((tempAddress == highlightAddress) ? " << " : "    "));

            }

            if (tempAddress >= endAddress) {
                break;
            }

            tempAddress += 4;
        }
    }

}
