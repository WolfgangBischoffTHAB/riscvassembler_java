# godbolt
# RISC-V 32 bit, gcc-15.2.0
# -O0

/*
// C program to multiply two matrices
#include <stdio.h>
#include <stdlib.h>

// matrix dimensions so that we dont have to pass them as
// parametersmat1[R1][C1] and mat2[R2][C2]
#define R1 2 // number of rows in Matrix-1
#define C1 2 // number of columns in Matrix-1
#define R2 2 // number of rows in Matrix-2
#define C2 3 // number of columns in Matrix-2

void multiplyMatrix(int m1[][C1], int m2[][C2])
{
    int result[R1][C2];

    printf("Resultant Matrix is:\n");

    for (int i = 0; i < R1; i++) {
        for (int j = 0; j < C2; j++) {
            result[i][j] = 0;

            for (int k = 0; k < R2; k++) {
                result[i][j] += m1[i][k] * m2[k][j];
            }

            printf("%d\t", result[i][j]);
        }

        printf("\n");
    }
}

// Driver code
int main()
{
    // R1 = 4, C1 = 4 and R2 = 4, C2 = 4 (Update these
    // values in MACROs)
    int m1[R1][C1] = { { 1, 1 }, { 2, 2 } };

    int m2[R2][C2] = { { 1, 1, 1 }, { 2, 2, 2 } };

    // if coloumn of m1 not equal to rows of m2
    if (C1 != R2) {
        printf("The number of columns in Matrix-1 must be "
               "equal to the number of rows in "
               "Matrix-2\n");
        printf("Please update MACROs value according to "
               "your array dimension in "
               "#define section\n");

        exit(EXIT_FAILURE);
    }

    // Function call
    multiplyMatrix(m1, m2);

    return 0;
}
*/
.LC2:
        .string "Resultant Matrix is:"
.LC3:
        .string "%d\t"
multiplyMatrix(int (*) [2], int (*) [3]):
        addi    sp,sp,-80
        sw      ra,76(sp)
        sw      s0,72(sp)
        addi    s0,sp,80
        sw      a0,-68(s0)
        sw      a1,-72(s0)
        lui     a5,%hi(.LC2)
        addi    a0,a5,%lo(.LC2)
        call    puts
        sw      zero,-20(s0)
        j       .L2
.L7:
        sw      zero,-24(s0)
        j       .L3
.L6:
        lw      a4,-20(s0)
        mv      a5,a4
        slli    a5,a5,1
        add     a5,a5,a4
        lw      a4,-24(s0)
        add     a4,a5,a4
        addi    a5,s0,-52
        slli    a4,a4,2
        add     a5,a4,a5
        sw      zero,0(a5)
        sw      zero,-28(s0)
        j       .L4
.L5:
        lw      a4,-20(s0)
        mv      a5,a4
        slli    a5,a5,1
        add     a5,a5,a4
        lw      a4,-24(s0)
        add     a4,a5,a4
        addi    a5,s0,-52
        slli    a4,a4,2
        add     a5,a4,a5
        lw      a3,0(a5)
        lw      a5,-20(s0)
        slli    a5,a5,3
        lw      a4,-68(s0)
        add     a4,a4,a5
        lw      a5,-28(s0)
        slli    a5,a5,2
        add     a5,a4,a5
        lw      a2,0(a5)
        lw      a4,-28(s0)
        mv      a5,a4
        slli    a5,a5,1
        add     a5,a5,a4
        slli    a5,a5,2
        mv      a4,a5
        lw      a5,-72(s0)
        add     a4,a5,a4
        lw      a5,-24(s0)
        slli    a5,a5,2
        add     a5,a4,a5
        lw      a5,0(a5)
        mul     a5,a2,a5
        add     a3,a3,a5
        lw      a4,-20(s0)
        mv      a5,a4
        slli    a5,a5,1
        add     a5,a5,a4
        lw      a4,-24(s0)
        add     a4,a5,a4
        addi    a5,s0,-52
        slli    a4,a4,2
        add     a5,a4,a5
        sw      a3,0(a5)
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
.L4:
        lw      a4,-28(s0)
        li      a5,1
        ble     a4,a5,.L5
        lw      a4,-20(s0)
        mv      a5,a4
        slli    a5,a5,1
        add     a5,a5,a4
        lw      a4,-24(s0)
        add     a4,a5,a4
        addi    a5,s0,-52
        slli    a4,a4,2
        add     a5,a4,a5
        lw      a5,0(a5)
        mv      a1,a5
        lui     a5,%hi(.LC3)
        addi    a0,a5,%lo(.LC3)
        call    printf
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L3:
        lw      a4,-24(s0)
        li      a5,2
        ble     a4,a5,.L6
        li      a0,10
        call    putchar
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L2:
        lw      a4,-20(s0)
        li      a5,1
        ble     a4,a5,.L7
        nop
        nop
        lw      ra,76(sp)
        lw      s0,72(sp)
        addi    sp,sp,80
        jr      ra
.LC0:
        .word   1
        .word   1
        .word   2
        .word   2
.LC1:
        .word   1
        .word   1
        .word   1
        .word   2
        .word   2
        .word   2
main:
        addi    sp,sp,-64
        sw      ra,60(sp)
        sw      s0,56(sp)
        addi    s0,sp,64
        lui     a5,%hi(.LC0)
        addi    a5,a5,%lo(.LC0)
        lw      a2,0(a5)
        lw      a3,4(a5)
        lw      a4,8(a5)
        sw      a2,-32(s0)
        sw      a3,-28(s0)
        sw      a4,-24(s0)
        lw      a5,12(a5)
        sw      a5,-20(s0)
        lui     a5,%hi(.LC1)
        addi    a5,a5,%lo(.LC1)
        lw      a0,0(a5)
        lw      a1,4(a5)
        lw      a2,8(a5)
        lw      a3,12(a5)
        lw      a4,16(a5)
        sw      a0,-56(s0)
        sw      a1,-52(s0)
        sw      a2,-48(s0)
        sw      a3,-44(s0)
        sw      a4,-40(s0)
        lw      a5,20(a5)
        sw      a5,-36(s0)
        addi    a4,s0,-56
        addi    a5,s0,-32
        mv      a1,a4
        mv      a0,a5
        call    multiplyMatrix(int (*) [2], int (*) [3])
        li      a5,0
        mv      a0,a5
        lw      ra,60(sp)
        lw      s0,56(sp)
        addi    sp,sp,64
        jr      ra