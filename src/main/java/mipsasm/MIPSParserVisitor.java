// Generated from mipsasm\MIPSParser.g4 by ANTLR 4.9.1
package mipsasm;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MIPSParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MIPSParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MIPSParser#asm_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm_file(MIPSParser.Asm_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#asm_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm_line(MIPSParser.Asm_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(MIPSParser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonic(MIPSParser.MnemonicContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(MIPSParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(MIPSParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(MIPSParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#modifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModifier(MIPSParser.ModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(MIPSParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#register}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegister(MIPSParser.RegisterContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssembler_instruction(MIPSParser.Assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#attribute_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute_assembler_instruction(MIPSParser.Attribute_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#align_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlign_assembler_instruction(MIPSParser.Align_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#equ_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqu_assembler_instruction(MIPSParser.Equ_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#extern_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtern_assembler_instruction(MIPSParser.Extern_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#section_definition_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection_definition_assembler_instruction(MIPSParser.Section_definition_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#globl_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobl_assembler_instruction(MIPSParser.Globl_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#global_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobal_assembler_instruction(MIPSParser.Global_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#text_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText_assembler_instruction(MIPSParser.Text_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#type_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_assembler_instruction(MIPSParser.Type_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#data_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_assembler_instruction(MIPSParser.Data_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#byte_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitByte_assembler_instruction(MIPSParser.Byte_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#space_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpace_assembler_instruction(MIPSParser.Space_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#half_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHalf_assembler_instruction(MIPSParser.Half_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#weak_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeak_assembler_instruction(MIPSParser.Weak_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#word_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWord_assembler_instruction(MIPSParser.Word_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#dword_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDword_assembler_instruction(MIPSParser.Dword_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#file_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_assembler_instruction(MIPSParser.File_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#skip_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkip_assembler_instruction(MIPSParser.Skip_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#ascii_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAscii_assembler_instruction(MIPSParser.Ascii_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#asciz_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsciz_assembler_instruction(MIPSParser.Asciz_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#asciiz_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsciiz_assembler_instruction(MIPSParser.Asciiz_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#string_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_assembler_instruction(MIPSParser.String_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#option_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption_assembler_instruction(MIPSParser.Option_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#size_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSize_assembler_instruction(MIPSParser.Size_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#ident_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdent_assembler_instruction(MIPSParser.Ident_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#csv_identifier_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsv_identifier_list(MIPSParser.Csv_identifier_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link MIPSParser#csv_numeric_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsv_numeric_list(MIPSParser.Csv_numeric_listContext ctx);
}