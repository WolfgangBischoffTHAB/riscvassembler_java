// Generated from linkerscriptlanguage/LINKERSCRIPTLANGUAGEParser.g4 by ANTLR 4.9.1
package linkerscriptlanguage;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LINKERSCRIPTLANGUAGEParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LINKERSCRIPTLANGUAGEParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(LINKERSCRIPTLANGUAGEParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#filename}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilename(LINKERSCRIPTLANGUAGEParser.FilenameContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#script_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript_file(LINKERSCRIPTLANGUAGEParser.Script_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#ifile_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfile_list(LINKERSCRIPTLANGUAGEParser.Ifile_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#ifile_p1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfile_p1(LINKERSCRIPTLANGUAGEParser.Ifile_p1Context ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memory_specs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemory_specs(LINKERSCRIPTLANGUAGEParser.Memory_specsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memory_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemory_spec(LINKERSCRIPTLANGUAGEParser.Memory_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memory_spec_attributes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemory_spec_attributes(LINKERSCRIPTLANGUAGEParser.Memory_spec_attributesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memory_spec_attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemory_spec_attribute(LINKERSCRIPTLANGUAGEParser.Memory_spec_attributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memory_spec_origin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemory_spec_origin(LINKERSCRIPTLANGUAGEParser.Memory_spec_originContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memory_spec_length}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemory_spec_length(LINKERSCRIPTLANGUAGEParser.Memory_spec_lengthContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#sections}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSections(LINKERSCRIPTLANGUAGEParser.SectionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#sec_or_group_p1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSec_or_group_p1(LINKERSCRIPTLANGUAGEParser.Sec_or_group_p1Context ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#wildcard_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildcard_name(LINKERSCRIPTLANGUAGEParser.Wildcard_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#wildcard_maybe_exclude}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildcard_maybe_exclude(LINKERSCRIPTLANGUAGEParser.Wildcard_maybe_excludeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#wildcard_maybe_reverse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildcard_maybe_reverse(LINKERSCRIPTLANGUAGEParser.Wildcard_maybe_reverseContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#filename_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilename_spec(LINKERSCRIPTLANGUAGEParser.Filename_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#section_name_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection_name_spec(LINKERSCRIPTLANGUAGEParser.Section_name_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#sect_flag_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSect_flag_list(LINKERSCRIPTLANGUAGEParser.Sect_flag_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#sect_flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSect_flags(LINKERSCRIPTLANGUAGEParser.Sect_flagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#exclude_name_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExclude_name_list(LINKERSCRIPTLANGUAGEParser.Exclude_name_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#section_name_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection_name_list(LINKERSCRIPTLANGUAGEParser.Section_name_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#input_section_spec_no_keep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_section_spec_no_keep(LINKERSCRIPTLANGUAGEParser.Input_section_spec_no_keepContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#input_section_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_section_spec(LINKERSCRIPTLANGUAGEParser.Input_section_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(LINKERSCRIPTLANGUAGEParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#statement_list_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_list_opt(LINKERSCRIPTLANGUAGEParser.Statement_list_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#statement_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_list(LINKERSCRIPTLANGUAGEParser.Statement_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#length}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLength(LINKERSCRIPTLANGUAGEParser.LengthContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#fill_exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFill_exp(LINKERSCRIPTLANGUAGEParser.Fill_expContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#fill_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFill_opt(LINKERSCRIPTLANGUAGEParser.Fill_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#statement_anywhere}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_anywhere(LINKERSCRIPTLANGUAGEParser.Statement_anywhereContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#assign_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_op(LINKERSCRIPTLANGUAGEParser.Assign_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#separator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeparator(LINKERSCRIPTLANGUAGEParser.SeparatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(LINKERSCRIPTLANGUAGEParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_comma}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_comma(LINKERSCRIPTLANGUAGEParser.Opt_commaContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#paren_script_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParen_script_name(LINKERSCRIPTLANGUAGEParser.Paren_script_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#mustbe_exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMustbe_exp(LINKERSCRIPTLANGUAGEParser.Mustbe_expContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(LINKERSCRIPTLANGUAGEParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memspec_at_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemspec_at_opt(LINKERSCRIPTLANGUAGEParser.Memspec_at_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_at}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_at(LINKERSCRIPTLANGUAGEParser.Opt_atContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_align}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_align(LINKERSCRIPTLANGUAGEParser.Opt_alignContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_align_with_input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_align_with_input(LINKERSCRIPTLANGUAGEParser.Opt_align_with_inputContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_subalign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_subalign(LINKERSCRIPTLANGUAGEParser.Opt_subalignContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#sect_constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSect_constraint(LINKERSCRIPTLANGUAGEParser.Sect_constraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection(LINKERSCRIPTLANGUAGEParser.SectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(LINKERSCRIPTLANGUAGEParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#atype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtype(LINKERSCRIPTLANGUAGEParser.AtypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_exp_with_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_exp_with_type(LINKERSCRIPTLANGUAGEParser.Opt_exp_with_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_exp_without_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_exp_without_type(LINKERSCRIPTLANGUAGEParser.Opt_exp_without_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_nocrossrefs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_nocrossrefs(LINKERSCRIPTLANGUAGEParser.Opt_nocrossrefsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#memspec_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemspec_opt(LINKERSCRIPTLANGUAGEParser.Memspec_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#phdr_opt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhdr_opt(LINKERSCRIPTLANGUAGEParser.Phdr_optContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#overlay_section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOverlay_section(LINKERSCRIPTLANGUAGEParser.Overlay_sectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#phdrs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhdrs(LINKERSCRIPTLANGUAGEParser.PhdrsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#phdr_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhdr_list(LINKERSCRIPTLANGUAGEParser.Phdr_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#phdr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhdr(LINKERSCRIPTLANGUAGEParser.PhdrContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#phdr_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhdr_type(LINKERSCRIPTLANGUAGEParser.Phdr_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#phdr_qualifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhdr_qualifiers(LINKERSCRIPTLANGUAGEParser.Phdr_qualifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#phdr_val}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhdr_val(LINKERSCRIPTLANGUAGEParser.Phdr_valContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#dynamic_list_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamic_list_file(LINKERSCRIPTLANGUAGEParser.Dynamic_list_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#dynamic_list_nodes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamic_list_nodes(LINKERSCRIPTLANGUAGEParser.Dynamic_list_nodesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#dynamic_list_node}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamic_list_node(LINKERSCRIPTLANGUAGEParser.Dynamic_list_nodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#dynamic_list_tag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamic_list_tag(LINKERSCRIPTLANGUAGEParser.Dynamic_list_tagContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#version_script_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVersion_script_file(LINKERSCRIPTLANGUAGEParser.Version_script_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVersion(LINKERSCRIPTLANGUAGEParser.VersionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#vers_nodes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVers_nodes(LINKERSCRIPTLANGUAGEParser.Vers_nodesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#vers_node}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVers_node(LINKERSCRIPTLANGUAGEParser.Vers_nodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#verdep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVerdep(LINKERSCRIPTLANGUAGEParser.VerdepContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#vers_tag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVers_tag(LINKERSCRIPTLANGUAGEParser.Vers_tagContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#vers_defns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVers_defns(LINKERSCRIPTLANGUAGEParser.Vers_defnsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#opt_semicolon}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpt_semicolon(LINKERSCRIPTLANGUAGEParser.Opt_semicolonContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#section_ordering_script_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection_ordering_script_file(LINKERSCRIPTLANGUAGEParser.Section_ordering_script_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#section_ordering_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection_ordering_list(LINKERSCRIPTLANGUAGEParser.Section_ordering_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link LINKERSCRIPTLANGUAGEParser#section_order}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection_order(LINKERSCRIPTLANGUAGEParser.Section_orderContext ctx);
}