.LC2:
        .string "Matrix"
.LC3:
        .string "A"
.LC4:
        .string "%6d"
.LC5:
        .string "B"
.LC6:
        .string "C"
.LC0:
        .word   9
        .word   0
        .word   9
        .word   4
        .word   2
        .word   6
        .word   6
        .word   7
        .word   9
        .word   3
        .word   8
        .word   1
        .word   6
        .word   9
        .word   7
        .word   1
.LC1:
        .word   1
        .word   2
        .word   4
        .word   2
        .word   8
        .word   6
        .word   0
        .word   0
        .word   7
        .word   6
        .word   8
        .word   5
        .word   8
        .word   4
        .word   7
        .word   5

printf:
        li      a7, 92          ; select function/service
        ecall                   ; perform ecall
        jr      ra

puts:
        li      a7, 92          ; select function/service
        ecall                   ; perform ecall
        jr      ra

putchar:
        li      a7, 101         ; select function/service
        ecall                   ; perform ecall
        jr      ra

memcpy:
        li      a7, 11           ; select function/service
        ecall                   ; perform ecall
        jr      ra

_start:
main:
        addi    sp,sp,-208
        sw      ra,204(sp)
        sw      s0,200(sp)
        addi    s0,sp,208
        lui     a5,%hi(.LC2)
        addi    a0,a5,%lo(.LC2)
        call    puts
        lui     a5,%hi(.LC0)
        addi    a4,a5,%lo(.LC0)
        addi    a5,s0,-80
        mv      a3,a4
        li      a4,64
        mv      a2,a4
        mv      a1,a3
        mv      a0,a5
        call    memcpy
        lui     a5,%hi(.LC1)
        addi    a4,a5,%lo(.LC1)
        addi    a5,s0,-144
        mv      a3,a4
        li      a4,64
        mv      a2,a4
        mv      a1,a3
        mv      a0,a5
        call    memcpy
        sw      zero,-208(s0)
        sw      zero,-204(s0)
        sw      zero,-200(s0)
        sw      zero,-196(s0)
        sw      zero,-192(s0)
        sw      zero,-188(s0)
        sw      zero,-184(s0)
        sw      zero,-180(s0)
        sw      zero,-176(s0)
        sw      zero,-172(s0)
        sw      zero,-168(s0)
        sw      zero,-164(s0)
        sw      zero,-160(s0)
        sw      zero,-156(s0)
        sw      zero,-152(s0)
        sw      zero,-148(s0)
        lui     a5,%hi(.LC3)
        addi    a0,a5,%lo(.LC3)
        call    puts
        addi    a4,s0,-80
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,4
        mv      a0,a4
        call    prettyPrintFormatMatrix
        lui     a5,%hi(.LC5)
        addi    a0,a5,%lo(.LC5)
        call    puts
        addi    a4,s0,-144
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,4
        mv      a0,a4
        call    prettyPrintFormatMatrix
        addi    a2,s0,-208
        addi    a1,s0,-144
        addi    a5,s0,-80
        li      a4,4
        li      a3,4
        mv      a0,a5
        call    standardMatrixMult
        lui     a5,%hi(.LC6)
        addi    a0,a5,%lo(.LC6)
        call    puts
        addi    a4,s0,-208
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,4
        mv      a0,a4
        call    prettyPrintFormatMatrix
        li      a5,0
        mv      a0,a5
        lw      ra,204(sp)
        lw      s0,200(sp)
        addi    sp,sp,208
        jr      ra
matrixAddInto:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      zero,-20(s0)
        j       .L4
.L7:
        sw      zero,-24(s0)
        j       .L5
.L6:
        lw      a4,-20(s0)
        lw      a5,-44(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a3,0(a5)
        lw      a4,-20(s0)
        lw      a5,-44(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-40(s0)
        add     a5,a4,a5
        lw      a4,0(a5)
        lw      a2,-20(s0)
        lw      a5,-44(s0)
        mul     a2,a2,a5
        lw      a5,-24(s0)
        add     a5,a2,a5
        slli    a5,a5,2
        lw      a2,-36(s0)
        add     a5,a2,a5
        add     a4,a3,a4
        sw      a4,0(a5)
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L5:
        lw      a4,-24(s0)
        lw      a5,-48(s0)
        blt     a4,a5,.L6
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L4:
        lw      a4,-20(s0)
        lw      a5,-44(s0)
        blt     a4,a5,.L7
        nop
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra
getSubMatrix:
        addi    sp,sp,-64
        sw      ra,60(sp)
        sw      s0,56(sp)
        addi    s0,sp,64
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      a4,-52(s0)
        sw      a5,-56(s0)
        sw      a6,-60(s0)
        sw      zero,-20(s0)
        lw      a5,-44(s0)
        sw      a5,-24(s0)
        j       .L9
.L12:
        sw      zero,-28(s0)
        lw      a5,-48(s0)
        sw      a5,-32(s0)
        j       .L10
.L11:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        mul     a4,a4,a5
        lw      a5,-32(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a4,a4,a5
        lw      a3,-20(s0)
        lw      a5,-52(s0)
        mul     a3,a3,a5
        lw      a5,-28(s0)
        add     a5,a3,a5
        slli    a5,a5,2
        lw      a3,-60(s0)
        add     a5,a3,a5
        lw      a4,0(a4)
        sw      a4,0(a5)
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
        lw      a5,-32(s0)
        addi    a5,a5,1
        sw      a5,-32(s0)
.L10:
        lw      a4,-48(s0)
        lw      a5,-56(s0)
        add     a5,a4,a5
        lw      a4,-32(s0)
        blt     a4,a5,.L11
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L9:
        lw      a4,-44(s0)
        lw      a5,-52(s0)
        add     a5,a4,a5
        lw      a4,-24(s0)
        blt     a4,a5,.L12
        nop
        nop
        lw      ra,60(sp)
        lw      s0,56(sp)
        addi    sp,sp,64
        jr      ra
setSubMatrix:
        addi    sp,sp,-64
        sw      ra,60(sp)
        sw      s0,56(sp)
        addi    s0,sp,64
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      a4,-52(s0)
        sw      a5,-56(s0)
        sw      a6,-60(s0)
        sw      zero,-20(s0)
        j       .L14
.L17:
        sw      zero,-24(s0)
        j       .L15
.L16:
        lw      a4,-20(s0)
        lw      a5,-52(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-60(s0)
        add     a5,a4,a5
        lw      a5,0(a5)
        sw      a5,-28(s0)
        lw      a4,-48(s0)
        lw      a5,-20(s0)
        add     a4,a4,a5
        lw      a5,-40(s0)
        mul     a4,a4,a5
        lw      a3,-44(s0)
        lw      a5,-24(s0)
        add     a5,a3,a5
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a4,-28(s0)
        sw      a4,0(a5)
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L15:
        lw      a4,-24(s0)
        lw      a5,-52(s0)
        blt     a4,a5,.L16
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L14:
        lw      a4,-20(s0)
        lw      a5,-56(s0)
        blt     a4,a5,.L17
        nop
        nop
        lw      ra,60(sp)
        lw      s0,56(sp)
        addi    sp,sp,64
        jr      ra
.LC7:
        .string "["
.LC8:
        .string "------------------------"
.LC9:
        .string "]"
.LC10:
        .string "%d\n"
segmentedMatrixMult:
        addi    sp,sp,-160
        sw      ra,156(sp)
        sw      s0,152(sp)
        addi    s0,sp,160
        sw      a0,-132(s0)
        sw      a1,-136(s0)
        sw      a2,-140(s0)
        sw      a3,-144(s0)
        sw      a4,-148(s0)
        li      a5,2
        sw      a5,-36(s0)
        lw      a4,-144(s0)
        lw      a5,-36(s0)
        div     a5,a4,a5
        sw      a5,-40(s0)
        li      a5,2
        sw      a5,-44(s0)
        lw      a4,-148(s0)
        lw      a5,-44(s0)
        div     a5,a4,a5
        sw      a5,-48(s0)
        li      a5,2
        sw      a5,-52(s0)
        lw      a4,-144(s0)
        lw      a5,-36(s0)
        div     a5,a4,a5
        sw      a5,-56(s0)
        sw      zero,-20(s0)
        sw      zero,-24(s0)
        j       .L19
.L24:
        sw      zero,-28(s0)
        j       .L20
.L23:
        lw      a4,-28(s0)
        lw      a5,-44(s0)
        mul     a2,a4,a5
        lw      a4,-24(s0)
        lw      a5,-36(s0)
        mul     a3,a4,a5
        addi    a5,s0,-72
        mv      a6,a5
        lw      a5,-36(s0)
        lw      a4,-44(s0)
        li      a1,4
        lw      a0,-136(s0)
        call    getSubMatrix
        sw      zero,-32(s0)
        j       .L21
.L22:
        sw      zero,-88(s0)
        sw      zero,-84(s0)
        sw      zero,-80(s0)
        sw      zero,-76(s0)
        lw      a4,-32(s0)
        lw      a5,-52(s0)
        mul     a2,a4,a5
        lw      a4,-28(s0)
        lw      a5,-44(s0)
        mul     a3,a4,a5
        addi    a5,s0,-88
        mv      a6,a5
        lw      a5,-44(s0)
        lw      a4,-52(s0)
        li      a1,4
        lw      a0,-132(s0)
        call    getSubMatrix
        lui     a5,%hi(.LC7)
        addi    a0,a5,%lo(.LC7)
        call    puts
        addi    a4,s0,-88
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,2
        mv      a0,a4
        call    prettyPrintFormatMatrix
        lui     a5,%hi(.LC8)
        addi    a0,a5,%lo(.LC8)
        call    puts
        addi    a4,s0,-72
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,2
        mv      a0,a4
        call    prettyPrintFormatMatrix
        lui     a5,%hi(.LC9)
        addi    a0,a5,%lo(.LC9)
        call    puts
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
        sw      zero,-104(s0)
        sw      zero,-100(s0)
        sw      zero,-96(s0)
        sw      zero,-92(s0)
        addi    a2,s0,-104
        addi    a1,s0,-72
        addi    a5,s0,-88
        li      a4,2
        li      a3,2
        mv      a0,a5
        call    standardMatrixMult
        sw      zero,-120(s0)
        sw      zero,-116(s0)
        sw      zero,-112(s0)
        sw      zero,-108(s0)
        lw      a4,-32(s0)
        lw      a5,-44(s0)
        mul     a2,a4,a5
        lw      a4,-24(s0)
        lw      a5,-36(s0)
        mul     a3,a4,a5
        addi    a5,s0,-120
        mv      a6,a5
        lw      a5,-44(s0)
        lw      a4,-36(s0)
        li      a1,4
        lw      a0,-140(s0)
        call    getSubMatrix
        addi    a4,s0,-104
        addi    a5,s0,-120
        li      a3,2
        li      a2,2
        mv      a1,a4
        mv      a0,a5
        call    matrixAddInto
        lw      a4,-24(s0)
        lw      a5,-36(s0)
        mul     a2,a4,a5
        lw      a4,-32(s0)
        lw      a5,-44(s0)
        mul     a3,a4,a5
        addi    a5,s0,-120
        mv      a6,a5
        lw      a5,-44(s0)
        lw      a4,-36(s0)
        li      a1,4
        lw      a0,-140(s0)
        call    setSubMatrix
        lw      a5,-32(s0)
        addi    a5,a5,1
        sw      a5,-32(s0)
.L21:
        lw      a4,-32(s0)
        lw      a5,-56(s0)
        blt     a4,a5,.L22
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
.L20:
        lw      a4,-28(s0)
        lw      a5,-48(s0)
        blt     a4,a5,.L23
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L19:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L24
        lw      a1,-20(s0)
        lui     a5,%hi(.LC10)
        addi    a0,a5,%lo(.LC10)
        call    printf
        nop
        lw      ra,156(sp)
        lw      s0,152(sp)
        addi    sp,sp,160
        jr      ra
standardMatrixMult:
        addi    sp,sp,-64
        sw      ra,60(sp)
        sw      s0,56(sp)
        addi    s0,sp,64
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      a4,-52(s0)
        sw      zero,-20(s0)
        j       .L26
.L31:
        sw      zero,-24(s0)
        j       .L27
.L30:
        sw      zero,-28(s0)
        j       .L28
.L29:
        lw      a4,-20(s0)
        lw      a5,-48(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-44(s0)
        add     a5,a4,a5
        lw      a3,0(a5)
        lw      a4,-20(s0)
        lw      a5,-52(s0)
        mul     a4,a4,a5
        lw      a5,-28(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a4,0(a5)
        lw      a2,-28(s0)
        lw      a5,-52(s0)
        mul     a2,a2,a5
        lw      a5,-24(s0)
        add     a5,a2,a5
        slli    a5,a5,2
        lw      a2,-40(s0)
        add     a5,a2,a5
        lw      a5,0(a5)
        mul     a4,a4,a5
        lw      a2,-20(s0)
        lw      a5,-48(s0)
        mul     a2,a2,a5
        lw      a5,-24(s0)
        add     a5,a2,a5
        slli    a5,a5,2
        lw      a2,-44(s0)
        add     a5,a2,a5
        add     a4,a3,a4
        sw      a4,0(a5)
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
.L28:
        lw      a4,-28(s0)
        lw      a5,-52(s0)
        blt     a4,a5,.L29
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L27:
        lw      a4,-24(s0)
        lw      a5,-52(s0)
        blt     a4,a5,.L30
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L26:
        lw      a4,-20(s0)
        lw      a5,-48(s0)
        blt     a4,a5,.L31
        nop
        nop
        lw      ra,60(sp)
        lw      s0,56(sp)
        addi    sp,sp,64
        jr      ra
prettyPrintFormatMatrix:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      zero,-20(s0)
        j       .L33
.L36:
        sw      zero,-24(s0)
        j       .L34
.L35:
        lw      a4,-40(s0)
        lw      a5,-20(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a5,0(a5)
        mv      a1,a5
        lw      a0,-44(s0)
        call    printf
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L34:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L35
        li      a0,10
        call    putchar
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L33:
        lw      a4,-20(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L36
        nop
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra
upCountingMatrix:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        li      a5,1
        sw      a5,-20(s0)
        sw      zero,-24(s0)
        j       .L38
.L41:
        sw      zero,-28(s0)
        j       .L39
.L40:
        lw      a4,-40(s0)
        lw      a5,-24(s0)
        mul     a4,a4,a5
        lw      a5,-28(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a4,-20(s0)
        sw      a4,0(a5)
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
.L39:
        lw      a4,-28(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L40
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L38:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L41
        nop
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra