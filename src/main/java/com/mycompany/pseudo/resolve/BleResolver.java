package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * BLE is a pseudo instruction!
 *
 * <pre>
 * ble     t1, t0, break_label_2
 * </pre>
 *
 * is resolved by BGE and switching around the registers.
 */
public class BleResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        for (AsmLine<RISCVRegister> asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_BLE) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_BGE;
            RISCVRegister temp = asmLine.register_0;
            asmLine.register_0 = asmLine.register_1;
            asmLine.register_1 = temp;
        }
    }

}