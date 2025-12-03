_start:
# lui       t0, 32              # Load upper immediate sets the upper 20 bits of a register with an immediate value and zeros the lower 12 bits.
# addi	    t0, t0, -12

li      t0, 131060     


# addi    t0, x0, -12    # ff400293 == addi t0, x0, -12



/*
0000 0000 0000 0010 0000b == 20h == 32
1111 .. 1111 0100b == FFFFFFFFFFFFFFF4h == -12


[0000 0000 0000 0010 0000] [0000 0000 0000] == 32 << 12 == 131072 == 20000h

[0000 0000 0000 0001 1111] [1111 1111 0100]   --   -12

131072 - 12 = 131060


0001 1111 1111 1111 0100
*/