li t1, 0xFF00F007

#
# https://riscvasm.lucasteske.dev/# encodes it to:
#
# 0:	000ff337          	lui	t1,0xff
# 4:	00f3031b          	addiw	t1,t1,15
# 8:	00c31313          	slli	t1,t1,0xc
# c:	00730313          	addi	t1,t1,7 # ff007 <_sstack+0xee607>
#
# 000ff337
# 00f3031b
# 00c31313
# 00730313


#
# GNU riscv elf 32 bit toolchain encodes (li t1, 0xFF00F007) to
#
# 37F300FF -- FF00F337 -- lui x6, -4081
# 13037300 -- 00730313 -- addi x6, x6, 7
#

#
# 37F000FF -- FF00F037 -- lui x0, -4081
# 13037000 -- 00700313 -- addi x6, x0, 7
#

#
# GNU riscv elf 32 bit toolchain encodes (li t3, 0xFFFFFFF0) to
#
# 130E00FF  --   FF000E13  -- addi x28, x0, -16 # addi performs sign extension before loading the value into x28
#                                               # ADDI adds the sign-extended value 12 bit immediate to the register rs1
#
# My assembler encodes (li t3, 0xFFFFFFF0) to
#
# 37010000  -- lui x2, 0           <---- This needs to be optimized away
# 1B0E01FF  -- addiw x28, x2, -16  <---- This needs to be a RV32I instruction not a 64 bit instruction
#                                        Also, the register x2 has to be replaced by x0
#
# -16 is 0xFFFFFFF0
#
