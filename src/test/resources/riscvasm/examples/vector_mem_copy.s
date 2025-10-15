// https://gitlab.com/qemu-project/qemu/-/issues/1976
vector_memcmp:
    vsetvli a3,a2,e8,m8,ta,ma
    vle8.v  v0,(a0)
    vle8.v  v8,(a1)

    csrw    vstart, 1         # Simulate page fault for the first element
    vle8.v  v8,(a1)           # This instruction sets env->vstart to zero, but doesn't change tb/disas flags

    vmsne.vv        v16,v0,v8
    sub     a2,a2,a3

    vfirst.m        a4,v16    # here I'm getting Illegal Instruction since vstart_eq_zero is false
    bgez    a4,.Lfound_diff
    add     a0,a0,a3
    add     a1,a1,a3
    bnez    a2,vector_memcmp
    li      a0,0
    ret
.Lfound_diff:
    add     a0,a0,a4
    add     a1,a1,a4
    lbu     a5,0(a0)
    lbu     a6,0(a1)
    sub     a0,a5,a6
    ret
