package com.mycompany.data;

/**
 * <pre>
 * REG_ZERO : X '0' ;       // 0, Hard-wired zero
 * REG_RA : X '1' ;         // 1, Return address
 * REG_SP : X '2' ;         // 2, Stack pointer
 * REG_GP : X '3' ;         // 3, Global pointer
 * REG_TP : X '4' ;         // 4, Thread pointer
 *
 * REG_T0 : X '5' ;         // 5, Temporary/alternate link register
 * REG_T1 : X '6' ;         // 6, Temporary
 * REG_T2 : X '7' ;         // 7, Temporary
 *
 * REG_S0 : X '8' ;         // 8, Saved register/frame pointer
 * REG_S1 : X '9' ;         // 9, Saved register
 *
 * REG_A0 : X '1' '0' ;     // 10, Function arguments/return values
 * REG_A1 : X '1' '1' ;     // 11, Function arguments/return values
 *
 * REG_A2 : X '1' '2' ;     // 12, Function arguments
 * REG_A3 : X '1' '3' ;     // 13, Function arguments
 * REG_A4 : X '1' '4' ;     // 14, Function arguments
 * REG_A5 : X '1' '5' ;     // 15, Function arguments
 * REG_A6 : X '1' '6' ;     // 16, Function arguments
 * REG_A7 : X '1' '7' ;     // 17, Function arguments
 *
 * REG_S2 : X '1' '8' ;     // 18, Saved registers (By convention, called function should save and restore s register if it wants to use them!)
 * REG_S3 : X '1' '9' ;     // 19, Saved registers
 * REG_S4 : X '2' '0' ;     // 20, Saved registers
 * REG_S5 : X '2' '1' ;     // 21, Saved registers
 * REG_S6 : X '2' '2' ;     // 22, Saved registers
 * REG_S7 : X '2' '3' ;     // 23, Saved registers
 * REG_S8 : X '2' '4' ;     // 24, Saved registers
 * REG_S9 : X '2' '5' ;     // 25, Saved registers
 * REG_S10 : X '2' '6' ;    // 26, Saved registers
 * REG_S11 : X '2' '7' ;    // 27, Saved registers
 *
 * REG_T3 : X '2' '8' ;     // 28, Temporary (can be trashed, caller saved)
 * REG_T4 : X '2' '9' ;     // 29, Temporary (can be trashed, caller saved)
 * REG_T5 : X '3' '0' ;     // 30, Temporary (can be trashed, caller saved)
 * REG_T6 : X '3' '1' ;     // 31, Temporary (can be trashed, caller saved)
 * </pre>
 */
public enum RISCVRegister implements Register {

    /** x0 */
    REG_ZERO(0),
    /** x1 */
    REG_RA(1),
    /** x2 */
    REG_SP(2), // Stack Pointer
    /** x3 */
    REG_GP(3), // Global Pointer
    /** x4 */
    REG_TP(4),

    /** x5 */
    REG_T0(5), // temp register
    /** x6 */
    REG_T1(6), // temp register
    /** x7 */
    REG_T2(7), // temp register

    /** x8 */
    REG_S0(8), // saved register 0 / frame pointer
    /** x8 */
    REG_FP(8), // saved register 0 / frame pointer

    /** x9 */
    REG_S1(9), // saved register 1

    /** x10 */
    REG_A0(10), // function argument 0 / return value 0
    /** x11 */
    REG_A1(11), // function argument 1 / return value 1
    /** x12 */
    REG_A2(12), // function argument 2
    /** x13 */
    REG_A3(13),
    /** x14 */
    REG_A4(14),
    /** x15 */
    REG_A5(15),
    /** x16 */
    REG_A6(16),
    /** x17 */
    REG_A7(17), // function argument 7

    /** x18 */
    REG_S2(18), // saved register 2
    /** x19 */
    REG_S3(19),
    /** x20 */
    REG_S4(20),
    /** x21 */
    REG_S5(21),
    /** x22 */
    REG_S6(22),
    /** x23 */
    REG_S7(23),
    /** x24 */
    REG_S8(24),
    /** x25 */
    REG_S9(25),
    /** x26 */
    REG_S10(26),
    /** x27 */
    REG_S11(27), // saved register 11

    /** x28 */
    REG_T3(28), // temporary register 3
    /** x29 */
    REG_T4(29),
    /** x30 */
    REG_T5(30),
    /** x31 */
    REG_T6(31), // temporary register 6

    /** x0 */
    REG_X0(0),
    /** x1 */
    REG_X1(1),
    /** x2 */
    REG_X2(2),
    /** x3 */
    REG_X3(3),
    /** x4 */
    REG_X4(4),

    /** x5 */
    REG_X5(5),
    /** x6 */
    REG_X6(6),
    /** x7 */
    REG_X7(7),

    /** x8 */
    REG_X8(8),

    /** x9 */
    REG_X9(9),

    /** x10 */
    REG_X10(10),
    /** x11 */
    REG_X11(11),
    /** x12 */
    REG_X12(12),
    /** x13 */
    REG_X13(13),
    /** x14 */
    REG_X14(14),
    /** x15 */
    REG_X15(15),
    /** x16 */
    REG_X16(16),
    /** x17 */
    REG_X17(17),

    /** x18 */
    REG_X18(18),
    /** x19 */
    REG_X19(19),
    /** x20 */
    REG_X20(20),
    /** x21 */
    REG_X21(21),
    /** x22 */
    REG_X22(22),
    /** x23 */
    REG_X23(23),
    /** x24 */
    REG_X24(24),
    /** x25 */
    REG_X25(25),
    /** x26 */
    REG_X26(26),
    /** x27 */
    REG_X27(27),

    /** x28 */
    REG_X28(28),
    /** x29 */
    REG_X29(29),
    /** x30 */
    REG_X30(30),
    /** x31 */
    REG_X31(31),

    //
    // V-Extension (RVV vector extension)
    //

    REG_V0(0x00),
    REG_V0_T(0x01),
    REG_V1(0x02),
    REG_V1_T(0x03),
    REG_V2(0x04),
    REG_V2_T(0x05),

    REG_UNKNOWN(255);

    private int index;

    RISCVRegister(final int index) {
        this.index = index;
    }

    public static Register fromInt(final int data) {

        switch (data) {

            case 0x00:
                return REG_ZERO;
            case 0x01:
                return REG_RA;
            case 0x02:
                return REG_SP;
            case 0x03:
                return REG_GP;
            case 0x04:
                return REG_TP;
            case 0x05:
                return REG_T0;
            case 0x06:
                return REG_T1;
            case 0x07:
                return REG_T2;
            case 0x08:
                return REG_S0;
            case 0x09:
                return REG_S1;
            case 0x0A:
                return REG_A0;
            case 0x0B:
                return REG_A1;
            case 0x0C:
                return REG_A2;
            case 0x0D:
                return REG_A3;
            case 0x0E:
                return REG_A4;
            case 0x0F:
                return REG_A5;
            case 0x10:
                return REG_A6;
            case 0x11:
                return REG_A7;
            case 0x12:
                return REG_S2;
            case 0x13:
                return REG_S3;
            case 0x14:
                return REG_S4;
            case 0x15:
                return REG_S5;
            case 0x16:
                return REG_S6;
            case 0x17:
                return REG_S7;
            case 0x18:
                return REG_S8;
            case 0x19:
                return REG_S9;
            case 0x1A:
                return REG_S10;
            case 0x1B:
                return REG_S11;
            case 0x1C:
                return REG_T3;
            case 0x1D:
                return REG_T4;
            case 0x1E:
                return REG_T5;
            case 0x1F:
                return REG_T6;

            default:
                throw new RuntimeException("Unknown register: \"" + data + "\"");
        }
    }

    /**
     * Unprivilegded spec, compressed, page 163, table 38
     * RVC Register Number
     * 
     * Integer Register Number
     * 000 001 010 011 100 101 110 111
     * _x8 _x9 x10 x11 x12 x13 x14 x15
     * 
     * Integer Register ABI Name
     * 000 001 010 011 100 101 110 111
     * _s0 _s1 _a0 _a1 _a2 _a3 _a4 _a5
     * 
     * Floating-Point Register Number
     * 000 001 010 011 100 101 110 111
     * _f8 _f9 f10 f11 f12 f13 f14 f15
     * 
     * Floating-Point Register ABI Name
     * 000 001 010 011 100 101 110 111
     * fs0 fs1 fa0 fa1 fa2 fa3 fa4 fa5
     *
     * @param data
     * @return
     */
    public static Register fromIntCompressedInstruction(final int data) {

        switch (data) {

            /** 0 */
            case 0x00:
                return REG_X8;
            /** 1 */
            case 0x01:
                return REG_X9;
            /** 2 */
            case 0x02:
                return REG_X10;
            /** 3 */
            case 0x03:
                return REG_X11;
            /** 4 */
            case 0x04:
                return REG_X12;
            /** 5 */
            case 0x05:
                return REG_X13;
            /** 6 */
            case 0x06:
                return REG_X14;
            /** 7 */
            case 0x07:
                return REG_X15;

            default:
                throw new RuntimeException("Unknown C-Extension register: \"" + data + "\"");
        }
    }

    public static Register fromIntRVV(final int data) {

        switch (data) {

            //
            // V-Extension (RVV Vector Extension)
            //

            /** 0 */
            case 0x00:
                return REG_V0;
            /** 1 */
            case 0x01:
                return REG_V0_T;
            /** 2 */
            case 0x02:
                return REG_V1;
            /** 3 */
            case 0x03:
                return REG_V1_T;
            /** 4 */
            case 0x04:
                return REG_V2;
            /** 5 */
            case 0x05:
                return REG_V2_T;

            default:
                throw new RuntimeException("Unknown V-Extension register: \"" + data + "\"");
        }
    }

    public static Register fromString(final String register) {

        if (register.equalsIgnoreCase("ZERO")) { // 0
            return REG_ZERO;
        } else if (register.equalsIgnoreCase("RA")) { // 1
            return REG_RA;
        } else if (register.equalsIgnoreCase("SP")) { // 2
            return REG_SP;
        } else if (register.equalsIgnoreCase("GP")) { // 3
            return REG_GP;
        } else if (register.equalsIgnoreCase("TP")) { // 4
            return REG_TP;
        } else if (register.equalsIgnoreCase("T0")) { // 5
            return REG_T0;
        } else if (register.equalsIgnoreCase("T1")) {
            return REG_T1;
        } else if (register.equalsIgnoreCase("T2")) { // 7
            return REG_T2;
        } else if (register.equalsIgnoreCase("S0")) { // 8, also FP
            return REG_S0; // also FP
        }
        // else if (register.equalsIgnoreCase("FP")) { // 8
        // return REG_FP; // also S0
        // }
        else if (register.equalsIgnoreCase("S1")) { // 9
            return REG_S1;
        } else if (register.equalsIgnoreCase("A0")) { // 10
            return REG_A0;
        } else if (register.equalsIgnoreCase("A1")) {
            return REG_A1;
        } else if (register.equalsIgnoreCase("A2")) {
            return REG_A2;
        } else if (register.equalsIgnoreCase("A3")) {
            return REG_A3;
        } else if (register.equalsIgnoreCase("A4")) {
            return REG_A4;
        } else if (register.equalsIgnoreCase("A5")) {
            return REG_A5;
        } else if (register.equalsIgnoreCase("A6")) {
            return REG_A6;
        } else if (register.equalsIgnoreCase("A7")) { // 17
            return REG_A7;
        } else if (register.equalsIgnoreCase("S2")) { // 18
            return REG_S2;
        } else if (register.equalsIgnoreCase("S3")) {
            return REG_S3;
        } else if (register.equalsIgnoreCase("S4")) {
            return REG_S4;
        } else if (register.equalsIgnoreCase("S5")) {
            return REG_S5;
        } else if (register.equalsIgnoreCase("S6")) {
            return REG_S6;
        } else if (register.equalsIgnoreCase("S7")) {
            return REG_S7;
        } else if (register.equalsIgnoreCase("S8")) {
            return REG_S8;
        } else if (register.equalsIgnoreCase("S9")) {
            return REG_S9;
        } else if (register.equalsIgnoreCase("S10")) {
            return REG_S10;
        } else if (register.equalsIgnoreCase("S11")) { // 27
            return REG_S11;
        } else if (register.equalsIgnoreCase("T3")) { // 28
            return REG_T3;
        } else if (register.equalsIgnoreCase("T4")) {
            return REG_T4;
        } else if (register.equalsIgnoreCase("T5")) {
            return REG_T5;
        } else if (register.equalsIgnoreCase("T6")) { // 31
            return REG_T6;
        }

        if (register.equalsIgnoreCase("x0") || register.equalsIgnoreCase("$0")) {
            return REG_ZERO;
        } else if (register.equalsIgnoreCase("x1") || register.equalsIgnoreCase("$1")) {
            return REG_RA;
        } else if (register.equalsIgnoreCase("x2") || register.equalsIgnoreCase("$2")) {
            return REG_SP;
        } else if (register.equalsIgnoreCase("x3") || register.equalsIgnoreCase("$3")) {
            return REG_GP;
        } else if (register.equalsIgnoreCase("x4") || register.equalsIgnoreCase("$4")) {
            return REG_TP;
        } else if (register.equalsIgnoreCase("x5") || register.equalsIgnoreCase("$5")) {
            return REG_T0;
        } else if (register.equalsIgnoreCase("x6") || register.equalsIgnoreCase("$6")) {
            return REG_T1;
        } else if (register.equalsIgnoreCase("x7") || register.equalsIgnoreCase("$7")) {
            return REG_T2;
        } else if (register.equalsIgnoreCase("x8") || register.equalsIgnoreCase("$8")) {
            return REG_S0;
        } else if (register.equalsIgnoreCase("x9") || register.equalsIgnoreCase("$9")) {
            return REG_S1;
        } else if (register.equalsIgnoreCase("x10") || register.equalsIgnoreCase("$10")) {
            return REG_A0;
        } else if (register.equalsIgnoreCase("x11") || register.equalsIgnoreCase("$11")) {
            return REG_A1;
        } else if (register.equalsIgnoreCase("x12") || register.equalsIgnoreCase("$12")) {
            return REG_A2;
        } else if (register.equalsIgnoreCase("x13") || register.equalsIgnoreCase("$13")) {
            return REG_A3;
        } else if (register.equalsIgnoreCase("x14") || register.equalsIgnoreCase("$14")) {
            return REG_A4;
        } else if (register.equalsIgnoreCase("x15") || register.equalsIgnoreCase("$15")) {
            return REG_A5;
        } else if (register.equalsIgnoreCase("x16") || register.equalsIgnoreCase("$16")) {
            return REG_A6;
        } else if (register.equalsIgnoreCase("x17") || register.equalsIgnoreCase("$17")) {
            return REG_A7;
        } else if (register.equalsIgnoreCase("x18") || register.equalsIgnoreCase("$18")) {
            return REG_S2;
        } else if (register.equalsIgnoreCase("x19") || register.equalsIgnoreCase("$19")) {
            return REG_S3;
        } else if (register.equalsIgnoreCase("x20") || register.equalsIgnoreCase("$20")) {
            return REG_S4;
        } else if (register.equalsIgnoreCase("x21") || register.equalsIgnoreCase("$21")) {
            return REG_S5;
        } else if (register.equalsIgnoreCase("x22") || register.equalsIgnoreCase("$22")) {
            return REG_S6;
        } else if (register.equalsIgnoreCase("x23") || register.equalsIgnoreCase("$23")) {
            return REG_S7;
        } else if (register.equalsIgnoreCase("x24") || register.equalsIgnoreCase("$24")) {
            return REG_S8;
        } else if (register.equalsIgnoreCase("x25") || register.equalsIgnoreCase("$25")) {
            return REG_S9;
        } else if (register.equalsIgnoreCase("x26") || register.equalsIgnoreCase("$26")) {
            return REG_S10;
        } else if (register.equalsIgnoreCase("x27") || register.equalsIgnoreCase("$27")) {
            return REG_S11;
        } else if (register.equalsIgnoreCase("x28") || register.equalsIgnoreCase("$28")) {
            return REG_T3;
        } else if (register.equalsIgnoreCase("x29") || register.equalsIgnoreCase("$29")) {
            return REG_T4;
        } else if (register.equalsIgnoreCase("x30") || register.equalsIgnoreCase("$30")) {
            return REG_T5;
        } else if (register.equalsIgnoreCase("x31") || register.equalsIgnoreCase("$31")) {
            return REG_T6;
        }

        //
        // V-Extension (RVV vector extension)
        //

        else if (register.equalsIgnoreCase("v0")) {
            return REG_V0;
        } else if (register.equalsIgnoreCase("v0.t")) {
            return REG_V0_T;
        } else if (register.equalsIgnoreCase("v1")) {
            return REG_V1;
        } else if (register.equalsIgnoreCase("v1.t")) {
            return REG_V1_T;
        } else if (register.equalsIgnoreCase("v2")) {
            return REG_V2;
        } else if (register.equalsIgnoreCase("v2.t")) {
            return REG_V2_T;
        }

        throw new RuntimeException("Unknown register: \"" + register + "\"");
    }

    public static String toString(final RISCVRegister register) {

        if (register == null) {
            return "null";
        }

        switch (register) {

            case REG_ZERO:
            case REG_X0:
                return "x0";
            case REG_RA:
            case REG_X1:
                return "x1";
            case REG_SP:
            case REG_X2:
                return "x2";
            case REG_GP:
            case REG_X3:
                return "x3";
            case REG_TP:
            case REG_X4:
                return "x4";
            case REG_T0:
            case REG_X5:
                return "x5";
            case REG_T1:
            case REG_X6:
                return "x6";
            case REG_T2:
            case REG_X7:
                return "x7";
            // case REG_FP:
            // return "x8";
            case REG_S0:
            case REG_X8:
                return "x8";
            case REG_S1:
            case REG_X9:
                return "x9";
            case REG_A0:
            case REG_X10:
                return "x10";
            case REG_A1:
            case REG_X11:
                return "x11";
            case REG_A2:
            case REG_X12:
                return "x12";
            case REG_A3:
            case REG_X13:
                return "x13";
            case REG_A4:
            case REG_X14:
                return "x14";
            case REG_A5:
            case REG_X15:
                return "x15";
            case REG_A6:
            case REG_X16:
                return "x16";
            case REG_A7:
            case REG_X17:
                return "x17";
            case REG_S2:
            case REG_X18:
                return "x18";
            case REG_S3:
            case REG_X19:
                return "x19";
            case REG_S4:
            case REG_X20:
                return "x20";
            case REG_S5:
            case REG_X21:
                return "x21";
            case REG_S6:
            case REG_X22:
                return "x22";
            case REG_S7:
            case REG_X23:
                return "x23";
            case REG_S8:
            case REG_X24:
                return "x24";
            case REG_S9:
            case REG_X25:
                return "x25";
            case REG_S10:
            case REG_X26:
                return "x26";
            case REG_S11:
            case REG_X27:
                return "x27";
            case REG_T3:
            case REG_X28:
                return "x28";
            case REG_T4:
            case REG_X29:
                return "x29";
            case REG_T5:
            case REG_X30:
                return "x30";
            case REG_T6:
            case REG_X31:
                return "x31";


            //
            // V-Extension (RVV Vector Extension)
            //

            case REG_V0:
                return "v0";

            default:
                throw new RuntimeException("Unknown register: \"" + register.ordinal() + "\"");
            // return "Unknown register: \"" + register.ordinal() + "\"";
        }
    }

    public static String toStringAbi(final RISCVRegister register) {

        switch (register) {

            /** x0 */
            case REG_X0:
            case REG_ZERO:
                return "zero";

            /** x1 */
            case REG_X1:
            case REG_RA:
                return "ra";

            /** x2 */
            case REG_X2:
            case REG_SP:
                return "sp";

            /** x3 */
            case REG_X3:
            case REG_GP:
                return "gp";
            case REG_X4:
            case REG_TP:
                return "tp";
            case REG_X5:
            case REG_T0:
                return "t0";
            case REG_X6:
            case REG_T1:
                return "t1";
            case REG_X7:
            case REG_T2:
                return "t2";
            case REG_S0:
                return "s0";
            case REG_X8:
            case REG_FP:
                return "fp";
            case REG_X9:
            case REG_S1:
                return "s1";
            case REG_X10:
            case REG_A0:
                return "a0";
            case REG_X11:
            case REG_A1:
                return "a1";
            case REG_X12:
            case REG_A2:
                return "a2";
            case REG_X13:
            case REG_A3:
                return "a3";
            case REG_X14:
            case REG_A4:
                return "a4";
            case REG_X15:
            case REG_A5:
                return "a5";
            case REG_X16:
            case REG_A6:
                return "a6";
            case REG_X17:
            case REG_A7:
                return "a7";
            case REG_X18:
            case REG_S2:
                return "s2";
            case REG_X19:
            case REG_S3:
                return "s3";
            case REG_X20:
            case REG_S4:
                return "s4";
            case REG_X21:
            case REG_S5:
                return "s5";
            case REG_X22:
            case REG_S6:
                return "s6";
            case REG_X23:
            case REG_S7:
                return "s7";
            case REG_X24:
            case REG_S8:
                return "s8";
            case REG_X25:
            case REG_S9:
                return "s9";
            case REG_X26:
            case REG_S10:
                return "s10";
            case REG_X27:
            case REG_S11:
                return "s11";
            case REG_X28:
            case REG_T3:
                return "t3";
            case REG_X29:
            case REG_T4:
                return "t4";
            case REG_X30:
            case REG_T5:
                return "t5";
            case REG_X31:
            case REG_T6:
                return "t6";
            //
            // V-Extension (RVV vector extension)
            //
            case REG_V0:
                return "v0";
            case REG_V0_T:
                return "v0";
            case REG_V1:
                return "v1";
            case REG_V1_T:
                return "v1.t";
            case REG_V2:
                return "v2";
            case REG_V2_T:
                return "v2.t";

            default:
                throw new RuntimeException("Unknown register: \"" + register + "\"");
        }
    }

    public String toStringAbi() {
        return toStringAbi(this);
    }

    public String toString() {
        return toString(this);
    }

    @Override
    public int getIndex() {
        return index;
    }

}
