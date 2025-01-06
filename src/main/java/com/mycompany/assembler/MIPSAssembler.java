package com.mycompany.assembler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenSource;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Section;
import com.mycompany.encoder.Encoder;
import com.mycompany.encoder.MIPSEncoder;
import com.mycompany.visitor.MIPSASMExtractingOutputListener;

import mipsasm.MIPSLexer;
import mipsasm.MIPSParser;

public class MIPSAssembler extends BaseAssembler {

    private CharStream asmCharStream;

    private MIPSLexer lexer;

    private MIPSParser parser;

    private MIPSEncoder encoder = new MIPSEncoder();

    public MIPSAssembler(Map<String, Section> sectionMap, Section dummySection) {

        currentSection = sectionMap.get(".text");

        // set up the visitor
        // the extractor assembles AsmLineS by visiting the antlr4 AST
        MIPSASMExtractingOutputListener asmListener = new MIPSASMExtractingOutputListener();
        asmListener.dummySection = dummySection;
        asmListener.asmLines = asmLines;
        asmListener.sectionMap = sectionMap;
        asmListener.currentSection = currentSection;

        this.asmListener = asmListener;
    }

    @Override
    public TokenSource getLexer(String asmInputFile) throws IOException {

        asmCharStream = CharStreams.fromFileName(asmInputFile);
        lexer = new MIPSLexer(asmCharStream);

        return lexer;
    }

    @Override
    public Parser getParser(CommonTokenStream asmTokens) {

        parser = new MIPSParser(asmTokens);

        return parser;
    }

    @Override
    public ParserRuleContext getRoot() {
        return parser.asm_file();
    }

    @Override
    public List<AsmLine<?>> getAsmLines() {
        return asmLines;
    }

    @Override
    public Encoder getEncoder() {
        return encoder;
    }

}
