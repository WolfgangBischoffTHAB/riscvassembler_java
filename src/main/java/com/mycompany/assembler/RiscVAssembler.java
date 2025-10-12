package com.mycompany.assembler;

import java.io.IOException;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenSource;

import com.mycompany.data.Section;
import com.mycompany.encoder.Encoder;
import com.mycompany.encoder.RISCVEncoder;
import com.mycompany.visitor.RISCASMExtractingOutputListener;

import riscvasm.RISCVASMLexer;
import riscvasm.RISCVASMParser;

public class RiscVAssembler extends BaseAssembler {

    private CharStream asmCharStream;

    private RISCVASMLexer lexer;

    private RISCVASMParser parser;

    public RISCVEncoder encoder = new RISCVEncoder();

    /**
     * ctor
     *
     * @param sectionMap
     * @param dummySection
     */
    public RiscVAssembler(Map<String, Section> sectionMap, Section dummySection) {

        currentSection = sectionMap.get(".text");

        // set up the visitor
        // the extractor assembles AsmLineS by visiting the antlr4 AST
        RISCASMExtractingOutputListener asmListener = new RISCASMExtractingOutputListener();
        asmListener.dummySection = dummySection;
        asmListener.asmLines = asmLines;
        asmListener.sectionMap = sectionMap;
        asmListener.currentSection = currentSection;

        this.asmListener = asmListener;
    }

    @Override
    public TokenSource getLexer(String asmInputFile) throws IOException {

        asmCharStream = CharStreams.fromFileName(asmInputFile);
        lexer = new RISCVASMLexer(asmCharStream);

        return lexer;
    }

    @Override
    public Parser getParser(CommonTokenStream asmTokens) {
        parser = new RISCVASMParser(asmTokens);
        return parser;
    }

    @Override
    public ParserRuleContext getRoot() {
        return parser.asm_file();
    }

    @Override
    public Encoder getEncoder() {
        return encoder;
    }

}
