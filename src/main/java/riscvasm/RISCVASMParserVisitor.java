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
}