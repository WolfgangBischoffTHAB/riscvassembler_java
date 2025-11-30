.LABEL_0:
        .string "test: %d\n"
main:
_start:
        lui     a5, %hi(.LABEL_0)
        addi    a0, a5, %lo(.LABEL_0)
        call    puts
        ret
puts:
        li      a7, 92   # ecall for puts
        ecall
        jr      ra