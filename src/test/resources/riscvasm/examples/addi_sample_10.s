fail:
fence

main:

lui	    a3, 0x80000     # expected: a3 / x13 = 0xffffffff80000000 # lui result is sign-extended to 64 bit
                        #                    +
addiw	a3, a3, -1      #                      0x00000000ffffffff
                        # ---------------------------------------
                        # expected: a3 / x13 = 0x000000007fffffff
                        #                    + 
addi	a4, a3, 2047    #                      0x00000000000007ff
                        # ---------------------------------------
                        #                      0x00000000800007FE which is then sign extended 
                        #                      (because 32bit instruction results are sign extended in RV64)
                        # expected: a4 / x14 = 0xffffffff800007FE

addiw	t2, zero, 1     # expected: t2 / x7  = 0x0000000000000001
slli	t2, t2, 0x1f    # expected: t2 / x7  = 0xffffffff80000000 # left shited, and result is sign extended
addi	t2, t2, 2046    # expected: t2 / x7  = 0xffffffff800007fe # 32bit result of 0x80000000+2046=800007fe which is then sign extended

bne	    a4, t2, fail

nop
nop
nop