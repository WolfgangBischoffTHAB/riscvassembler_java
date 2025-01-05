// Generated from mipsasm\MIPSParser.g4 by ANTLR 4.9.1
package mipsasm;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MIPSParser}.
 */
public interface MIPSParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MIPSParser#asm_file}.
	 * @param ctx the parse tree
	 */
	void enterAsm_file(MIPSParser.Asm_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#asm_file}.
	 * @param ctx the parse tree
	 */
	void exitAsm_file(MIPSParser.Asm_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#asm_line}.
	 * @param ctx the parse tree
	 */
	void enterAsm_line(MIPSParser.Asm_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#asm_line}.
	 * @param ctx the parse tree
	 */
	void exitAsm_line(MIPSParser.Asm_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(MIPSParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(MIPSParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#mnemonic}.
	 * @param ctx the parse tree
	 */
	void enterMnemonic(MIPSParser.MnemonicContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#mnemonic}.
	 * @param ctx the parse tree
	 */
	void exitMnemonic(MIPSParser.MnemonicContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#params}.
	 * @param ctx the parse tree
	 */
	void enterParams(MIPSParser.ParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#params}.
	 * @param ctx the parse tree
	 */
	void exitParams(MIPSParser.ParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(MIPSParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(MIPSParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(MIPSParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(MIPSParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifier(MIPSParser.ModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifier(MIPSParser.ModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(MIPSParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(MIPSParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#register}.
	 * @param ctx the parse tree
	 */
	void enterRegister(MIPSParser.RegisterContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#register}.
	 * @param ctx the parse tree
	 */
	void exitRegister(MIPSParser.RegisterContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAssembler_instruction(MIPSParser.Assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAssembler_instruction(MIPSParser.Assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#attribute_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAttribute_assembler_instruction(MIPSParser.Attribute_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#attribute_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAttribute_assembler_instruction(MIPSParser.Attribute_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#align_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAlign_assembler_instruction(MIPSParser.Align_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#align_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAlign_assembler_instruction(MIPSParser.Align_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#equ_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterEqu_assembler_instruction(MIPSParser.Equ_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#equ_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitEqu_assembler_instruction(MIPSParser.Equ_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#extern_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterExtern_assembler_instruction(MIPSParser.Extern_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#extern_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitExtern_assembler_instruction(MIPSParser.Extern_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#section_definition_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSection_definition_assembler_instruction(MIPSParser.Section_definition_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#section_definition_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSection_definition_assembler_instruction(MIPSParser.Section_definition_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#globl_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterGlobl_assembler_instruction(MIPSParser.Globl_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#globl_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitGlobl_assembler_instruction(MIPSParser.Globl_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#global_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterGlobal_assembler_instruction(MIPSParser.Global_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#global_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitGlobal_assembler_instruction(MIPSParser.Global_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#text_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterText_assembler_instruction(MIPSParser.Text_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#text_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitText_assembler_instruction(MIPSParser.Text_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#type_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterType_assembler_instruction(MIPSParser.Type_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#type_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitType_assembler_instruction(MIPSParser.Type_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#data_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterData_assembler_instruction(MIPSParser.Data_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#data_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitData_assembler_instruction(MIPSParser.Data_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#byte_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterByte_assembler_instruction(MIPSParser.Byte_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#byte_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitByte_assembler_instruction(MIPSParser.Byte_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#space_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSpace_assembler_instruction(MIPSParser.Space_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#space_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSpace_assembler_instruction(MIPSParser.Space_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#half_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterHalf_assembler_instruction(MIPSParser.Half_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#half_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitHalf_assembler_instruction(MIPSParser.Half_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#weak_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterWeak_assembler_instruction(MIPSParser.Weak_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#weak_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitWeak_assembler_instruction(MIPSParser.Weak_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#word_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterWord_assembler_instruction(MIPSParser.Word_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#word_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitWord_assembler_instruction(MIPSParser.Word_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#dword_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterDword_assembler_instruction(MIPSParser.Dword_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#dword_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitDword_assembler_instruction(MIPSParser.Dword_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#file_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterFile_assembler_instruction(MIPSParser.File_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#file_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitFile_assembler_instruction(MIPSParser.File_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#skip_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSkip_assembler_instruction(MIPSParser.Skip_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#skip_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSkip_assembler_instruction(MIPSParser.Skip_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#ascii_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAscii_assembler_instruction(MIPSParser.Ascii_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#ascii_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAscii_assembler_instruction(MIPSParser.Ascii_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#asciz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAsciz_assembler_instruction(MIPSParser.Asciz_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#asciz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAsciz_assembler_instruction(MIPSParser.Asciz_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#asciiz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterAsciiz_assembler_instruction(MIPSParser.Asciiz_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#asciiz_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitAsciiz_assembler_instruction(MIPSParser.Asciiz_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#string_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterString_assembler_instruction(MIPSParser.String_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#string_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitString_assembler_instruction(MIPSParser.String_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#option_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterOption_assembler_instruction(MIPSParser.Option_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#option_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitOption_assembler_instruction(MIPSParser.Option_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#size_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterSize_assembler_instruction(MIPSParser.Size_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#size_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitSize_assembler_instruction(MIPSParser.Size_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#ident_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void enterIdent_assembler_instruction(MIPSParser.Ident_assembler_instructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#ident_assembler_instruction}.
	 * @param ctx the parse tree
	 */
	void exitIdent_assembler_instruction(MIPSParser.Ident_assembler_instructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#csv_identifier_list}.
	 * @param ctx the parse tree
	 */
	void enterCsv_identifier_list(MIPSParser.Csv_identifier_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#csv_identifier_list}.
	 * @param ctx the parse tree
	 */
	void exitCsv_identifier_list(MIPSParser.Csv_identifier_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link MIPSParser#csv_numeric_list}.
	 * @param ctx the parse tree
	 */
	void enterCsv_numeric_list(MIPSParser.Csv_numeric_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link MIPSParser#csv_numeric_list}.
	 * @param ctx the parse tree
	 */
	void exitCsv_numeric_list(MIPSParser.Csv_numeric_listContext ctx);
}