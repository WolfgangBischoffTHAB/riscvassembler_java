fail:
fence

main:
lui	    a1, 0x80000     # expected: a1 / x11 = 0x80000000

lui	    a2, 0x8         # expected: a2 / x12 = 0x00008000
addiw	a2, a2, -1      # expected: a2 / x12 = 0000000000007FFF

add	    a4, a1, a2      # expected: a4 / x14 = 0x80007FFF

lui	    t2, 0x80008     # expected: t2 / x7  = 0x80008000
addiw	t2, t2, -1      # expected: t2 / x7  = 0x80007FFF

bne	    a4, t2, fail

nop
nop
nop