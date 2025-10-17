fail:
fence

main:

        auipc	a0, 0x2             # 0x0002000, expected: a0 = pc + (0x2 << 12)
        addi	a0, a0, 1820                    # 800028bc <_end+0x8bc>

        jal	    a1, next 
next:   sub	    a0, a0, a1

        lui	    t2, 0x2
        addiw	t2, t2, 1808

        bne	    a0, t2, fail

nop
nop
nop




# 0000000080000198 <test_2>:
#    80000198:	00200193          	li	gp,2
#    8000019c:	00000013          	nop

#    800001a0:	00002517          	auipc	a0,0x2   # expected a0 = 0x800001a0 + 0x00002000 = 0x800021a0
#    800001a4:	71c50513          	addi	a0,a0,1820 # 0x800021a0 + 1820 = 800028bc <_end+0x8bc>

#    800001a8:	004005ef          	jal	a1,800001ac <test_2+0x14> # expected a1 = 800001a8 + 4 = 800001ac
#    800001ac:	40b50533          	sub	a0,a0,a1 # 800028bc - 800001ac = 0x2710
#    800001b0:	000023b7          	lui	t2,0x2 # t2 = 0x00002000
#    800001b4:	7103839b          	addiw	t2,t2,1808 # 1808 = 0x710
#    800001b8:	02751463          	bne	a0,t2,800001e0 <fail>