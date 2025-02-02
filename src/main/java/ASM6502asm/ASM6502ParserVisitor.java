// Generated from ASM6502asm\ASM6502Parser.g4 by ANTLR 4.9.1
package ASM6502asm;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ASM6502Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ASM6502ParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#asm_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm_file(ASM6502Parser.Asm_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#asm_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm_line(ASM6502Parser.Asm_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(ASM6502Parser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#mnemonic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMnemonic(ASM6502Parser.MnemonicContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(ASM6502Parser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(ASM6502Parser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#immediate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImmediate(ASM6502Parser.ImmediateContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(ASM6502Parser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#modifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModifier(ASM6502Parser.ModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(ASM6502Parser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#register}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegister(ASM6502Parser.RegisterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssembler_instruction(ASM6502Parser.Assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#attribute_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute_assembler_instruction(ASM6502Parser.Attribute_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#align_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlign_assembler_instruction(ASM6502Parser.Align_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#equ_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqu_assembler_instruction(ASM6502Parser.Equ_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#extern_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtern_assembler_instruction(ASM6502Parser.Extern_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#section_definition_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection_definition_assembler_instruction(ASM6502Parser.Section_definition_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#globl_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobl_assembler_instruction(ASM6502Parser.Globl_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#global_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobal_assembler_instruction(ASM6502Parser.Global_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#text_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText_assembler_instruction(ASM6502Parser.Text_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#type_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_assembler_instruction(ASM6502Parser.Type_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#data_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_assembler_instruction(ASM6502Parser.Data_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#byte_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitByte_assembler_instruction(ASM6502Parser.Byte_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#space_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpace_assembler_instruction(ASM6502Parser.Space_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#half_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHalf_assembler_instruction(ASM6502Parser.Half_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#weak_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeak_assembler_instruction(ASM6502Parser.Weak_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#word_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWord_assembler_instruction(ASM6502Parser.Word_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#dword_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDword_assembler_instruction(ASM6502Parser.Dword_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#file_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_assembler_instruction(ASM6502Parser.File_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#skip_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkip_assembler_instruction(ASM6502Parser.Skip_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#ascii_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAscii_assembler_instruction(ASM6502Parser.Ascii_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#asciz_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsciz_assembler_instruction(ASM6502Parser.Asciz_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#asciiz_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsciiz_assembler_instruction(ASM6502Parser.Asciiz_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#string_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_assembler_instruction(ASM6502Parser.String_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#option_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption_assembler_instruction(ASM6502Parser.Option_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#size_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSize_assembler_instruction(ASM6502Parser.Size_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#ident_assembler_instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdent_assembler_instruction(ASM6502Parser.Ident_assembler_instructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#csv_identifier_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsv_identifier_list(ASM6502Parser.Csv_identifier_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link ASM6502Parser#csv_numeric_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCsv_numeric_list(ASM6502Parser.Csv_numeric_listContext ctx);
}