package com.mycompany.data;

public enum Register {

    REG_UNKNOWN,

    REG_ZERO,
    REG_RA,
    REG_SP,
    REG_GP,
    REG_TP,

    REG_T0,
    REG_T1,
    REG_T2,
    REG_T3,
    REG_T4,
    REG_T5,
    REG_T6,

    REG_FP,

    REG_A0,
    REG_A1,
    REG_A2,
    REG_A3,
    REG_A4,
    REG_A5,
    REG_A6,
    REG_A7,

    REG_S0,
    REG_S1,
    REG_S2,
    REG_S3,
    REG_S4,
    REG_S5,
    REG_S6,
    REG_S7,
    REG_S8,
    REG_S9,
    REG_S10,
    REG_S11;

    // REG_ZERO : X '0' ; // 0, Hard-wired zero
    // REG_RA : X '1' ; // 1, Return address
    // REG_SP : X '2' ; // 2, Stack pointer
    // REG_GP : X '3' ; // 3, Global pointer
    // REG_TP : X '4' ; // 4, Thread pointer

    // REG_T0 : X '5' ; // 5, Temporary/alternate link register
    // REG_T1 : X '6' ; // 6, Temporary
    // REG_T2 : X '7' ; // 7, Temporary

    // REG_S0 : X '8' ; // 8, Saved register/frame pointer
    // REG_S1 : X '9' ; // 9, Saved register

    // REG_A0 : X '1' '0' ; // 10, Function arguments/return values
    // REG_A1 : X '1' '1' ; // 11, Function arguments/return values

    // REG_A2 : X '1' '2' ; // 12, Function arguments
    // REG_A3 : X '1' '3' ; // 13, Function arguments
    // REG_A4 : X '1' '4' ; // 14, Function arguments
    // REG_A5 : X '1' '5' ; // 15, Function arguments
    // REG_A6 : X '1' '6' ; // 16, Function arguments
    // REG_A7 : X '1' '7' ; // 17, Function arguments

    // REG_S2 : X '1' '8' ; // 18, Saved registers
    // REG_S3 : X '1' '9' ; // 19, Saved registers
    // REG_S4 : X '2' '0' ; // 20, Saved registers
    // REG_S5 : X '2' '1' ; // 21, Saved registers
    // REG_S6 : X '2' '2' ; // 22, Saved registers
    // REG_S7 : X '2' '3' ; // 23, Saved registers
    // REG_S8 : X '2' '4' ; // 24, Saved registers
    // REG_S9 : X '2' '5' ; // 25, Saved registers
    // REG_S10 : X '2' '6' ; // 26, Saved registers
    // REG_S11 : X '2' '7' ; // 27, Saved registers

    // REG_T3 : X '2' '8' ; // 28, Temporary
    // REG_T4 : X '2' '9' ; // 29, Temporary
    // REG_T5 : X '3' '0' ; // 30, Temporary
    // REG_T6 : X '3' '1' ; // 31, Temporary

    public static Register fromString(final String register) {
        if (register.equalsIgnoreCase("ZERO")) {
            return REG_ZERO;
        } else if (register.equalsIgnoreCase("RA")) {
            return REG_RA;
        } else if (register.equalsIgnoreCase("SP")) {
            return REG_SP;
        } else if (register.equalsIgnoreCase("GP")) {
            return REG_GP;
        } else if (register.equalsIgnoreCase("TP")) {
            return REG_TP;
        } else if (register.equalsIgnoreCase("T0")) {
            return REG_T0;
        } else if (register.equalsIgnoreCase("T1")) {
            return REG_T1;
        } else if (register.equalsIgnoreCase("T2")) {
            return REG_T2;
        } else if (register.equalsIgnoreCase("T3")) {
            return REG_T3;
        } else if (register.equalsIgnoreCase("T4")) {
            return REG_T4;
        } else if (register.equalsIgnoreCase("T5")) {
            return REG_T5;
        } else if (register.equalsIgnoreCase("T6")) {
            return REG_T6;
        } else if (register.equalsIgnoreCase("FP")) {
            return REG_FP;
        } else if (register.equalsIgnoreCase("A0")) {
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
        } else if (register.equalsIgnoreCase("A7")) {
            return REG_A7;
        } else if (register.equalsIgnoreCase("S0")) {
            return REG_S0;
        } else if (register.equalsIgnoreCase("S1")) {
            return REG_S1;
        } else if (register.equalsIgnoreCase("S2")) {
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
        } else if (register.equalsIgnoreCase("S11")) {
            return REG_S11;
        }

        if (register.equalsIgnoreCase("x0")) {
            return REG_ZERO;
        } else if (register.equalsIgnoreCase("x1")) {
            return REG_RA;
        } else if (register.equalsIgnoreCase("x2")) {
            return REG_SP;
        } else if (register.equalsIgnoreCase("x3")) {
            return REG_GP;
        } else if (register.equalsIgnoreCase("x4")) {
            return REG_TP;
        } else if (register.equalsIgnoreCase("x5")) {
            return REG_T0;
        } else if (register.equalsIgnoreCase("x6")) {
            return REG_T1;
        } else if (register.equalsIgnoreCase("x7")) {
            return REG_T2;
        } else if (register.equalsIgnoreCase("x8")) {
            return REG_T3;
        } else if (register.equalsIgnoreCase("x9")) {
            return REG_T4;
        } else if (register.equalsIgnoreCase("x10")) {
            return REG_T5;
        } else if (register.equalsIgnoreCase("x11")) {
            return REG_T6;
        } else if (register.equalsIgnoreCase("x12")) {
            return REG_FP;
        } else if (register.equalsIgnoreCase("x13")) {
            return REG_A0;
        } else if (register.equalsIgnoreCase("x14")) {
            return REG_A1;
        } else if (register.equalsIgnoreCase("x15")) {
            return REG_A2;
        } else if (register.equalsIgnoreCase("x16")) {
            return REG_A3;
        } else if (register.equalsIgnoreCase("x17")) {
            return REG_A4;
        } else if (register.equalsIgnoreCase("x18")) {
            return REG_A5;
        } else if (register.equalsIgnoreCase("x19")) {
            return REG_A6;
        } else if (register.equalsIgnoreCase("x20")) {
            return REG_A7;
        } else if (register.equalsIgnoreCase("x21")) {
            return REG_S0;
        } else if (register.equalsIgnoreCase("x22")) {
            return REG_S1;
        } else if (register.equalsIgnoreCase("x23")) {
            return REG_S2;
        } else if (register.equalsIgnoreCase("x24")) {
            return REG_S3;
        } else if (register.equalsIgnoreCase("x25")) {
            return REG_S4;
        } else if (register.equalsIgnoreCase("x26")) {
            return REG_S5;
        } else if (register.equalsIgnoreCase("x27")) {
            return REG_S6;
        } else if (register.equalsIgnoreCase("x28")) {
            return REG_S7;
        } else if (register.equalsIgnoreCase("x29")) {
            return REG_S8;
        } else if (register.equalsIgnoreCase("x30")) {
            return REG_S9;
        } else if (register.equalsIgnoreCase("x31")) {
            return REG_S10;
        } else if (register.equalsIgnoreCase("x32")) {
            return REG_S11;
        }

        throw new RuntimeException("Unknown register: \"" + register + "\"");
    }

    public static String toString(final Register register) {
        switch (register) {
            case REG_ZERO:
                return "x0";
            case REG_RA:
                return "x1";
            case REG_SP:
                return "x2";
            case REG_GP:
                return "x3";
            case REG_TP:
                return "x4";
            case REG_T0:
                return "x5";
            case REG_T1:
                return "x6";
            case REG_T2:
                return "x7";
            case REG_FP:
                return "x8";
            case REG_S1:
                return "x9";
            case REG_A0:
                return "x10";
            case REG_A1:
                return "x11";
            case REG_A2:
                return "x12";
            case REG_A3:
                return "x13";
            case REG_A4:
                return "x14";
            case REG_A5:
                return "x15";
            case REG_A6:
                return "x16";
            case REG_A7:
                return "x17";
            case REG_S2:
                return "x18";
            case REG_S3:
                return "x19";
            case REG_S4:
                return "x20";
            case REG_S5:
                return "x21";
            case REG_S6:
                return "x22";
            case REG_S7:
                return "x23";
            case REG_S8:
                return "x24";
            case REG_S9:
                return "x25";
            case REG_S10:
                return "x26";
            case REG_S11:
                return "x27";
            case REG_T3:
                return "x28";
            case REG_T4:
                return "x29";
            case REG_T5:
                return "x30";
            case REG_T6:
                return "x31";
            default:
                throw new RuntimeException("Unknown register: \"" + register + "\"");
        }
    }

    public static String toStringAbi(final Register register) {
        switch (register) {
            case REG_ZERO:
                return "zero";
            case REG_RA:
                return "ra";
            case REG_SP:
                return "sp";
            case REG_GP:
                return "gp";
            case REG_TP:
                return "tp";
            case REG_T0:
                return "t0";
            case REG_T1:
                return "t1";
            case REG_T2:
                return "t2";
            case REG_S0:
                return "s0";
            case REG_FP:
                return "fp";
            case REG_S1:
                return "s1";
            case REG_A0:
                return "a0";
            case REG_A1:
                return "a1";
            case REG_A2:
                return "a2";
            case REG_A3:
                return "a3";
            case REG_A4:
                return "a4";
            case REG_A5:
                return "a5";
            case REG_A6:
                return "a6";
            case REG_A7:
                return "a7";
            case REG_S2:
                return "s2";
            case REG_S3:
                return "s3";
            case REG_S4:
                return "s4";
            case REG_S5:
                return "s5";
            case REG_S6:
                return "s6";
            case REG_S7:
                return "s7";
            case REG_S8:
                return "s8";
            case REG_S9:
                return "s9";
            case REG_S10:
                return "s10";
            case REG_S11:
                return "s11";
            case REG_T3:
                return "t3";
            case REG_T4:
                return "t4";
            case REG_T5:
                return "t5";
            case REG_T6:
                return "t6";
            default:
                throw new RuntimeException("Unknown register: \"" + register + "\"");
        }
    }

}
