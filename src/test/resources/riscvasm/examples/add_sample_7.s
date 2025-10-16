fail:
fence

main:
lui	    a1, 0x80000     # expected: a1 / x11 = 0x80000000
lui	    a2, 0xffff8     # expected: a2 / x12 = 0xffff8000 
add	    a4, a1, a2      # expected: a4 / x14 = 0x7FFF8000 = 2147450880

lui	    t2, 0xffff0     # expected: t2 / x7  = 0xffff0000 
addiw	t2, t2, -1      # expected: t2 / x7  = 0x00000000FFFEFFFF 
slli	t2, t2, 0xf     # expected: t2 / x7  = 0x7FFF8000 = 2147450880

bne	    a4, t2, fail

nop
nop
nop