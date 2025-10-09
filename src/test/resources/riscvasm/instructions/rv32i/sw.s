# Synopsis: Store 32 bits of data from register rs2 to an address
# formed by adding rs1 to a signed offset.
#
# Format: sw rs2, imm(rs1)

#sw    a0, -4(s0)

addi  sp, sp, -32
sw    s0, 28(sp)
sw    a0, -4(s0)

#sw   x2, 0x20(x3)

# sw x1, 28(x2)
#sw      ra,28(sp)

#.equ IO_LEDS, 4
#sw   t0, IO_LEDS(gp)
