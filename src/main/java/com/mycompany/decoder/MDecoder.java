package com.mycompany.decoder;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;

/**
 * Decodes https://github.com/riscv/riscv-opcodes/blob/master/extensions/rv_m
 */
public class MDecoder implements Decoder {

    private static final int M_TYPE = 0b0110011;

    @Override
    public AsmLine<?> decode(int data) {
        
        int funct7 = (data >> 25) & 0b1111111;
        if (funct7 != 1) {
            return null;
        }
        int opcode = (data >> 0) & 0b1111111;
        if (opcode != M_TYPE) {
            return null;
        }

        int funct3 = (data >> 12) & 0b111;

        int rd = (data >> 7) & 0b11111;
        int rs1 = (data >> 15) & 0b11111;
        int rs2 = (data >> 20) & 0b11111;

        AsmLine<Register> asmLine = new AsmLine<>();

        switch (funct3) {

            case 0b000:
                asmLine.mnemonic = Mnemonic.I_MUL;
                break;

            case 0b001:
                asmLine.mnemonic = Mnemonic.I_MULH;
                break;

            case 0b010:
                asmLine.mnemonic = Mnemonic.I_MULHSU;
                break;

            case 0b011:
                asmLine.mnemonic = Mnemonic.I_MULHU;
                break;

            case 0b100:
                asmLine.mnemonic = Mnemonic.I_DIV;
                break;
            
            case 0b101:
                asmLine.mnemonic = Mnemonic.I_DIVU;
                break;

            case 0b110:
                asmLine.mnemonic = Mnemonic.I_REM;
                break;

            case 0b111:
                asmLine.mnemonic = Mnemonic.I_REMU;
                break;

            default:
                return null;
        }

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);
        asmLine.register_2 = RISCVRegister.fromInt(rs2);

        return asmLine;
    }
    
}
