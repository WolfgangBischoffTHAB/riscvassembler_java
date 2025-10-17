fail:
fence

main:
    li	    a1, 0
    addiw	a2, zero, 1
    slli	a2, a2, 0x20
    addi	a2, a2, -1 # 1ffff <_start-0x7ffe0001>
    sltu	a4, a1, a2
    li	    t2, 1
    bne	    a4, t2, 800006a4 <fail>

    nop
    nop
    nop