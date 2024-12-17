package com.mycompany.app;

import javax.management.RuntimeErrorException;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import com.mycompany.data.Section;

import linkerscriptlanguage.LINKERSCRIPTLANGUAGELexer;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.ExpContext;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser.Mustbe_expContext;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParserBaseListener;

public class LINKERSCRIPTLANGUAGEExtractingOutputListener extends LINKERSCRIPTLANGUAGEParserBaseListener {

    private Section section = new Section();

    @Override
    public void exitAssignment(LINKERSCRIPTLANGUAGEParser.AssignmentContext ctx) {

        Mustbe_expContext mustbeExpContext = ctx.mustbe_exp();
        // System.out.println(mustbeExpContext);

        ExpContext expContext = mustbeExpContext.exp();
        // System.out.println(expContext.toStringTree());

        int i = 0;
        // for (int i = 0; i < expContext.getChildCount(); i++) {
        if (expContext.getChildCount() == 1) {

            String variableName = ctx.NAME().toString();
            System.out.print(variableName + " = ");

            ParseTree parseTree = expContext.getChild(i);
            // System.out.println(parseTree);

            TerminalNodeImpl terminalNodeImpl = (TerminalNodeImpl) parseTree;
            // System.out.println(terminalNodeImpl);

            switch (terminalNodeImpl.getSymbol().getType()) {

                case LINKERSCRIPTLANGUAGELexer.NAME:
                    System.out.println("name: " + terminalNodeImpl.getText());
                    break;

                case LINKERSCRIPTLANGUAGELexer.DEC_INT:
                    System.out.println("dec int: " + terminalNodeImpl.getText());
                    break;

                case LINKERSCRIPTLANGUAGELexer.HEX_INT:
                    System.out.println("hex int: " + terminalNodeImpl.getText());
                    break;

                default:
                    throw new RuntimeException("unknown");
            }

        } else {

            throw new RuntimeException("Need to evaluate expression! " + expContext.getText());

        }
    }

    @Override
    public void exitSection(LINKERSCRIPTLANGUAGEParser.SectionContext ctx) {

        //System.out.println(ctx.getText());
        //System.out.println(ctx.NAME());

        section.name = ctx.NAME().getText();

        // for (int i = 0; i < ctx.getChildCount(); i++) {
        // ParseTree parseTree = ctx.getChild(i);
        // System.out.println(parseTree);
        // }

        //System.out.println(ctx.memspec_opt().NAME());

        section.memspec = ctx.memspec_opt().NAME().getText();

        System.out.println(section);

    }

    @Override
    public void exitSection_name_spec(LINKERSCRIPTLANGUAGEParser.Section_name_specContext ctx) {
        System.out.println(ctx.getText());

        section.inputSections.add(ctx.getText());
    }

}