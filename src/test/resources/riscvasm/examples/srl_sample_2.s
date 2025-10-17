fail:
fence

main:

    lui	    a1, 0x80000     # a1 / x11 = 0xffffffff80000000
    li	    a2, 0
    srl	    a4, a1, a2      # a4 / x14 = 0x7FFFFFFFC0000000

    lui	    t2, 0x80000

    bne	    a4, t2, fail

    nop
    nop
    nop