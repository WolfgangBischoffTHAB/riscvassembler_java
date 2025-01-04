# Test the RISC-V processorinstructions: add, sub, and, or, slt, addi, lw, sw, beq, jal
#       RISC-V Assembly          Description             Address           Machine Code
main:   addi  x2, x0, 5          # initialize x2 = 5     0                 00500113
        addi  x3, x0, 12         # initialize x3 = 12    4                 00c00193
        addi  x7, x3, -9         # initialize x7 = 3     8                 ff718393
        or    x4, x7, x2         # x4 = (3 OR 5) = 7     C                 0023e233
        and   x5, x3, x4         # x5 = (12 AND 7) = 4   10                0041f2b3
        add   x5, x5, x4         # x5 = 4 + 7 = 11       14                004282b3
        beq   x5, x7, end        # shouldn't be taken    18                02728e63
        slt   x4, x3, x4         # x4 = (12 < 7) = 0     1C                0041a233
        beq   x4, x0, around     # should be taken       20                00020463
        addi  x5, x0, 0          # shouldn't happen      24                00000293
around: slt   x4, x7, x2         # x4 = (3 < 5) = 1      28                0023a233
        add   x7, x4, x5         # x7 = 1 + 11 = 12      2C                005203b3
        sub   x7, x7, x2         # x7 = 12 - 5 = 7       30                402383b3
        sw    x7, 68(x3)         # [80] = 7              34                0471a223
        lw    x2, 80(x0)         # x2 = [80] = 7         38                05002103
        jal   x3, end            # jump to end, x3 = 64  3C                018001ef
        addi  x2, x0, 1          # shouldn't happen      40                00100113
        nop                      # don't know why, but   44                00000013
        nop                      # without this nop's    48                00000013
        nop                      # the simulator crashes 4C                00000013
        nop                      # at PC = 50 (here)     50                00000013
end:    add   x2, x2, x3         # x2 = 7 + 64 = 71      54                00310133
        sw    x2, 84(x0)         # write mem[84] = 71    58                04202a23
        xor   x6, x5, x2         # x6 = (11 XOR 71) = 76 5C                0022c333
        addi  x4, x2, -4         # x4 = 71 - 4 = 67      60                ffc10213
        srl   x6, x6, x4         # x6 = 76 >> 67(3) = 9  64                00435333
        sll   x6, x6, x2         # x6 = 9 << 71(7)=1152  68                00231333
        addi  x4, x0, 2          # x4 = 2                6C                00200213
        sra   x6, x6, x4         # x6 = 1152 >>> 2 = 288 70                40435333
        addi  x5, x0, -63        # x5 = -63              74                fc100293
        sll   x4, x5, x7         # x4 = -63 << 7 = -8064 78                00729233
        sra   x4, x4, x5         # x4 = -8064>>> 1=-4032 7C                40525233
        srl   x4, x4, x5         # x4 = 2147481632       80                00525233
        addi  x7, x5, 27         # x7 = -63 + 27 = -36   84                01b28393
        addi  x6, x0, 7          # x6 = 7                88                00700313
        mul   x4, x2, x6         # x4 = 71 * 7 = 497     8C                02610233
        mul   x4, x4, x7         # x4 = 497 * -36=-17892 90                02720233
        mul   x3, x5, x3         # x3 = -63 * 64 = -4032 94                023281b3
        mul   x5, x5, x7         # x5 = -63 * -36 = 2268 98                027282b3
        addi  x5, x0, 7          # x5 = 7                9C                00700293
        bne   x5, x6, nebr       # shouldn't be taken    A0                00629a63
        mul   x3, x3, x4         # x3 = 72140544         A4                024181b3
        addi  x7, x6, 2          # x7 = 7 + 2 = 9        A8                00230393
        bne   x4, x3, nebr       # should be taken       AC                00321463
        addi  x5, x0, 42         # shouldn't happen      B0                02a00293
nebr:   blt   x3, x4, ltbr       # definitely not taken  B4                0041ca63
        sll   x3, x3, x7         # x3 = -1718747136      B8                007191b3
        blt   x5, x6, ltbr       # shouldn't be taken    BC                0062c663
        blt   x3, x4, ltbr       # should be taken       C0                0041c463
        addi  x5, x0, 42         # shouldn't happen      C4                02a00293
ltbr:   bge   x5, x6, eqbr       # should be taken       C8                0062d463
gebr:   sll   x3, x3, x7         # x3 = 469762048        CC                007191b3
eqbr:   addi  x5, x5, 5          # x5 = 7(12)+5=12(17)   D0                00528293
        bge   x4, x3, gebr       # taken only once       D4                fe325ce3
        addi  x2, x0, 5          # x2 = 5                D8                00500113
        xori  x3, x7, -937       # x3 = xor = -930       DC                c573c193
        slli  x3, x3, 3          # x3 = -7440            E0                00319193
        srli  x3, x3, 7          # x3 = 33554373         E4                0071d193
        srai  x3, x3, 4          # x3 = 2097148          E8                4041d193
        xori  x3, x3, -1         # x3 = -2097149         EC                fff1c193
        srai  x3, x3, 2          # x3 = -524288          F0                4021d193
        srli  x3, x3, 1          # x3 = 2147221504       F4                0011d193
        lui   x4, 2              # x4 = 2 * 4096 = 8192  F8                00002237
        auipc x5, 17             # x5 =17*4096+252=69884 FC                00011297
        lui   x2, 1048575        # x2 = -4096 (31-0 > 1) 100               fffff137
        srli  x2, x2, 29         # x2 = -4096 >> 29 = 7  104               01D15113
        xori  x2, x2, 2          # x2 = 7 xor 2 = 5      108               00214113
        jalr  x5, x7, 271        # addr 118, x5 = 272    10C               10f382e7
        lui   x6, 42             # shouldn't happen      110               0002a337
        jalr  x3, x6, -2         # addr 4, x3 = 280      114               ffe301e7
        addi  x7, x7, 5          # x7 = 9 + 5 = 14       118               00538393
        jalr  x0, x5, 7          # jump addr 114 (<=117) 11C               00728067