package com.mycompany.decoder;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

public class Decoder {

    private static final int I_TYPE = 0b1100111;
    private static final int R_TYPE = 0b0110011;

    public static AsmLine decode(final int data) {

        int opcode = data & 0b1111111;
        int funct3 = (data >> 12) & 0b111;
        int funct7 = (data >> 25) & 0b1111111;
        int imm11_0 = (data >> 20) & 0b11111111111111111111;

        int rd = (data >> 7) & 0b11111;
        int rs1 = (data >> 15) & 0b11111;
        int rs2 = (data >> 20) & 0b11111;

        AsmLine asmLine = new AsmLine();

        switch (opcode) {

            case R_TYPE:
                decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                break;

            case I_TYPE:
                decodeIType(asmLine, funct3, funct7, rd, rs1, imm11_0);
                break;
        }

        return asmLine;
    }

    private static void decodeIType(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm11_0) {
        if (funct3 == 0b000) {
            asmLine.mnemonic = Mnemonic.I_JALR;
            asmLine.register_0 = Register.fromInt(rd);
            asmLine.register_1 = Register.fromInt(rs1);
            asmLine.numeric_2 = (long) imm11_0;
        }
    }

    private static void decodeRType(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int rs2) {

        switch (funct3) {

            case 0b000:
                if (funct7 == 0b0000000) {
                    asmLine.mnemonic = Mnemonic.I_ADD;
                    asmLine.register_0 = Register.fromInt(rd);
                    asmLine.register_1 = Register.fromInt(rs1);
                    asmLine.register_2 = Register.fromInt(rs2);
                } else if (funct7 == 0b0100000) {
                    asmLine.mnemonic = Mnemonic.I_SUB;
                }
                break;
        }
    }

}
