fail:
fence

main:

li	    a1, 1           # 
lui	    a2, 0x80000     # expected: a2 / x12 = 0x80000000
addiw	a2, a2, -1      # expected: a2 / x12 = 0x000000007FFFFFFF
add	    a4, a1, a2      # expected: a4 / x14 = 0x80000000

addiw	t2, zero, 1     # expected: t2 / x7  = 0x01
slli	t2, t2, 0x1f    # expected: t2 / x7  = 0x80000000

bne	    a4, t2, fail

nop
nop
nop