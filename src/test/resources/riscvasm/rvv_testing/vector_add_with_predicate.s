foo:
        ble     a4,zero,.L13
        addi    a6,a0,-4
        sub     a7,a6,a2
        sub     a5,a6,a1
        bgtu    a5,a7,.L17
        sub     a6,a6,a3
        bgtu    a5,a6,.L18
.L5:
        csrr    a6,vlenb
        addi    a6,a6,-8
        bleu    a5,a6,.L3
.L6:
        vsetvli a5,a4,e32,m1,ta,mu
        vle32.v v0,0(a3)
        vle32.v v1,0(a1)
        slli    a6,a5,2
        sub     a4,a4,a5
        add     a3,a3,a6
        add     a1,a1,a6
        vmsne.vi        v0,v0,1
        vle32.v v2,0(a2),v0.t
        add     a2,a2,a6
        vadd.vv v1,v2,v1,v0.t
        vse32.v v1,0(a0)
        add     a0,a0,a6
        bne     a4,zero,.L6
        ret
.L8:
        add     a6,a0,a5
        sw      a7,0(a6)
        addi    a5,a5,4
        bne     a4,a5,.L15
.L13:
        ret
.L18:
        mv      a5,a6
        csrr    a6,vlenb
        addi    a6,a6,-8
        bgtu    a5,a6,.L6
.L3:
        slli    a4,a4,2
        li      a5,0
        li      t4,1
.L15:
        add     a6,a3,a5
        lw      a6,0(a6)
        add     a7,a1,a5
        lw      a7,0(a7)
        add     t3,a2,a5
        add     t1,a0,a5
        beq     a6,t4,.L8
        lw      a6,0(t3)
        addi    a5,a5,4
        addw    a6,a6,a7
        sw      a6,0(t1)
        bne     a4,a5,.L15
        ret
.L17:
        mv      a5,a7
        sub     a6,a6,a3
        bleu    a5,a6,.L5
        j       .L18


# C sourcecode
#
# void foo (int *x, int *y, int *z, int *pred, int n)
# {
# 	for (int i = 0; i < n; ++i)
# 	{
# 		x[i] = pred[i] != 1 ? y[i] + z[i] : y[i];
# 	}
# }