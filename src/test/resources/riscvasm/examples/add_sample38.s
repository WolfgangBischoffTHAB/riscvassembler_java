fail:
fence

main:
li	    ra, 16              # expected: ra = 16
li	    sp, 30              # expected: sp = 30
add	    zero, ra, sp

li	    t2, 0
bne	    zero, t2, fail

bne	    zero, gp, pass

pass:
nop
nop
nop