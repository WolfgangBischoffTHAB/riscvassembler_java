.LABEL_0:
        .string "test_string_test\n"
main:
_start:
        addi    sp, sp, -16
        # variable 'length'
        li      t0, 10
        sw      t0, 16(sp)
        # variable 'i'
        li      t0, 0
        sw      t0, 8(sp)
start_0:
        lw      t0, 16(sp)
        lw      t1, 8(sp)
        ble     t0, t1, break_label_0
        lui     a5, %hi(.LABEL_0)
        addi    a0, a5, %lo(.LABEL_0)
        call    puts
continue_label_0:
        lw      t0, 8(sp)
        addi    a5, t0, 1
        # variable 'tmp.1.0'
        mv      t0, a5
        sw      t0, 4(sp)
        lw      a5, 4(sp)
        # variable 'i'
        mv      t0, a5
        sw      t0, 8(sp)
        j       start_0
break_label_0:
        call    exit
        # <processReturn()>
        ret
exit:
        li      a7, 93   # ecall for exit
        ecall
        jr      ra
puts:
        li      a7, 92   # ecall for puts
        ecall
        jr      ra
