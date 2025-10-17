fail:
fence

main:

    lui	    a1, 0x21212     # a1 / x11 = 0x0000000021212000
    addiw	a1, a1, 289     # a1 / x11 = 0x0000000021212121
#    li	    a2, -63         # a2 / x12 = 0xFFFFFFFFFFFFFFC1
    addi    a2, x0, -63     # a2 / x12 = 0xFFFFFFFFFFFFFFC1
    srl	    a4, a1, a2      # a4 / x14 = 0x0000000000000001

    lui	    t2, 0x10909     # t2 / x7  = 0x0000000010909000
    addiw	t2, t2, 144     # t2 / x7  = 0x0000000010909090

    bne	    a4, t2, fail

    nop
    nop
    nop