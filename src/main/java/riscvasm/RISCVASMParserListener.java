// Generated from riscvasm\RISCVASMParser.g4 by ANTLR 4.9.1
package riscvasm;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RISCVASMParser}.
 */
public interface RISCVASMParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#asm_file}.
	 * @param ctx the parse tree
	 */
	void enterAsm_file(RISCVASMParser.Asm_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#asm_file}.
	 * @param ctx the parse tree
	 */
	void exitAsm_file(RISCVASMParser.Asm_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#asm_line}.
	 * @param ctx the parse tree
	 */
	void enterAsm_line(RISCVASMParser.Asm_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#asm_line}.
	 * @param ctx the parse tree
	 */
	void exitAsm_line(RISCVASMParser.Asm_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(RISCVASMParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(RISCVASMParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#mnemonic}.
	 * @param ctx the parse tree
	 */
	void enterMnemonic(RISCVASMParser.MnemonicContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#mnemonic}.
	 * @param ctx the parse tree
	 */
	void exitMnemonic(RISCVASMParser.MnemonicContext ctx);
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#params}.
	 * @param ctx the parse tree
	 */
	void enterParams(RISCVASMParser.ParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#params}.
	 * @param ctx the parse tree
	 */
	void exitParams(RISCVASMParser.ParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(RISCVASMParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(RISCVASMParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(RISCVASMParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(RISCVASMParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link RISCVASMParser#register}.
	 * @param ctx the parse tree
	 */
	void enterRegister(RISCVASMParser.RegisterContext ctx);
	/**
	 * Exit a parse tree produced by {@link RISCVASMParser#register}.
	 * @param ctx the parse tree
	 */
	void exitRegister(RISCVASMParser.RegisterContext ctx);
}