.LC0:
        .string "Matrix"
printf:
        li      a7, 92
        ecall
        jr      ra

puts:
        li      a7, 92
        ecall
        jr      ra

putchar:
        li      a7, 101
        ecall
        jr      ra

memcpy:
        li      a7, 0
        ecall
        jr      ra
_start:
main:
        addi    sp,sp,-16
        sw      ra,12(sp)
        sw      s0,8(sp)
        addi    s0,sp,16
        lui     a5,%hi(.LC0)
        addi    a0,a5,%lo(.LC0)
        call    puts
        li      a5,0
        mv      a0,a5
        lw      ra,12(sp)
        lw      s0,8(sp)
        addi    sp,sp,16
        jr      ra