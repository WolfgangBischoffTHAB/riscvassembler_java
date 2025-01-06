package com.mycompany.visitor;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import linkerscriptlanguage.LINKERSCRIPTLANGUAGELexer;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParser;
import linkerscriptlanguage.LINKERSCRIPTLANGUAGEParserBaseListener;

public class LINKERSCRIPTLANGUAGERawOutputListener extends LINKERSCRIPTLANGUAGEParserBaseListener {

    public LINKERSCRIPTLANGUAGEParser linkerParser;

    private int indent;

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        printIndent();
        System.out.println(ctx.getClass().getSimpleName() + " [" + ctx.getStart().getText() + "] " + ctx.hashCode() + " " + linkerParser.getRuleNames()[ctx.getRuleIndex()]);
        descend();
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        ascend();
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        descend();
        printIndent();
        System.out.println(node.getText() + " Type: " + LINKERSCRIPTLANGUAGELexer.VOCABULARY.getSymbolicName(node.getSymbol().getType()));
        // currentNode.setLabel("TERMINAL: " + node.getText());
        ascend();
    }

    private void descend() {
        indent++;
    }

    private void ascend() {
        indent--;
    }

    private void printIndent() {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
    }

}