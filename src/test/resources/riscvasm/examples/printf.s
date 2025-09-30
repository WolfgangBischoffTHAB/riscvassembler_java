# godbolt
# RISC-V 32 bit, gcc-15.2.0
# -O0

/*
#include <stdio.h>
int main()
{
    printf("Resultant Matrix is:\n");

    return 0;
}
*/

# Explanation
#
# The assembler instruction .string is an alias for .asciz
# .asciz places the ASCII characters in memory consecutively postfixing a zero 
# for zero-termination.
#
# the function puts prints the characters starting at address stored in a0
# to console until the zero terminator is encountered. 

.LC0:
        .string "Resultant Matrix is:"

main:
        addi    sp,sp,-16
        sw      ra,12(sp)
        sw      s0,8(sp)
        addi    s0,sp,16
        lui     a5,%hi(.LC0)
        addi    a0,a5,%lo(.LC0)
        call    puts
        li      a5,0
        mv      a0,a5
        lw      ra,12(sp)
        lw      s0,8(sp)
        addi    sp,sp,16
        jr      ra # jalr x0, 0(x1)

