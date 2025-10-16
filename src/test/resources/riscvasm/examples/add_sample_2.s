fail:
fence
main:
li	    gp, 21
li	    tp, 0
test_21_8:
li	    ra, 14
li	    sp, 11
add	    a4, ra, sp
nop
mv	    t1, a4
addi	tp, tp, 1
li	    t0, 2
bne	    tp, t0, test_21_8
li	    t2, 25
fence
bne	    t1, t2, fail
nop
nop
nop
