# add          x1, x2, x3
# vle64.v      v0, 0(a0)
# vle64.v      v0, (a0)
# vle64.v      v0, equ_offset(a0)
# vsetvli      t0, x0, e8,m8,tu,mu
# lui          a3, %hi(result)
# addi         a3, a3, %lo(result)
# lui          a5, %hi(uart)

# TODO
lw           a5, %lo(uart)(a5)
# lbu          a4, %lo(LSR.2)(a4)
# lbu          a5, %lo(LSR_RI.1)(a5)

# lui          a4, %hi(LSR.2)
# lui          a5, %hi(LSR_RI.1)
# addi         a0, a0, %lo(.L.str)
# auipc        gp, %pcrel_hi(__global_pointer$)
# lw           t5, -4(t0)
# li           t1, 0xFF00F007
# lw           a1, dword+4
# li           a3, 0x00FF
# call         putchar
# jr           ra
# j            .LBB1_2
# nop