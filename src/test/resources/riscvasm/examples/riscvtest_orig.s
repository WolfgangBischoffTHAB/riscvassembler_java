#       RISC-V Assembly         Description               Address   Machine Code
main:   addi x2, x0, 5          # x2 = 5                  00        0x00500113
        addi x3, x0, 12         # x3 = 12                 04        0x00C00193
        addi x7, x3, -9         # x7 = (12 - 9) = 3       08        0xFF718393
        or   x4, x7, x2         # x4 = (3 OR 5) = 7       0C        0x0023E233
        and  x5, x3, x4         # x5 = (12 AND 7) = 4     10        0x0041F2B3
        add  x5, x5, x4         # x5 = (4 + 7) = 11       14        0x004282B3
        beq  x5, x7, end        # shouldn't be taken      18        0x02728863
        slt  x4, x3, x4         # x4 = (12 < 7) = 0       1C        0x0041A233
        beq  x4, x0, around     # should be taken         20        0x00020463
        addi x5, x0, 0          # shouldn't happen        24        0x00000293
around: slt  x4, x7, x2         # x4 = (3 < 5)  = 1       28        0x0023A233
        add  x7, x4, x5         # x7 = (1 + 11) = 12      2C        0x005203B3
        sub  x7, x7, x2         # x7 = (12 - 5) = 7       30        0x402383B3
        sw   x7, 84(x3)         # [96] = 7                34        0x0471AA23
        lw   x2, 96(x0)         # x2 = [96] = 7           38        0x06002103
        add  x9, x2, x5         # x9 = (7 + 11) = 18      3C        0x005104B3
        jal  x3, end            # jump to end, x3 = 0x44  40        0x008001EF
        addi x2, x0, 1          # shouldn't happen        44        0x00100113
end:    add  x2, x2, x9         # x2 = (7 + 18)  = 25     48        0x00910133
        sw   x2, 0x20(x3)       # mem[100] = 25           4C        0x0221A023
done:   beq  x2, x2, done       # infinite loop           50        0x00210063