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