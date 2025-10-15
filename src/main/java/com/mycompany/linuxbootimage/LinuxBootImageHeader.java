package com.mycompany.linuxbootimage;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;

/**
 * https://elixir.bootlin.com/linux/v6.1.14/source/Documentation/riscv/boot-image-header.rst
 * 
 * u32 code0; // Executable code
 * u32 code1; // Executable code
 * u64 text_offset; // Image load offset, little endian
 * u64 image_size; // Effective Image size, little endian
 * u64 flags; // kernel flags, little endian
 * u32 version; // Version of this header
 * u32 res1 = 0; // Reserved
 * u64 res2 = 0; // Reserved
 * u64 magic = 0x5643534952; // Magic number, little endian, "RISCV"
 * u32 magic2 = 0x05435352; // Magic number 2, little endian, "RSC\x05"
 * u32 res3; // Reserved for PE COFF offset
 */
public class LinuxBootImageHeader {

    private static final Logger logger = LoggerFactory.getLogger(LinuxBootImageHeader.class);

    public int code0;
    public int code1;
    public long textOffset;
    public long imageSize;
    public long flags;
    public int version;
    public int res1;
    public long res2;
    public long magic;
    public int magic2;
    public int res3;

    public int load(byte[] buffer, int offset) {

        code0 = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 0);
        code1 = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        textOffset = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 4);
        imageSize = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 8);
        flags = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 8);
        version = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 8);
        res1 = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);
        res2 = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 4);
        magic = ByteArrayUtil.decodeInt64FromArrayBigEndian(buffer, offset += 8);
        magic2 = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 8);
        res3 = ByteArrayUtil.decodeInt32FromArrayBigEndian(buffer, offset += 4);

        return offset;
    }

    public void print() throws UnsupportedEncodingException {
        
        logger.info("code0: " + ByteArrayUtil.byteToHex(code0));
        logger.info("code1: " + ByteArrayUtil.byteToHex(code1));
        logger.info("textOffset: " + ByteArrayUtil.byteToHex(textOffset));
        logger.info("imageSize: " + ByteArrayUtil.byteToHex(imageSize));
        logger.info("flags: " + ByteArrayUtil.byteToHex(flags));
        logger.info("version: " + ByteArrayUtil.byteToHex(version));
        logger.info("res1: " + ByteArrayUtil.byteToHex(res1));
        logger.info("res2: " + ByteArrayUtil.byteToHex(res2));
        logger.info("magic: " + ByteArrayUtil.byteToHex(magic));
        logger.info("magic2: " + ByteArrayUtil.byteToHex(magic2));
        logger.info("res3: " + ByteArrayUtil.byteToHex(res3));

        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2);
        byteBuffer.putLong(magic);
        byteBuffer.putLong(magic2);
        logger.info(new String(byteBuffer.array(), "ASCII"));
    }

}
