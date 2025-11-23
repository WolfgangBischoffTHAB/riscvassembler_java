.LC3:
        /**/.string "A"
.LC4:
        /**/.string "%6d"
.LC5:
        /**/.string "B"
.LC6:
        /**/.string "C"
.LC0:
        /**/
        .word   9
        .word   0
        .word   9
        .word   4
        .word   2
        .word   6
        .word   6
        .word   7
        .word   9
        .word   3
        .word   8
        .word   1
        .word   6
        .word   9
        .word   7
        .word   1
        
.LC1:
/**/
        .word   1
        .word   2
        .word   4
        .word   2
        .word   8
        .word   6
        .word   0
        .word   0
        .word   7
        .word   6
        .word   8
        .word   5
        .word   8
        .word   4
        .word   7
        .word   5
        
.LC2:
/**/
        .word   1
        .word   2
        .word   3
        .word   4
        

puts:
        jr      ra

memcpy:
        jr      ra

printf:
        jr      ra

putchar:
        jr      ra
_start:
main:
        addi    sp,sp,-240
        sw      ra,236(sp)
        sw      s0,232(sp)
        addi    s0,sp,240
        lui     a5,%hi(.LC0)
        addi    a4,a5,%lo(.LC0)
        addi    a5,s0,-80
        mv      a3,a4
        li      a4,64
        mv      a2,a4
        mv      a1,a3
        mv      a0,a5
        call    memcpy
        lui     a5,%hi(.LC1)
        addi    a4,a5,%lo(.LC1)
        addi    a5,s0,-144
        mv      a3,a4
        li      a4,64
        mv      a2,a4
        mv      a1,a3
        mv      a0,a5
        call    memcpy
        sw      zero,-208(s0)
        sw      zero,-204(s0)
        sw      zero,-200(s0)
        sw      zero,-196(s0)
        sw      zero,-192(s0)
        sw      zero,-188(s0)
        sw      zero,-184(s0)
        sw      zero,-180(s0)
        sw      zero,-176(s0)
        sw      zero,-172(s0)
        sw      zero,-168(s0)
        sw      zero,-164(s0)
        sw      zero,-160(s0)
        sw      zero,-156(s0)
        sw      zero,-152(s0)
        sw      zero,-148(s0)
        sw      zero,-224(s0)
        sw      zero,-220(s0)
        sw      zero,-216(s0)
        sw      zero,-212(s0)
        lui     a5,%hi(.LC2)
        addi    a5,a5,%lo(.LC2)
        lw      a2,0(a5)
        lw      a3,4(a5)
        lw      a4,8(a5)
        sw      a2,-240(s0)
        sw      a3,-236(s0)
        sw      a4,-232(s0)
        lw      a5,12(a5)
        sw      a5,-228(s0)
        addi    a5,s0,-80
        li      a1,4
        mv      a0,a5
        call    upCountingMatrix
        addi    a5,s0,-144
        li      a1,4
        mv      a0,a5
        call    upCountingMatrix
        addi    a5,s0,-240
        addi    a0,s0,-208
        mv      a6,a5
        li      a5,2
        li      a4,2
        li      a3,0
        li      a2,0
        li      a1,4
        call    setSubMatrix
        lui     a5,%hi(.LC3)
        addi    a0,a5,%lo(.LC3)
        call    puts
        addi    a4,s0,-80
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,4
        mv      a0,a4
        call    prettyPrintFormatMatrix
        lui     a5,%hi(.LC5)
        addi    a0,a5,%lo(.LC5)
        call    puts
        addi    a4,s0,-144
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,4
        mv      a0,a4
        call    prettyPrintFormatMatrix
        addi    a2,s0,-208
        addi    a1,s0,-144
        addi    a5,s0,-80
        li      a4,4
        li      a3,4
        mv      a0,a5
        call    segmentedMatrixMult
        lui     a5,%hi(.LC6)
        addi    a0,a5,%lo(.LC6)
        call    puts
        addi    a4,s0,-208
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,4
        mv      a0,a4
        call    prettyPrintFormatMatrix
        li      a5,0
        mv      a0,a5
        lw      ra,236(sp)
        lw      s0,232(sp)
        addi    sp,sp,240
        jr      ra
matrixAddInto:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      zero,-20(s0)
        j       .L4
.L7:
        sw      zero,-24(s0)
        j       .L5
.L6:
        lw      a4,-20(s0)
        lw      a5,-44(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a3,0(a5)
        lw      a4,-20(s0)
        lw      a5,-44(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-40(s0)
        add     a5,a4,a5
        lw      a4,0(a5)
        lw      a2,-20(s0)
        lw      a5,-44(s0)
        mul     a2,a2,a5
        lw      a5,-24(s0)
        add     a5,a2,a5
        slli    a5,a5,2
        lw      a2,-36(s0)
        add     a5,a2,a5
        add     a4,a3,a4
        sw      a4,0(a5)
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L5:
        lw      a4,-24(s0)
        lw      a5,-48(s0)
        blt     a4,a5,.L6
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L4:
        lw      a4,-20(s0)
        lw      a5,-44(s0)
        blt     a4,a5,.L7
        nop
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra
getSubMatrix:
        addi    sp,sp,-64
        sw      ra,60(sp)
        sw      s0,56(sp)
        addi    s0,sp,64
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      a4,-52(s0)
        sw      a5,-56(s0)
        sw      a6,-60(s0)
        sw      zero,-20(s0)
        lw      a5,-44(s0)
        sw      a5,-24(s0)
        j       .L9
.L12:
        sw      zero,-28(s0)
        lw      a5,-48(s0)
        sw      a5,-32(s0)
        j       .L10
.L11:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        mul     a4,a4,a5
        lw      a5,-32(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a4,a4,a5
        lw      a3,-20(s0)
        lw      a5,-52(s0)
        mul     a3,a3,a5
        lw      a5,-28(s0)
        add     a5,a3,a5
        slli    a5,a5,2
        lw      a3,-60(s0)
        add     a5,a3,a5
        lw      a4,0(a4)
        sw      a4,0(a5)
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
        lw      a5,-32(s0)
        addi    a5,a5,1
        sw      a5,-32(s0)
.L10:
        lw      a4,-48(s0)
        lw      a5,-56(s0)
        add     a5,a4,a5
        lw      a4,-32(s0)
        blt     a4,a5,.L11
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L9:
        lw      a4,-44(s0)
        lw      a5,-52(s0)
        add     a5,a4,a5
        lw      a4,-24(s0)
        blt     a4,a5,.L12
        nop
        nop
        lw      ra,60(sp)
        lw      s0,56(sp)
        addi    sp,sp,64
        jr      ra
setSubMatrix:
        addi    sp,sp,-64
        sw      ra,60(sp)
        sw      s0,56(sp)
        addi    s0,sp,64
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      a4,-52(s0)
        sw      a5,-56(s0)
        sw      a6,-60(s0)
        sw      zero,-20(s0)
        j       .L14
.L17:
        sw      zero,-24(s0)
        j       .L15
.L16:
        lw      a4,-20(s0)
        lw      a5,-52(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-60(s0)
        add     a5,a4,a5
        lw      a5,0(a5)
        sw      a5,-28(s0)
        lw      a4,-48(s0)
        lw      a5,-20(s0)
        add     a4,a4,a5
        lw      a5,-40(s0)
        mul     a4,a4,a5
        lw      a3,-44(s0)
        lw      a5,-24(s0)
        add     a5,a3,a5
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a4,-28(s0)
        sw      a4,0(a5)
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L15:
        lw      a4,-24(s0)
        lw      a5,-52(s0)
        blt     a4,a5,.L16
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L14:
        lw      a4,-20(s0)
        lw      a5,-56(s0)
        blt     a4,a5,.L17
        nop
        nop
        lw      ra,60(sp)
        lw      s0,56(sp)
        addi    sp,sp,64
        jr      ra
.LC7:
        /**/.string "["
.LC8:
        /**/.string "---------------------------"
.LC9:
        /**/.string "]"
.LC10:
        /**/.string "%d\n"
segmentedMatrixMult:
        addi    sp,sp,-160
        sw      ra,156(sp)
        sw      s0,152(sp)
        addi    s0,sp,160
        sw      a0,-132(s0)
        sw      a1,-136(s0)
        sw      a2,-140(s0)
        sw      a3,-144(s0)
        sw      a4,-148(s0)
        li      a5,2
        sw      a5,-36(s0)
        lw      a4,-144(s0)
        lw      a5,-36(s0)
        div     a5,a4,a5
        sw      a5,-40(s0)
        li      a5,2
        sw      a5,-44(s0)
        lw      a4,-148(s0)
        lw      a5,-44(s0)
        div     a5,a4,a5
        sw      a5,-48(s0)
        li      a5,2
        sw      a5,-52(s0)
        lw      a4,-144(s0)
        lw      a5,-36(s0)
        div     a5,a4,a5
        sw      a5,-56(s0)
        sw      zero,-20(s0)
        sw      zero,-24(s0)
        j       .L19
.L24:
        sw      zero,-28(s0)
        j       .L20
.L23:
        lw      a4,-28(s0)
        lw      a5,-44(s0)
        mul     a2,a4,a5
        lw      a4,-24(s0)
        lw      a5,-36(s0)
        mul     a3,a4,a5
        addi    a5,s0,-72
        mv      a6,a5
        lw      a5,-36(s0)
        lw      a4,-44(s0)
        li      a1,4
        lw      a0,-136(s0)
        call    getSubMatrix
        sw      zero,-32(s0)
        j       .L21
.L22:
        sw      zero,-88(s0)
        sw      zero,-84(s0)
        sw      zero,-80(s0)
        sw      zero,-76(s0)
        lw      a4,-32(s0)
        lw      a5,-52(s0)
        mul     a2,a4,a5
        lw      a4,-28(s0)
        lw      a5,-44(s0)
        mul     a3,a4,a5
        addi    a5,s0,-88
        mv      a6,a5
        lw      a5,-44(s0)
        lw      a4,-52(s0)
        li      a1,4
        lw      a0,-132(s0)
        call    getSubMatrix
        lui     a5,%hi(.LC7)
        addi    a0,a5,%lo(.LC7)
        call    puts
        addi    a4,s0,-88
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,2
        mv      a0,a4
        call    prettyPrintFormatMatrix
        lui     a5,%hi(.LC8)
        addi    a0,a5,%lo(.LC8)
        call    puts
        addi    a4,s0,-72
        lui     a5,%hi(.LC4)
        addi    a2,a5,%lo(.LC4)
        li      a1,2
        mv      a0,a4
        call    prettyPrintFormatMatrix
        lui     a5,%hi(.LC9)
        addi    a0,a5,%lo(.LC9)
        call    puts
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
        sw      zero,-104(s0)
        sw      zero,-100(s0)
        sw      zero,-96(s0)
        sw      zero,-92(s0)
        addi    a2,s0,-104
        addi    a1,s0,-72
        addi    a5,s0,-88
        li      a4,2
        li      a3,2
        mv      a0,a5
        call    standardMatrixMult
        sw      zero,-120(s0)
        sw      zero,-116(s0)
        sw      zero,-112(s0)
        sw      zero,-108(s0)
        lw      a4,-32(s0)
        lw      a5,-44(s0)
        mul     a2,a4,a5
        lw      a4,-24(s0)
        lw      a5,-36(s0)
        mul     a3,a4,a5
        addi    a5,s0,-120
        mv      a6,a5
        lw      a5,-44(s0)
        lw      a4,-36(s0)
        li      a1,4
        lw      a0,-140(s0)
        call    getSubMatrix
        addi    a4,s0,-104
        addi    a5,s0,-120
        li      a3,2
        li      a2,2
        mv      a1,a4
        mv      a0,a5
        call    matrixAddInto
        lw      a4,-24(s0)
        lw      a5,-36(s0)
        mul     a2,a4,a5
        lw      a4,-32(s0)
        lw      a5,-44(s0)
        mul     a3,a4,a5
        addi    a5,s0,-120
        mv      a6,a5
        lw      a5,-44(s0)
        lw      a4,-36(s0)
        li      a1,4
        lw      a0,-140(s0)
        call    setSubMatrix
        lw      a5,-32(s0)
        addi    a5,a5,1
        sw      a5,-32(s0)
.L21:
        lw      a4,-32(s0)
        lw      a5,-56(s0)
        blt     a4,a5,.L22
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
.L20:
        lw      a4,-28(s0)
        lw      a5,-48(s0)
        blt     a4,a5,.L23
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L19:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L24
        lw      a1,-20(s0)
        lui     a5,%hi(.LC10)
        addi    a0,a5,%lo(.LC10)
        call    printf
        nop
        lw      ra,156(sp)
        lw      s0,152(sp)
        addi    sp,sp,160
        jr      ra
standardMatrixMult:
        addi    sp,sp,-64
        sw      ra,60(sp)
        sw      s0,56(sp)
        addi    s0,sp,64
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      a3,-48(s0)
        sw      a4,-52(s0)
        sw      zero,-20(s0)
        j       .L26
.L31:
        sw      zero,-24(s0)
        j       .L27
.L30:
        sw      zero,-28(s0)
        j       .L28
.L29:
        lw      a4,-20(s0)
        lw      a5,-48(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-44(s0)
        add     a5,a4,a5
        lw      a3,0(a5)
        lw      a4,-20(s0)
        lw      a5,-52(s0)
        mul     a4,a4,a5
        lw      a5,-28(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a4,0(a5)
        lw      a2,-28(s0)
        lw      a5,-52(s0)
        mul     a2,a2,a5
        lw      a5,-24(s0)
        add     a5,a2,a5
        slli    a5,a5,2
        lw      a2,-40(s0)
        add     a5,a2,a5
        lw      a5,0(a5)
        mul     a4,a4,a5
        lw      a2,-20(s0)
        lw      a5,-48(s0)
        mul     a2,a2,a5
        lw      a5,-24(s0)
        add     a5,a2,a5
        slli    a5,a5,2
        lw      a2,-44(s0)
        add     a5,a2,a5
        add     a4,a3,a4
        sw      a4,0(a5)
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
.L28:
        lw      a4,-28(s0)
        lw      a5,-52(s0)
        blt     a4,a5,.L29
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L27:
        lw      a4,-24(s0)
        lw      a5,-52(s0)
        blt     a4,a5,.L30
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L26:
        lw      a4,-20(s0)
        lw      a5,-48(s0)
        blt     a4,a5,.L31
        nop
        nop
        lw      ra,60(sp)
        lw      s0,56(sp)
        addi    sp,sp,64
        jr      ra
prettyPrintFormatMatrix:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        sw      zero,-20(s0)
        j       .L33
.L36:
        sw      zero,-24(s0)
        j       .L34
.L35:
        lw      a4,-40(s0)
        lw      a5,-20(s0)
        mul     a4,a4,a5
        lw      a5,-24(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a5,0(a5)
        mv      a1,a5
        lw      a0,-44(s0)
        call    printf
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L34:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L35
        li      a0,10
        call    putchar
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L33:
        lw      a4,-20(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L36
        nop
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra
upCountingMatrix:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        li      a5,1
        sw      a5,-20(s0)
        sw      zero,-24(s0)
        j       .L38
.L41:
        sw      zero,-28(s0)
        j       .L39
.L40:
        lw      a4,-40(s0)
        lw      a5,-24(s0)
        mul     a4,a4,a5
        lw      a5,-28(s0)
        add     a5,a4,a5
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a4,-20(s0)
        sw      a4,0(a5)
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
        lw      a5,-28(s0)
        addi    a5,a5,1
        sw      a5,-28(s0)
.L39:
        lw      a4,-28(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L40
        lw      a5,-24(s0)
        addi    a5,a5,1
        sw      a5,-24(s0)
.L38:
        lw      a4,-24(s0)
        lw      a5,-40(s0)
        blt     a4,a5,.L41
        nop
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra

/*
// Msys2: install gcc: 
// pacman -S mingw-w64-ucrt-x86_64-gcc
// gcc.exe is installed to C:\msys64\mingw64\bin
// Add it to the path environment variable
// set PATH=%PATH%;C:\msys64\mingw64\bin\
//
// Compile an application:
// gcc main.c

#include <stdio.h>

#define DIMENSION 4
#define ELEMENTS DIMENSION*DIMENSION

// void printMatrix(int* matrix, int dim);
void prettyPrintFormatMatrix(int* matrix, int dim, const char* format);
// void identityMatrix(int* matrix, int dim);
// void zeroMatrix(int* matrix, int dim);
void upCountingMatrix(int* matrix, int dim);

void standardMatrixMult(int* matrixA, int* matrixB, int* matrixC, int rows, int columns);
void segmentedMatrixMult(int* matrixA, int* matrixB, int* matrixC, int rows, int columns);
void matrixAddInto(int* matrixA, int* matrixB, int rows, int columns);
void getSubMatrix(int* matrix, int dim, int xPos, int yPos, int width, int height, int* matrixC);
void setSubMatrix(int* matrix, int dim, int xPos, int yPos, int width, int height, int* matrixC);

int main()
{
    // printf("Matrix\n");

    int matrixA[ELEMENTS] = {
        9, 0, 9, 4,
        2, 6, 6, 7,
        9, 3, 8, 1,
        6, 9, 7, 1
    };
    int matrixB[ELEMENTS] = {
        1, 2, 4, 2,
        8, 6, 0, 0,
        7, 6, 8, 5,
        8, 4, 7, 5
    };
    int matrixC[ELEMENTS] = {
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0
    };

    int sub[4] = {
        0, 0, 
        0, 0
    };
    int subSetTest[4] = {
        1, 2, 
        3, 4
    };

    upCountingMatrix(matrixA, DIMENSION);
    upCountingMatrix(matrixB, DIMENSION);

    setSubMatrix(matrixC, DIMENSION, 0, 0, 2, 2, subSetTest);
    
    printf("A\n");
    prettyPrintFormatMatrix(matrixA, DIMENSION, "%6d");
    printf("B\n");
    prettyPrintFormatMatrix(matrixB, DIMENSION, "%6d");

    // //standardMatrixMult(matrixA, matrixB, matrixC, DIMENSION, DIMENSION);
    segmentedMatrixMult(matrixA, matrixB, matrixC, DIMENSION, DIMENSION);

    printf("C\n");
    prettyPrintFormatMatrix(matrixC, DIMENSION, "%6d");

    // int[] intArray = new int[] {
    //             104, 88, 136, 83,
    //             148, 104, 105, 69,
    //             97, 88, 107, 63,
    //             135, 112, 87, 52
    //     };
    
    return 0;
}

void matrixAddInto(int* matrixA, int* matrixB, int rows, int columns) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            matrixA[i * rows + j] += matrixB[i * rows + j];
        }
    }
}

void getSubMatrix(int* matrix, int dim, int xPos, int yPos, int width, int height, int* matrixC) {
    int innerI = 0;
    for (int i = xPos; i < xPos + width; i++) {
        int innerJ = 0;
        for (int j = yPos; j < yPos + height; j++) {
            matrixC[innerI * width + innerJ] = matrix[i * dim + j];
            innerJ++;
        }
        innerI++;
    }
}

void setSubMatrix(int* matrix, int dim, int xPos, int yPos, int width, int height, int* matrixC) {
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            int tempData = matrixC[i * width + j];
            matrix[(yPos + i) * dim + (xPos + j)] = tempData;
        }
    }
}

void segmentedMatrixMult(int* matrixA, int* matrixB, int* matrixC, int rows, int columns) {
    
    int nc = 2; // subset size
    int rowSteps = rows / nc;

    int kc = 2; // subset size
    int columnsSteps = columns / kc;

    int mc = 2; // subset size
    int innerSteps = rows / nc;

    //
    // ACT
    //

    // DEBUG
    int iterationCounter = 0;

    // for jc = 0 to n-1 step nc
    // Loop 1
    for (int jc = 0; jc < rowSteps; jc++) {

        // for pc = 0 to k-1 step kc
        // Loop 2
        for (int pc = 0; pc < columnsSteps; pc++) {

            //int* subMatrixB = subMatrix(matrixB, pc * kc, jc * nc, kc, nc);
            int subMatrixB[2*2];
            getSubMatrix(matrixB, DIMENSION, pc * kc, jc * nc, kc, nc, subMatrixB);

            // for ic = 0 to m-1 step mc
            // Loop 3
            for (int ic = 0; ic < innerSteps; ic++) {

                //Matrix subMatrixA = matrixA.getSubMatrix(ic * mc, pc * kc, mc, kc);
                int subMatrixA[2*2] = { 0, 0, 0, 0 };
                getSubMatrix(matrixA, DIMENSION, ic * mc, pc * kc, mc, kc, subMatrixA);

                printf("[\n");
                prettyPrintFormatMatrix(subMatrixA, 2, "%6d");
                printf("------------------------\n");
                prettyPrintFormatMatrix(subMatrixB, 2, "%6d");
                printf("]\n");

                iterationCounter++;                

                // perform a matrix mult of the sub matrices
                //Matrix temp = subMatrixA.mult(subMatrixB);
                int tempMult[2*2] = { 0, 0, 0, 0 };
                standardMatrixMult(subMatrixA, subMatrixB, tempMult, 2, 2);

                //printf("+++++++++++++++++++++++++++++\n");
                //prettyPrintFormatMatrix(tempMult, 2, "%6d");
                //printf("+++++++++++++++++++++++++++++\n");

                // // retrieve sub matrix to accumulate into
                // Matrix accumulatorSubMatrixC = matrixC.getSubMatrix(ic * kc, jc * nc, nc, kc);
                int tempAccum[2*2] = { 0, 0, 0, 0 };
                getSubMatrix(matrixC, DIMENSION, ic * kc, jc * nc, nc, kc, tempAccum);

                // // accumulate the result
                // accumulatorSubMatrixC.add(temp);
                matrixAddInto(tempAccum, tempMult, 2, 2);

                // // place the accumulator back into the large result matrix
                // matrixC.setSubMatrix(jc * nc, ic * kc, nc, kc, accumulatorSubMatrixC);
                setSubMatrix(matrixC, DIMENSION, jc * nc, ic * kc, nc, kc, tempAccum);

            }
        }
    }

    printf("%d\n", iterationCounter);
}

void standardMatrixMult(int* matrixA, int* matrixB, int* matrixC, int rows, int columns) {
    // over row of matrix B
    for (int i = 0; i < rows; i++) {
        // over column of matrix A
        for (int j = 0; j < columns; j++) {
            // fuse row and column together into a single cell of matrix C
            for (int k = 0; k < columns; k++) {
                matrixC[i * rows + j] += matrixA[i * columns + k] * matrixB[k * columns + j];
            }
        }
    }
}

// void printMatrix(int* matrix, int dim) {
//     for (int i = 0; i < dim; i++) {
//         for (int j = 0; j < dim; j++) {
//             printf("%d ", matrix[dim*i + j]);
//         }
//         printf("\n");
//     }
// }

void prettyPrintFormatMatrix(int* matrix, int dim, const char* format) {
    for (int i = 0; i < dim; i++) {
        for (int j = 0; j < dim; j++) {
            printf(format, matrix[dim*i + j]);
        }
        printf("\n");
    }
}

// void identityMatrix(int* matrix, int dim) {
//     for (int i = 0; i < dim; i++) {
//         for (int j = 0; j < dim; j++) {
//             matrix[dim*i + j] = (i==j) ? 1 : 0;
//         }
//     }
// }

// void zeroMatrix(int* matrix, int dim) {
//     for (int i = 0; i < dim; i++) {
//         for (int j = 0; j < dim; j++) {
//             matrix[dim*i + j] = 0;
//         }
//     }
// }

void upCountingMatrix(int* matrix, int dim) {
    int count = 1;
    for (int i = 0; i < dim; i++) {
        for (int j = 0; j < dim; j++) {
            matrix[dim*i + j] = count;
            count++;
        }
    }
}
*/