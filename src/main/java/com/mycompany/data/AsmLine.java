package com.mycompany.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsmLine<T extends Register> {

    private static final Logger logger = LoggerFactory.getLogger(AsmLine.class);

    public int sourceLine = -1;

    public long offset;
    public Section section;

    public String label;

    public Mnemonic mnemonic;

    public T register_0 = null;
    public T register_1 = null;
    public T register_2 = null;
    public T register_3 = null;
    public T register_4 = null;
    public T register_5 = null;

    public Long numeric_0 = null;
    public Long numeric_1 = null;
    public Long numeric_2 = null;
    public Long numeric_3 = null;
    public Long numeric_4 = null;
    public Long numeric_5 = null;

    public Long offset_0 = null;
    public Long offset_1 = null;
    public Long offset_2 = null;
    public Long offset_3 = null;
    public Long offset_4 = null;
    public Long offset_5= null;

    public String offsetLabel_0 = null;
    public String offsetLabel_1 = null;
    public String offsetLabel_2 = null;
    public String offsetLabel_3 = null;
    public String offsetLabel_4 = null;
    public String offsetLabel_5 = null;

    public String identifier_0 = null;
    public String identifier_1 = null;
    public String identifier_2 = null;
    public String identifier_3 = null;
    public String identifier_4 = null;
    public String identifier_5 = null;

    public AsmInstruction asmInstruction = null;

    public List<String> csvList = null;

    public Modifier modifier_0;
    public Modifier modifier_1;
    public Modifier modifier_2;
    public Modifier modifier_3;
    public Modifier modifier_4;
    public Modifier modifier_5;

    public String stringValue;

    public AsmLine<T> pseudoInstructionAsmLine = null;
    public List<AsmLine<T>> pseudoInstructionChildren = new ArrayList<>();

    public boolean optimized = false;

    public AsmLine<T> prev;
    public AsmLine<T> next;

    public ASTNode expr_0;
    public ASTNode expr_1;
    public ASTNode expr_2;
    public ASTNode expr_3;
    public ASTNode expr_4;
    public ASTNode expr_5;

    // RISCV V-Extension (RVV Vektor Extension)
    //
    // special type flags (vtypei) for the vsetvli instruction
    // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
    //
    // vsetvli rd, rs1, vtypei
    //
    // vtypei is a set of four flags (some of which are optional)
    // See page ??? of RISCV "V" Vector Extension Spec 1.0
    // vtypei ::= SEW, (LMUL,)? TAIL, MASK
    //
    // SEW - Selected Element Width {e8, e16, e32, e64}
    // how many bits does each vector element have?
    //
    // LMUL - vector length multiplier.
    // Is used to combine vectorregisters called vector grouping.
    // Is used to split vectorregisters (by using fractions of a register)
    // (See.: 3.4.2. Vector Register Grouping (vlmul[2:0]))
    //
    // The LMUL fractional options are {mf8, mf4, mf2}
    // The LMUL grouping options are {m1, m2, m4, m8}
    //
    // Since the LMUL vtypei setting is optional, a default value
    // of m1 is used when LMUL is not specified.
    //
    // TAIL - {ta, tu}
    // ta - tail agnostic - ???
    // tu - tail undisturbed - ???
    // 
    // MASK - {ma, mu}
    // ta - mask agnostic - ???
    // tu - mask undisturbed - ???
    public String rvvSew;
    public String rvvLmul;
    public String rvvTail;
    public String rvvMask;

    public boolean rvvMasking;

    public int instruction;

    public int encodedLength;

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        // stringBuilder.append("SourceLine: ").append(sourceLine).append(" ");
        // stringBuilder.append("[").append(offset).append("] ");

        // if (mnemonic == Mnemonic.I_VSETVLI) {
        //     logger.info("test");
        // }

        if (label != null) {
            stringBuilder.append(label).append(": ");
        }

        if (asmInstruction != null) {
            stringBuilder.append(AsmInstruction.toString(asmInstruction));
            switch (asmInstruction) {

                case EQU:
                    stringBuilder.append(" ").append(identifier_0).append(", ").append(numeric_1);
                    break;

                case SECTION:
                case GLOBAL:
                    stringBuilder.append(" ").append(stringValue);
                    break;

                case ASCIZ:
                case FILE:
                case STRING:
                    stringBuilder.append(" \"").append(stringValue).append("\"");
                    break;

                default:
                    stringBuilder.append(" ");
                    break;
            }
        }

        if (csvList != null) {
            boolean first = true;
            for (String val : csvList) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(val);

                first = false;
            }
        }

        if (mnemonic == null) {
            return stringBuilder.toString();
        }

        stringBuilder.append("    ");

        stringBuilder.append(Mnemonic.toString(mnemonic)).append(" ");

        boolean outputRegisterAsABI = true;
        outputBlock0(stringBuilder, outputRegisterAsABI);
        outputBlock1(stringBuilder, outputRegisterAsABI);
        outputBlock2(stringBuilder, outputRegisterAsABI);
        outputBlock3(stringBuilder, outputRegisterAsABI);
        outputBlock4(stringBuilder, outputRegisterAsABI);
        outputBlock5(stringBuilder, outputRegisterAsABI);

        //
        // V-Extension (RVV Vektor Extension)
        //

        // special type flags (vtypei) for the vsetvli instruction
        // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
        //
        // vsetvli rd, rs1, vtypei
        //
        // vtypei is a set of four flags (some of which are optional)
        // See page ??? of RISCV "V" Vector Extension Spec 1.0
        // vtypei ::= SEW, (LMUL,)? TAIL, MASK
        //
        // SEW - Selected Element Width {e8, e16, e32, e64}
        // how many bits does each vector element have?
        if (rvvSew != null) {
            stringBuilder.append(", ");
            stringBuilder.append(rvvSew);
        }

        // LMUL - vector length multiplier.
        // Is used to combine vector registers (called vector grouping).
        // Is used to split vectorregisters (by using fractions of a register)
        // (See.: 3.4.2. Vector Register Grouping (vlmul[2:0]))
        //
        // The LMUL fractional options are {mf8, mf4, mf2}
        // The LMUL grouping options are {m1, m2, m4, m8}
        //
        // Since the LMUL vtypei setting is optional, a default value
        // of m1 is used when LMUL is not specified.
        if (rvvLmul != null) {
            stringBuilder.append(", ");
            stringBuilder.append(rvvLmul);
        }

        // TAIL - {ta, tu}
        // ta - tail agnostic - ???
        // tu - tail undisturbed - ???
        if (rvvTail != null) {
            stringBuilder.append(", ");
            stringBuilder.append(rvvTail);
        }

        // MASK - {ma, mu}
        // ta - mask agnostic - ???
        // tu - mask undisturbed - ???
        if (rvvMask != null) {
            stringBuilder.append(", ");
            stringBuilder.append(rvvMask);
        }

        // for all instructions, that have masking activated
        if (rvvMasking) {
            stringBuilder.append(", ");
            stringBuilder.append(1);
        }

        if (pseudoInstructionAsmLine != null) {
            stringBuilder.append(" # --pseudo--> ").append(pseudoInstructionAsmLine.mnemonic);
        }

        return stringBuilder.toString();
    }

    private void outputBlock5(StringBuilder stringBuilder, boolean outputRegistersAsABI) {

        if (numeric_5 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(String.format("0x%X", numeric_5.intValue()));
        }
        if (modifier_5 != null) {

            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_5));

            stringBuilder.append("(");
            if (offsetLabel_5 != null) {
                stringBuilder.append(offsetLabel_5);
            }

            if (register_5 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_5));
                } else {
                    stringBuilder.append(Register.toString(register_5));
                }
            }

            if (identifier_5 != null) {
                stringBuilder.append(identifier_5);
            }

            stringBuilder.append(")");

        } else if (identifier_5 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(identifier_5);
        } else if (offset_5 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offset_5).append("(");
            if (register_5 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_5));
                } else {
                    stringBuilder.append(Register.toString(register_5));
                }
            }
            stringBuilder.append(")");
        } else if (offsetLabel_5 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offsetLabel_5).append("(");
            if (register_5 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_5));
                } else {
                    stringBuilder.append(Register.toString(register_5));
                }
            }
            stringBuilder.append(")");
        } else if (expr_5 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(expr_5.toString());
        } else {
            if (register_5 != null) {
                stringBuilder.append(", ");
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_5));
                } else {
                    stringBuilder.append(Register.toString(register_5));
                }
            }
        }
    }

    private void outputBlock4(StringBuilder stringBuilder, boolean outputRegistersAsABI) {

        if (numeric_4 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(String.format("0x%X", numeric_4.intValue()));
        }
        if (modifier_4 != null) {

            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_4));

            stringBuilder.append("(");
            if (offsetLabel_4 != null) {
                stringBuilder.append(offsetLabel_4);
            }

            if (register_4 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_4));
                } else {
                    stringBuilder.append(Register.toString(register_4));
                }
            }

            if (identifier_4 != null) {
                stringBuilder.append(identifier_4);
            }

            stringBuilder.append(")");

        } else if (identifier_4 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(identifier_4);
        } else if (offset_4 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offset_4).append("(");
            if (register_4 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_4));
                } else {
                    stringBuilder.append(Register.toString(register_4));
                }
            }
            stringBuilder.append(")");
        } else if (offsetLabel_4 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offsetLabel_4).append("(");
            if (register_4 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_4));
                } else {
                    stringBuilder.append(Register.toString(register_4));
                }
            }
            stringBuilder.append(")");
        } else if (expr_4 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(expr_4.toString());
        } else {
            if (register_4 != null) {
                stringBuilder.append(", ");
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_4));
                } else {
                    stringBuilder.append(Register.toString(register_4));
                }
            }
        }
    }

    private void outputBlock3(StringBuilder stringBuilder, boolean outputRegistersAsABI) {

        if (numeric_3 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(String.format("0x%X", numeric_3.intValue()));
        }
        if (modifier_3 != null) {
            
            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_3));

            stringBuilder.append("(");
            if (offsetLabel_3 != null) {
                stringBuilder.append(offsetLabel_3);
            }

            if (register_3 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_3));
                } else {
                    stringBuilder.append(Register.toString(register_3));
                }
            }

            if (identifier_3 != null) {
                stringBuilder.append(identifier_3);
            }

            stringBuilder.append(")");
            
        } else if (identifier_3 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(identifier_3);
        } else if (offset_3 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offset_3).append("(");
            if (register_3 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_3));
                } else {
                    stringBuilder.append(Register.toString(register_3));
                }
            }
            stringBuilder.append(")");
        } else if (offsetLabel_3 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offsetLabel_3).append("(");
            if (register_3 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_3));
                } else {
                    stringBuilder.append(Register.toString(register_3));
                }
            }
            stringBuilder.append(")");
        } else if (expr_3 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(expr_3.toString());
        } else {
            if (register_3 != null) {
                stringBuilder.append(", ");
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_3));
                } else {
                    stringBuilder.append(Register.toString(register_3));
                }
            }
        }
    }

    private void outputBlock2(StringBuilder stringBuilder, boolean outputRegistersAsABI) {

        if (numeric_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(String.format("0x%X", numeric_2.intValue()));
        }
        if (modifier_2 != null) {

            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_2));

            stringBuilder.append("(");
            if (offsetLabel_2 != null) {
                stringBuilder.append(offsetLabel_2);
            }

            if (register_2 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_2));
                } else {
                    stringBuilder.append(Register.toString(register_2));
                }
            }

            if (identifier_2 != null) {
                stringBuilder.append(identifier_2);
            }

            stringBuilder.append(")");

        } else if (identifier_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(identifier_2);
        } else if (offset_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offset_2).append("(");
            if (register_2 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_2));
                } else {
                    stringBuilder.append(Register.toString(register_2));
                }
            }
            stringBuilder.append(")");
        } else if (offsetLabel_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offsetLabel_2).append("(");
            if (register_2 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_2));
                } else {
                    stringBuilder.append(Register.toString(register_2));
                }
            }
            stringBuilder.append(")");
        } else if (expr_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(expr_2.toString());
        } else {
            if (register_2 != null) {
                stringBuilder.append(", ");
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_2));
                } else {
                    stringBuilder.append(Register.toString(register_2));
                }
            }
        }
    }

    private void outputBlock1(StringBuilder stringBuilder, boolean outputRegistersAsABI) {

        if (numeric_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(String.format("0x%X", numeric_1.intValue()));
        }
        if (modifier_1 != null) {

            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_1));

            stringBuilder.append("(");
            if (offsetLabel_1 != null) {
                stringBuilder.append(offsetLabel_1);
            }

            if (register_1 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_1));
                } else {
                    stringBuilder.append(Register.toString(register_1));
                }
            }

            if (identifier_1 != null) {
                stringBuilder.append(identifier_1);
            }

            stringBuilder.append(")");

        } else if (identifier_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(identifier_1);
        } else if (offset_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offset_1).append("(");
            if (register_1 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_1));
                } else {
                    stringBuilder.append(Register.toString(register_1));
                }
            }
            stringBuilder.append(")");
        } else if (offsetLabel_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offsetLabel_1).append("(");
            if (register_1 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_1));
                } else {
                    stringBuilder.append(Register.toString(register_1));
                }
            }
            stringBuilder.append(")");
        } else if (expr_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(expr_1.toString());
        } else {
            if (register_1 != null) {
                stringBuilder.append(", ");
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_1));
                } else {
                    stringBuilder.append(Register.toString(register_1));
                }
            }
        }
    }

    private void outputBlock0(StringBuilder stringBuilder, boolean outputRegistersAsABI) {

        if (numeric_0 != null) {
            stringBuilder.append(String.format("0x%X", numeric_0.intValue()));
        }
        if (modifier_0 != null) {

            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_0));

            stringBuilder.append("(");
            if (offsetLabel_0 != null) {
                stringBuilder.append(offsetLabel_0);
            }

            if (register_0 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_0));
                } else {
                    stringBuilder.append(Register.toString(register_0));
                }
            }

            if (identifier_0 != null) {
                stringBuilder.append(identifier_0);
            }

            stringBuilder.append(")");
            
        } else if (identifier_0 != null) {
            stringBuilder.append(identifier_0);
        } else if (offset_0 != null) {
            stringBuilder.append(offset_0).append("(");
            if (register_0 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_0));
                } else {
                    stringBuilder.append(Register.toString(register_0));
                }
            }
            stringBuilder.append(")");
        } else if (offsetLabel_0 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offsetLabel_0).append("(");
            if (register_0 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_0));
                } else {
                    stringBuilder.append(Register.toString(register_0));
                }
            }
            stringBuilder.append(")");
        } else if (expr_0 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(expr_0.toString());
        } else {
            if (register_0 != null) {
                if (outputRegistersAsABI) {
                    stringBuilder.append(Register.toStringAbi(register_0));
                } else {
                    stringBuilder.append(Register.toString(register_0));
                }
            }
        }
    }

    public AsmLineType getAsmLineType() {

        if (mnemonic != null) {

            // if (mnemonic == Mnemonic.I_PUTS) {
            //     return AsmLineType.EMULATOR_EXTENSION;
            // }

            if ((mnemonic != Mnemonic.I_UNKNOWN) && (!mnemonic.isPseudo())) {
                return AsmLineType.MNEMONIC;
            }
        }

        if (asmInstruction != null) {
            return AsmLineType.ASSEMBLER_INSTRUCTION;
        }

        return AsmLineType.UNKNOWN;
    }

}
