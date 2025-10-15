package com.mycompany.linuxbootimage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.memory.Memory;

/**
 * https://medium.com/@danijel.korent/risc-v-pyemulator-devlog02-the-linux-kernel-image-explained-e2eac0ce935b
 * https://elixir.bootlin.com/linux/v6.1.14/source/Documentation/riscv/boot-image-header.rst
 */
public class LinuxBootImage {

    // https://github.com/Danijel-Korent/RISC-V-emulator/blob/main/config.py#L20
    public static final int START_ADDRESS_OF_RAM = 0x80000000;
    //START_ADDRESS_OF_UART = 0x10000000
    //START_ADDRESS_OF_TIMER_CLINT = 0x11000000

    private static final Logger logger = LoggerFactory.getLogger(LinuxBootImage.class);

    public String filename;

    public byte[] buffer;

    public Memory memory;

    public void load() throws IOException {

        int pos = 0;
        buffer = Files.readAllBytes(Paths.get(filename));

        // load the overall, top-level ELF-header that has offsets to all other parts of
        // the elf file
        LinuxBootImageHeader linuxBootImageHeader = new LinuxBootImageHeader();
        pos = linuxBootImageHeader.load(buffer, pos);

        // DEBUG
        linuxBootImageHeader.print();

        logger.info(ByteArrayUtil.byteToHex(linuxBootImageHeader.magic));

        // copy data into memory
        //memory.copy(START_ADDRESS_OF_RAM, buffer, 0, linuxBootImageHeader.imageSize);
        memory.copy(START_ADDRESS_OF_RAM, buffer, 0, 1024);

    }

    public void setFile(String filename) {
        this.filename = filename;
    }
    
}
