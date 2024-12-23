# #include <stdio.h>
# int main() {
# 
#   int i, n;
# 
#   // initialize first and second terms
#   int t1 = 0, t2 = 1;
# 
#   // initialize the next term (3rd term)
#   int nextTerm = t1 + t2;
# 
#   // get no. of terms from user
#   printf("Enter the number of terms: ");
#   scanf("%d", &n);
# 
#   // print the first two terms t1 and t2
#   printf("Fibonacci Series: %d, %d, ", t1, t2);
# 
#   // print 3rd to nth terms
#   for (i = 3; i <= n; ++i) {
#     printf("%d, ", nextTerm);
#     t1 = t2;
#     t2 = nextTerm;
#     nextTerm = t1 + t2;
#   }
# 
#   return 0;
# }


.LC0:
        .string "Enter the number of terms: "
.LC1:
        .string "%d"
.LC2:
        .string "Fibonacci Series: %d, %d, "
.LC3:
        .string "%d, "
main:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      zero,-32(s0)
        li      a5,1
        sw      a5,-24(s0)
        lw      a4,-32(s0)
        lw      a5,-24(s0)
        add     a5,a4,a5
        sw      a5,-28(s0)
        lui     a5,%hi(.LC0)
        addi    a0,a5,%lo(.LC0)
        call    printf
        addi    a5,s0,-36
        mv      a1,a5
        lui     a5,%hi(.LC1)
        addi    a0,a5,%lo(.LC1)
        call    __isoc99_scanf
        lw      a2,-24(s0)
        lw      a1,-32(s0)
        lui     a5,%hi(.LC2)
        addi    a0,a5,%lo(.LC2)
        call    printf
        li      a5,3
        sw      a5,-20(s0)
        j       .L2
.L3:
        lw      a1,-28(s0)
        lui     a5,%hi(.LC3)
        addi    a0,a5,%lo(.LC3)
        call    printf
        lw      a5,-24(s0)
        sw      a5,-32(s0)
        lw      a5,-28(s0)
        sw      a5,-24(s0)
        lw      a4,-32(s0)
        lw      a5,-24(s0)
        add     a5,a4,a5
        sw      a5,-28(s0)
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)
.L2:
        lw      a5,-36(s0)
        lw      a4,-20(s0)
        ble     a4,a5,.L3
        li      a5,0
        mv      a0,a5
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra
