# https://riscv.vercel.app/#

main:
addi x10, x0, 10
addi x11, x0, 5
div x5, x10, x11

addi x10, x0, 10
addi x11, x0, -5
div x5, x10, x11