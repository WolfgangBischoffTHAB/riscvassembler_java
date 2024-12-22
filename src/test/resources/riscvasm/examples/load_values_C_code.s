# Compiled using https://godbolt.org/
#
# typedef unsigned int uint32_t;
# int main(int argc, char** argv)
# {
#  uint32_t volatile a0 = 0;
#  uint32_t volatile a1 = 1;
#  uint32_t volatile a2 = 2;
#  uint32_t volatile a3 = 3;
#  uint32_t* p = (uint32_t*) 0x81000000;
#  *(p + 0) = a0;
#  *(p + 1) = a1;
#  *(p + 2) = a2;
#  *(p + 3) = a3;
# }


main:
    addi    sp,sp,-64
    sw      ra,60(sp)
    sw      s0,56(sp)
    addi    s0,sp,64
    sw      a0,-52(s0)
    sw      a1,-56(s0)
    sw      zero,-24(s0)
    li      a5,1
    sw      a5,-28(s0)
    li      a5,2
    sw      a5,-32(s0)
    li      a5,3
    sw      a5,-36(s0)
    li      a5,-2130706432
    sw      a5,-20(s0)
    lw      a4,-24(s0)
    lw      a5,-20(s0)
    sw      a4,0(a5)
    lw      a5,-20(s0)
    addi    a5,a5,4
    lw      a4,-28(s0)
    sw      a4,0(a5)
    lw      a5,-20(s0)
    addi    a5,a5,8
    lw      a4,-32(s0)
    sw      a4,0(a5)
    lw      a5,-20(s0)
    addi    a5,a5,12
    lw      a4,-36(s0)
    sw      a4,0(a5)
    li      a5,0
    mv      a0,a5
    lw      ra,60(sp)
    lw      s0,56(sp)
    addi    sp,sp,64
    jr      ra