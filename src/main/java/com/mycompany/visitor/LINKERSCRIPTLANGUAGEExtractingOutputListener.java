package com.mycompany.visitor;

import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.MemorySpecifier;
import com.mycompany.data.Section;

import linkerscriptlanguage.LINKERSCRIPTLANGUAGELexer;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.ExpContext;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.Memory_spec_attributesContext;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.Memory_spec_lengthContext;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.Memory_spec_originContext;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.Mustbe_expContext;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParserBaseListener;

public class LINKERSCRIPTLANGUAGEExtractingOutputListener extends LINKERSCRIPTLANGUAGEParserBaseListener {

    private Section section = new Section();

    public Map<String, Section> sectionMap;

    public  Map<String, MemorySpecifier> memorySpecifierMap;

    @Override
    public void exitAssignment(LINKERSCRIPTLANGUAGEParser.AssignmentContext ctx) {

        Mustbe_expContext mustbeExpContext = ctx.mustbe_exp();

        ExpContext expContext = mustbeExpContext.exp();

        int i = 0;
        if (expContext.getChildCount() == 1) {

            String variableName = ctx.NAME().toString();
            // System.out.print(variableName + " = ");

            ParseTree parseTree = expContext.getChild(i);
            TerminalNodeImpl terminalNodeImpl = (TerminalNodeImpl) parseTree;

            switch (terminalNodeImpl.getSymbol().getType()) {

                case LINKERSCRIPTLANGUAGELexer.NAME:
                    // System.out.println("name: " + terminalNodeImpl.getText());
                    break;

                case LINKERSCRIPTLANGUAGELexer.DEC_INT:
                    // System.out.println("dec int: " + terminalNodeImpl.getText());
                    break;

                case LINKERSCRIPTLANGUAGELexer.HEX_INT:
                    // System.out.println("hex int: " + terminalNodeImpl.getText());
                    break;

                default:
                    throw new RuntimeException("unknown");
            }

        } else {

            //throw new RuntimeException("Need to evaluate expression! " + expContext.getText());
            System.out.println("Need to evaluate expression! " + expContext.getText());

        }
    }

    @Override
    public void exitSection(LINKERSCRIPTLANGUAGEParser.SectionContext ctx) {

        section.name = ctx.NAME().getText();
        section.memspec = ctx.memspec_opt().NAME().getText();

        sectionMap.put(section.name, section);

        section = new Section();
    }

    @Override
    public void exitSection_name_spec(LINKERSCRIPTLANGUAGEParser.Section_name_specContext ctx) {
        section.inputSections.add(ctx.getText());
    }

    @Override
    public void exitMemory_spec(LINKERSCRIPTLANGUAGEParser.Memory_specContext ctx) {

        String memorySpecName = ctx.NAME().getText();
        String memorySpecAttributes = "unknown";
        long memorySpecOrigin = 0L;
        long memorySpecLength = 0L;

        for (int i = 0; i < ctx.getChildCount(); i++) {

            ParseTree child = ctx.getChild(i);

            if (child instanceof Memory_spec_attributesContext) {

                memorySpecAttributes = child.getChild(1).getText();
            }

            if (child instanceof Memory_spec_originContext) {

                ParseTree parseTree = child.getChild(2);
                memorySpecOrigin = NumberParseUtil.parseLong(parseTree.getChild(0).getText());
            }

            if (child instanceof Memory_spec_lengthContext) {

                ParseTree parseTree = child.getChild(2);
                memorySpecLength = NumberParseUtil.parseLong(parseTree.getChild(0).getText());
            }
        }

        // System.out.println("MemorySpec Name: " + memorySpecName);
        // System.out.println("MemorySpec Attributes: " + memorySpecAttributes);
        // System.out.println("MemorySpec Origin: " + memorySpecOrigin);
        // System.out.println("MemorySpec Length: " + memorySpecLength);

        MemorySpecifier memorySpecifier = new MemorySpecifier();
        memorySpecifier.memorySpecName = memorySpecName;
        memorySpecifier.memorySpecAttributes = memorySpecAttributes;
        memorySpecifier.memorySpecOrigin = memorySpecOrigin;
        memorySpecifier.memorySpecLength = memorySpecLength;

        memorySpecifierMap.put(memorySpecifier.memorySpecName, memorySpecifier);
    }

}