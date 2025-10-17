fail:
fence

main:

    lui	    a1, 0x80000     # a1 / x11 = 0xffffffff80000000
    li	    a2, 1           # a2 / x12 = 0x0000000000000001
    srl	    a4, a1, a2      # a4 / x14 = 0x7FFFFFFFC0000000

    addiw	t2, zero, 1     # t2 / x7  = 0x01
    slli	t2, t2, 0x21    # t2 / x7  = 0x200000000
    addi	t2, t2, -1      # t2 / x7  = 0x1FFFFFFFF
    slli	t2, t2, 0x1e    # t2 / x7  = 0x7FFFFFFFC0000000
    
    bne	    a4, t2, fail

    nop
    nop
    nop