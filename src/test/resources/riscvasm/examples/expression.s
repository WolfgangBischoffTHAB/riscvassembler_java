#beq   a0, zero, pc + 16;

#.equ IO_BASE, 0x400000
.equ IO_BASE, 0x0F
beq   a0, zero, IO_BASE + 16;