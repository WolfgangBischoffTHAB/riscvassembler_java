// Generated from riscvasm\RISCVASMParser.g4 by ANTLR 4.9.1
package riscvasm;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RISCVASMParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RISCVASMParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#asm_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm_file(RISCVASMParser.Asm_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#asm_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm_line(RISCVASMParser.Asm_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(RISCVASMParser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonic(RISCVASMParser.MnemonicContext ctx);
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(RISCVASMParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(RISCVASMParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(RISCVASMParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link RISCVASMParser#register}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegister(RISCVASMParser.RegisterContext ctx);
}