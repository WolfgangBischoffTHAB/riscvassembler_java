some_func:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        lw      a5,-36(s0)
        sw      a5,-20(s0)
        j       .L2
.L3:
        lw      a5,-44(s0)
        lw      a4,0(a5)
        lw      a5,-40(s0)
        add     a4,a4,a5
        lw      a5,-44(s0)
        sw      a4,0(a5)
        lw      a5,-20(s0)
        addi    a5,a5,-1
        sw      a5,-20(s0)
.L2:
        lw      a5,-20(s0)
        bgt     a5,zero,.L3
        nop
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra
__main:
        addi    sp,sp,-32
        sw      ra,28(sp)
        sw      s0,24(sp)
        addi    s0,sp,32
        li      a5,5
        sw      a5,-20(s0)
        li      a5,6
        sw      a5,-24(s0)
        sw      zero,-28(s0)
        addi    a5,s0,-28
        mv      a2,a5
        lw      a1,-24(s0)
        lw      a0,-20(s0)
        call    some_func
        li      a5,0
        mv      a0,a5
        lw      ra,28(sp)
        lw      s0,24(sp)
        addi    sp,sp,32
        jr      ra