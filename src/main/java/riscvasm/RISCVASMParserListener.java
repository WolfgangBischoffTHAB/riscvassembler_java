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
}