# Zero all vector registers
vsetvli t0, x0, e8,m8,tu,mu
vmv.v.i v0, 0x0
vmv.v.i v8, 0x0
vmv.v.i v16, 0x0
vmv.v.i v24, 0x0