/*
void foo (int *x, int *y, int *z, int *pred, int n)
{
    for (int i = 0; i < n; ++i)
    {
        x[i] = y[i] + z[i];
    }
}

https://godbolt.org/

Compiler: 			RISCV (64-bits) gcc 14.3.0      or       RISCV (64-bits) gcc (trunk)
Compiler-Option: 	-march=rv64gcv -O3

Generated Assembly from godbolt:
*/

num1: .quad 0x0000000000000001, 0x0000000000000002, 0x0000000000000003, 0x0000000000000004
num2: .quad 0x0000000000000001, 0x0000000000000002, 0x0000000000000003, 0x0000000000000004
/* Reserve 32 bytes (4 x 64bit variables) (256 bits) for the result */
result: .space 32 

_start:



        /* Pseudo instruction LI is resolved to lui + addi 
        load the address of the result variable / memory location into a3 */
        lui a0, %hi(result)
        addi a0, a0, %lo(result)

        /* Pseudo instruction LI is resolved to lui + addi
        Load the address of the first 64-bit part of the first number into a1 */
        lui a1, %hi(num1)           /* lui uses 32-bit values even on 64 bit arch. Load 32 bit address */
        addi a1, a1, %lo(num1)      /* addi uses 32-bit values even on 64 bit arch. Load 32 bit address */

        /* Pseudo instruction LI is resolved to lui + addi */
        /* Load the address of the first 64-bit part of the second number into a2 */
        lui a2, %hi(num2)
        addi a2, a2, %lo(num2)



foo:
        ble     a4,zero,.L9         ; early out if length parameter n stored in a4 is zero
        addi    a7,a0,-4            ; ptr to x into a7
        sub     t1,a7,a1
        sub     a7,a7,a2
        mv      a6,a0
        mv      a5,a1
        mv      a3,a2
        bgtu    a7,t1,.L13
.L4:
        csrr    t1,vlenb
        addi    t1,t1,-8
        bleu    a7,t1,.L3           ; if there are very few elements to compute, do not even use
                                    ; the RVV extension but compute the result manually
.L5:
        vsetvli a5,a4,e32,m1,ta,ma  ; strip-mining: compute element count for this iteration
        vle32.v v2,0(a1)            ; load y into v2 register
        vle32.v v1,0(a2)            ; load z into v1 register
        slli    a3,a5,2             ; strip-mining: compute elements this iteration
        sub     a4,a4,a5            ; strip-mining: subtract "elements left"
        add     a1,a1,a3            ; strip-mining: advance index by element size this iteration
        add     a2,a2,a3            ; strip-mining: advance index by element size this iteration
        vadd.vv v1,v1,v2            ; perform vector addition
        vse32.v v1,0(a0)            ; store vector register into memory into parameter x
        add     a0,a0,a3
        bne     a4,zero,.L5         ; continue strip-mining loop if "elements left" is zero
        ret                         ; return function

        ; .L3 is for manual vector addition if the vector size is below VLEN
        ; in this case RVV is not even used and the addition is performed "manually"
.L3:
        slli    a4,a4,2             ; compute element count
        add     a1,a1,a4            ; advance y ptr
.L6:
        lw      a2,0(a5)
        lw      a4,0(a3)
        addi    a5,a5,4
        addi    a3,a3,4
        addw    a4,a4,a2
        sw      a4,0(a6)
        addi    a6,a6,4
        bne     a5,a1,.L6
.L9:
        ret
.L13:
        mv      a7,t1
        j       .L4

/**/