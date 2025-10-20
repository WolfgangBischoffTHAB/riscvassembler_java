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
# Example: 10 + 9 + 8 + 7 + 6 + 5 + 4 + 3 + 2 + 1 = 55
# 
# $31: Return Address
# $30: Stack Pointer
# $29: Return Value
# $1 : N

_START:
main:
    # set global pointer to somewhere into unused memory
    li      $3, 0x3000          # $3 is gp
    # set input N = 10
	addi    $1, $0, 10
	jal     $31, FAB

    # endless loop
HALT:
	jal     $31, HALT

FAB:
	addi    $3, $31, 0
	jal     $31, PUSH
	addi    $4, $0, 2
	blt     $1, $4, RET_ONE
	add     $3, $0, $1
	jal     $31, PUSH
	addi    $1, $1, -1          # dekrement
	jal     $31, FAB
	jal     $31, POP
	add     $29, $29, $3
	addi    $3, $0, 0
	beq     $3, $0, RET_

    # returns the value 1 (Return value is register $29)
    # this is the abort condition of the recursion
RET_ONE: 
	addi    $29, $0, 1

RET_:
	jal     $31, POP
	add     $31, $0, $3
	jalr    $0, 0($31)

PUSH:
	addi    $30, $30, 4
	sw      $30, 0($3)          # store content of $30 on the stack 
	jalr    $0, 0($31)

POP:
	lw      $3, 0($30)          # from global pointer to $30
	addi    $30, $30, -4
	jalr    $0, 0($31)

# SWAP:
#	add     $3, $0, $1
#	add     $1, $0, $2
#	add     $2, $0, $3
#	jalr    $0, 0($31)