package com.mycompany.assembler;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.data.Section;
import com.mycompany.encoder.Encoder;
import com.mycompany.optimize.BaseOptimizer;
import com.mycompany.optimize.CallOptimizer;
import com.mycompany.optimize.LiOptimizer;
import com.mycompany.pseudo.combine.LiCombiner;
import com.mycompany.pseudo.resolve.BeqzResolver;
import com.mycompany.pseudo.resolve.BgezResolver;
import com.mycompany.pseudo.resolve.BgtResolver;
import com.mycompany.pseudo.resolve.BgtuResolver;
import com.mycompany.pseudo.resolve.BleResolver;
import com.mycompany.pseudo.resolve.BleuResolver;
import com.mycompany.pseudo.resolve.BnezResolver;
import com.mycompany.pseudo.resolve.CallResolver;
import com.mycompany.pseudo.resolve.CsrrResolver;
import com.mycompany.pseudo.resolve.JResolver;
import com.mycompany.pseudo.resolve.JrResolver;
import com.mycompany.pseudo.resolve.LaResolver;
import com.mycompany.pseudo.resolve.LiResolver;
import com.mycompany.pseudo.resolve.MvResolver;
import com.mycompany.pseudo.resolve.NopResolver;
import com.mycompany.pseudo.resolve.RetResolver;

public abstract class BaseAssembler<T extends Register> {

    // private static final Logger logger = LoggerFactory.getLogger(BaseAssembler.class);

    /** Combine far calls (auipc+jalr) into near calls (jal) if possible */
    public static final boolean USE_CALL_OPTIMIZER = true;
    // public static final boolean USE_CALL_OPTIMIZER = false;

    //public List<AsmLine<RISCVRegister>> asmLines = new ArrayList<>();
    public List<AsmLine<T>> asmLines = new ArrayList<>();

    public ParseTreeListener asmListener;

    public Section currentSection;

    public abstract TokenSource getLexer(String asmInputFile) throws IOException;

    public abstract Parser getParser(CommonTokenStream asmTokens);

    public abstract ParserRuleContext getRoot();

    public abstract Encoder getEncoder();

    public Map<String, Long> labelAddressMap;

    /** maps an address to the AsmLine the data at that address was encoded from */
    public Map<Long, AsmLine<?>> addressSourceAsmLineMap;

    public abstract Logger getLogger();

    public abstract void assemble(Map<String, Section> sectionMap, String asmInputFile) throws IOException;

    public static void outputHexMachineCode(final byte[] byteArray, final ByteOrder byteOrder) {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

        int data;
        int container = 0;
        int container_index = 0;
        
        while ((data = (int) byteArrayInputStream.read()) != -1) {

            container <<= 8;
            container += data;

            container_index++;

            if (container_index == 4) {

                byte[] temp = ByteArrayUtil.intToFourByte(container, byteOrder);

                System.out.print(ByteArrayUtil.bytesToHexLowerCase(temp));
                System.out.println("");

                container_index = 0;
                container = 0;
            }

        }
        System.out.println("");
    }

    // @Override
    public List<AsmLine<T>> getAsmLines() {
        return asmLines;
    }

    public String spaces(int spaces) {
        return CharBuffer.allocate(spaces).toString().replace('\0', ' ');
    }

}



/*
         * //
         * // Process Data defined in .section .data and provide the labels which are
         * mapped
         * // to the address where the data can be found.
         * //
         * // e.g.
         * // .section .data
         * // num1: .quad 0x123456789ABCDEF0, 0x0FEDCBA987654321, 0x0011223344556677,
         * 0x8899AABBCCDDEEFF
         * // num2: .quad 0x1122334455667788, 0x99AABBCCDDEEFF00, 0x1234567890ABCDEF,
         * 0xFEDCBA9876543210
         * // result: .space 32 # Reserve 32 bytes (256 bits) for the result
         * //
         * 
         * String currentSection = null;
         * int addressIndex = 0x10000;
         * 
         * List<AsmLine<?>> killList = new ArrayList<>();
         * 
         * for (AsmLine<?> asmLine : asmLines) {
         * 
         * logger.info(asmLine.toString());
         * 
         * if (asmLine.asmInstruction == null) {
         * continue;
         * }
         * 
         * switch (asmLine.asmInstruction) {
         * 
         * case SECTION:
         * currentSection = asmLine.stringValue;
         * killList.add(asmLine);
         * break;
         * 
         * case QUAD:
         * equMap.put(asmLine.label, addressIndex);
         * 
         * // insert all data in the CSV list
         * for (String dataElement : asmLine.csvList) {
         * 
         * // insert the data element at this address
         * asdf
         * 
         * // .quad define 64 bit data so advance by 8 byte
         * addressIndex += 8;
         * }
         * killList.add(asmLine);
         * break;
         * 
         * default:
         * break;
         * }
         * }
         * 
         * asmLines.removeAll(killList);
         */