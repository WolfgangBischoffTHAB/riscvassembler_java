# https://github.com/metastableB/RISCV-RV32I-Assembler/blob/master/examples/recursive_sum_of_n.rvi
#
# NOTE: This source code is created for a Harvard architecture where instructions and data are separate
# and the instructions sw and lw do not write into the address space / memory area that stores
# the instructions but write into a separate memory sections that only stores data.
#
# NOTE: Even when using the Harvard architecture I think this code does not compute the correct result.
#
# @author:Don Dennis
# recursive_sum_of_n.rvi
#
# Finds sum of the first N 
# numbers recursively. Demonstrates
# how recursive functions can be added.
# 
# x31: Return Address
# x30: Stack Pointer
# x29: Return Value
# x1 : N

_START:
main:
	addi x1,x0,10
	jal x31, FAB
HALT:
	jal x31, HALT

FAB:
	addi x3, x31, 0
	jal x31, PUSH
	addi x4, x0, 2
	blt x1, x4, RET_ONE
	add x3, x0, x1
	jal x31, PUSH
	addi x1, x1, -1
	jal x31, FAB
	jal x31, POP
	add x29, x29, x3
	addi x3, x0, 0
	beq x3, x0, RET_
RET_ONE: 
	addi x29, x0, 1
RET_:
	jal x31, POP
	add x31, x0, x3
	jalr x0, 0(x31)

PUSH:
	addi x30,x30,4
	sw   x30,0(x3)
#	sd   x30,0(x3) 
	jalr x0,0(x31) 
POP:
	lw x3, 0(x30)
#	ld x3, 0(x30)
	addi x30,x30,-4
	jalr x0,0(x31)	

SWAP:
	add x3,x0,x1
	add x1,x0,x2
	add x2,x0,x3
	jalr x0,0(x31)

	