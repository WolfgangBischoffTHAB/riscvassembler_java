package com.mycompany.assembler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.slf4j.Logger;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Register;
import com.mycompany.data.Section;
import com.mycompany.encoder.Encoder;

public abstract class BaseAssembler<T extends Register> {

    // private static final Logger logger = LoggerFactory.getLogger(BaseAssembler.class);

    /** Combine far calls (auipc+jalr) into near calls (jal) if possible */
    // public static final boolean USE_CALL_OPTIMIZER = true;
    public static final boolean USE_CALL_OPTIMIZER = false;

    //public List<AsmLine<RISCVRegister>> asmLines = new ArrayList<>();
    public List<AsmLine<T>> asmLines = new ArrayList<>();

    public ParseTreeListener asmListener;

    // public Section currentSection;

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

    public List<AsmLine<T>> getAsmLines() {
        return asmLines;
    }

    public String spaces(int spaces) {
        return CharBuffer.allocate(spaces).toString().replace('\0', ' ');
    }

}