// package com.mycompany.decoder;

// import java.util.List;

// import com.mycompany.common.ByteArrayUtil;
// import com.mycompany.data.AsmLine;
// import com.mycompany.memory.Memory;

// public class IntegerRawPrintingDecoder implements Decoder<Integer> {

//     public Memory memory;

//     @Override
//     public List<AsmLine<?>> decode(Integer address) {
//         try {
//             int data = memory.readWord(address, Decoder.byteOrder);
//             System.out.println(ByteArrayUtil.byteToHex(address) + ": " + ByteArrayUtil.byteToHex(data) + " (" + data + ")");
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         return null;
//     }

// }
