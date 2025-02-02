// Generated from ASM6502asm\ASM6502Parser.g4 by ANTLR 4.9.1
package ASM6502asm;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ASM6502Parser}.
 */
public interface ASM6502ParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#asm_file}.
	 * @param ctx the parse tree
	 */
	void enterAsm_file(ASM6502Parser.Asm_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#asm_file}.
	 * @param ctx the parse tree
	 */
	void exitAsm_file(ASM6502Parser.Asm_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#asm_line}.
	 * @param ctx the parse tree
	 */
	void enterAsm_line(ASM6502Parser.Asm_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#asm_line}.
	 * @param ctx the parse tree
	 */
	void exitAsm_line(ASM6502Parser.Asm_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(ASM6502Parser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(ASM6502Parser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#mnemonic}.
	 * @param ctx the parse tree
	 */
	void enterMnemonic(ASM6502Parser.MnemonicContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#mnemonic}.
	 * @param ctx the parse tree
	 */
	void exitMnemonic(ASM6502Parser.MnemonicContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#params}.
	 * @param ctx the parse tree
	 */
	void enterParams(ASM6502Parser.ParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#params}.
	 * @param ctx the parse tree
	 */
	void exitParams(ASM6502Parser.ParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(ASM6502Parser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(ASM6502Parser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#immediate}.
	 * @param ctx the parse tree
	 */
	void enterImmediate(ASM6502Parser.ImmediateContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#immediate}.
	 * @param ctx the parse tree
	 */
	void exitImmediate(ASM6502Parser.ImmediateContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(ASM6502Parser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(ASM6502Parser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifier(ASM6502Parser.ModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifier(ASM6502Parser.ModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ASM6502Parser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ASM6502Parser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#register}.
	 * @param ctx the parse tree
	 */
	void enterRegister(ASM6502Parser.RegisterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#register}.
	 * @param ctx the parse tree
	 */
	void exitRegister(ASM6502Parser.RegisterContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAssembler_instruction(ASM6502Parser.Assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAssembler_instruction(ASM6502Parser.Assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#attribute_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAttribute_assembler_instruction(ASM6502Parser.Attribute_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#attribute_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAttribute_assembler_instruction(ASM6502Parser.Attribute_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#align_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAlign_assembler_instruction(ASM6502Parser.Align_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#align_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAlign_assembler_instruction(ASM6502Parser.Align_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#equ_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterEqu_assembler_instruction(ASM6502Parser.Equ_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#equ_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitEqu_assembler_instruction(ASM6502Parser.Equ_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#extern_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterExtern_assembler_instruction(ASM6502Parser.Extern_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#extern_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitExtern_assembler_instruction(ASM6502Parser.Extern_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#section_definition_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSection_definition_assembler_instruction(ASM6502Parser.Section_definition_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#section_definition_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSection_definition_assembler_instruction(ASM6502Parser.Section_definition_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#globl_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterGlobl_assembler_instruction(ASM6502Parser.Globl_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#globl_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitGlobl_assembler_instruction(ASM6502Parser.Globl_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#global_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterGlobal_assembler_instruction(ASM6502Parser.Global_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#global_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitGlobal_assembler_instruction(ASM6502Parser.Global_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#text_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterText_assembler_instruction(ASM6502Parser.Text_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#text_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitText_assembler_instruction(ASM6502Parser.Text_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#type_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterType_assembler_instruction(ASM6502Parser.Type_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#type_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitType_assembler_instruction(ASM6502Parser.Type_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#data_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterData_assembler_instruction(ASM6502Parser.Data_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#data_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitData_assembler_instruction(ASM6502Parser.Data_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#byte_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterByte_assembler_instruction(ASM6502Parser.Byte_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#byte_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitByte_assembler_instruction(ASM6502Parser.Byte_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#space_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSpace_assembler_instruction(ASM6502Parser.Space_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#space_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSpace_assembler_instruction(ASM6502Parser.Space_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#half_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterHalf_assembler_instruction(ASM6502Parser.Half_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#half_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitHalf_assembler_instruction(ASM6502Parser.Half_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#weak_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterWeak_assembler_instruction(ASM6502Parser.Weak_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#weak_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitWeak_assembler_instruction(ASM6502Parser.Weak_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#word_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterWord_assembler_instruction(ASM6502Parser.Word_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#word_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitWord_assembler_instruction(ASM6502Parser.Word_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#dword_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterDword_assembler_instruction(ASM6502Parser.Dword_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#dword_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitDword_assembler_instruction(ASM6502Parser.Dword_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#file_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterFile_assembler_instruction(ASM6502Parser.File_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#file_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitFile_assembler_instruction(ASM6502Parser.File_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#skip_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSkip_assembler_instruction(ASM6502Parser.Skip_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#skip_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSkip_assembler_instruction(ASM6502Parser.Skip_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#ascii_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAscii_assembler_instruction(ASM6502Parser.Ascii_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#ascii_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAscii_assembler_instruction(ASM6502Parser.Ascii_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#asciz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAsciz_assembler_instruction(ASM6502Parser.Asciz_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#asciz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAsciz_assembler_instruction(ASM6502Parser.Asciz_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#asciiz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAsciiz_assembler_instruction(ASM6502Parser.Asciiz_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#asciiz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAsciiz_assembler_instruction(ASM6502Parser.Asciiz_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#string_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterString_assembler_instruction(ASM6502Parser.String_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#string_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitString_assembler_instruction(ASM6502Parser.String_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#option_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterOption_assembler_instruction(ASM6502Parser.Option_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#option_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitOption_assembler_instruction(ASM6502Parser.Option_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#size_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSize_assembler_instruction(ASM6502Parser.Size_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#size_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSize_assembler_instruction(ASM6502Parser.Size_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#ident_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterIdent_assembler_instruction(ASM6502Parser.Ident_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#ident_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitIdent_assembler_instruction(ASM6502Parser.Ident_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#csv_identifier_list}.
	 * @param ctx the parse tree
	 */
	void enterCsv_identifier_list(ASM6502Parser.Csv_identifier_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#csv_identifier_list}.
	 * @param ctx the parse tree
	 */
	void exitCsv_identifier_list(ASM6502Parser.Csv_identifier_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link ASM6502Parser#csv_numeric_list}.
	 * @param ctx the parse tree
	 */
	void enterCsv_numeric_list(ASM6502Parser.Csv_numeric_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link ASM6502Parser#csv_numeric_list}.
	 * @param ctx the parse tree
	 */
	void exitCsv_numeric_list(ASM6502Parser.Csv_numeric_listContext ctx);
}