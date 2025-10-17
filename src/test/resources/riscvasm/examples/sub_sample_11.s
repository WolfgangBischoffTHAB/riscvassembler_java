fail:
fence

main:

    lui	    a1, 0x80000     # a1 / x11 = 0xffffffff80000000
    lui	    a2, 0x8         # a2 / x12 = 0x0000000000008000
    addiw	a2, a2, -1      # a2 / x12 = 0x0000000000007FFF
    sub	    a4, a1, a2      # a4 / x14 = 0xffffffff80000000 - 0x0000000000007FFF 
                            #          = 0xffffffff7FFF8001
    
    lui	    t2, 0xffff0     # t2 / x7  = 0xffffffffffff0000
    addiw	t2, t2, -1      # t2 / x7  = 0xffffffffFFFEFFFF
    slli	t2, t2, 0xf     # t2 / x7  = 0xffffffff7FFF8000
    addi	t2, t2, 1       # t2 / x7  = 0xffffffff7FFF8001
    
    bne	    a4, t2, fail

nop
nop
nop