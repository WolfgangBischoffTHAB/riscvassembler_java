package com.mycompany.data;

/**
 * https://minnie.tuhs.org/CompArch/Resources/mips_quick_tutorial.html
 */
public enum MIPSRegister implements Register {

     /** 0 */
     REG_ZERO,
     /** 1 */
     REG_AT,

     /** 2 */
     REG_V0,
     /** 3 */
     REG_V1,

     /** 4 */
     REG_A0,
     /** 5 */
     REG_A1,
     /** 6 */
     REG_A2,
     /** 7 */
     REG_A3,

     /** 8 */
     REG_T0,
     /** 9 */
     REG_T1,
     /** 10 */
     REG_T2,
     /** 11 */
     REG_T3,
     /** 12 */
     REG_T4,
     /** 13 */
     REG_T5,
     /** 14 */
     REG_T6,
     /** 15 */
     REG_T7,

     /** 16 */
     REG_S0,
     /** 17 */
     REG_S1,
     /** 18 */
     REG_S2,
     /** 19 */
     REG_S3,
     /** 20 */
     REG_S4,
     /** 21 */
     REG_S5,
     /** 22 */
     REG_S6,
     /** 23 */
     REG_S7,

     /** 24 */
     REG_T8,
     /** 25 */
     REG_T9,

     /** 26 */
     REG_K0,
     /** 27 */
     REG_K1,

     /** 28 */
     REG_GP,
     /** 29 */
     REG_SP,
     /** 30 */
     REG_FP,
     /** 31 */
     REG_RA,

     REG_UNKNOWN;

    /**
     * https://minnie.tuhs.org/CompArch/Resources/mips_quick_tutorial.html
     *
     * @param register
     * @return
     */
	public static Register fromString(String register) {

		if (register.equalsIgnoreCase("$zero")) { // 0
            return REG_ZERO;
        } else if (register.equalsIgnoreCase("$at")) { // 1
            return REG_RA;
        } else if (register.equalsIgnoreCase("$v0")) { // 2
            return REG_V0;
        } else if (register.equalsIgnoreCase("$v1")) { // 3
            return REG_V1;
        } else if (register.equalsIgnoreCase("$a0")) { // 4
            return REG_A0;
        } else if (register.equalsIgnoreCase("$a1")) {
            return REG_A1;
        } else if (register.equalsIgnoreCase("$a2")) {
            return REG_A2;
        } else if (register.equalsIgnoreCase("$a3")) { // 7
            return REG_A3;
        } else if (register.equalsIgnoreCase("$t0")) { // 8
            return REG_T0;
        } else if (register.equalsIgnoreCase("$t1")) {
            return REG_T1;
        } else if (register.equalsIgnoreCase("$t2")) {
            return REG_T2;
        } else if (register.equalsIgnoreCase("$t3")) {
            return REG_T3;
        } else if (register.equalsIgnoreCase("$t4")) {
            return REG_T4;
        } else if (register.equalsIgnoreCase("$t5")) {
            return REG_T5;
        } else if (register.equalsIgnoreCase("$t6")) {
            return REG_T6;
        } else if (register.equalsIgnoreCase("$t7")) { // 15
            return REG_T7;
        } else if (register.equalsIgnoreCase("$s0")) { // 16
            return REG_S0;
        } else if (register.equalsIgnoreCase("$s1")) {
            return REG_S1;
        } else if (register.equalsIgnoreCase("$s2")) {
            return REG_S2;
        } else if (register.equalsIgnoreCase("$s3")) {
            return REG_S3;
        } else if (register.equalsIgnoreCase("$s4")) {
            return REG_S4;
        } else if (register.equalsIgnoreCase("$s5")) {
            return REG_S5;
        } else if (register.equalsIgnoreCase("$s6")) {
            return REG_S6;
        } else if (register.equalsIgnoreCase("$s7")) { // 23
            return REG_S7;
        } else if (register.equalsIgnoreCase("$t8")) { // 24
            return REG_T8;
        } else if (register.equalsIgnoreCase("$t9")) { // 25
            return REG_T9;
        } else if (register.equalsIgnoreCase("$k0")) { // 26
            return REG_K0;
        } else if (register.equalsIgnoreCase("$k1")) { // 27
            return REG_K1;
        } else if (register.equalsIgnoreCase("gp")) { // 28
            return REG_GP;
        } else if (register.equalsIgnoreCase("sp")) { // 29
            return REG_SP;
        } else if (register.equalsIgnoreCase("fp")) { // 30
            return REG_FP;
        } else if (register.equalsIgnoreCase("ra")) { // 31
            return REG_RA;
        }

        return REG_UNKNOWN;
	}

    @Override
    public int getIndex() {
        return ordinal();
    }

    @Override
    public String toStringAbi() {
        return this.name();
    }

}
