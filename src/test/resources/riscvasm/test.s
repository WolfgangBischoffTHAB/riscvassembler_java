.equ IO_BASE, 0x400000
.equ IO_LEDS, 4

.section .text

.globl start

start:                          #  0        # addr: 0
    li   gp, IO_BASE            #  0 +8     # addr: 0
    li   sp, 0x1800             #  8 +8     # addr: 4
.L0:                            # 16        # addr: 8
    li   t0, 5                  # 16 +8     # addr: 8
    sw   t0, IO_LEDS(gp)        # 24 +4     # addr: 12
    call wait                   # 28 +8     # addr: 16
    li   t0, 10                 # 36 +8     # addr: 20
    sw   t0, IO_LEDS(gp)        # 44 +4     # addr: 24
    call wait                   # 48 +8     # addr: 28 (encoded to jal)
    j    .L0                    # 56 +8     # addr: 32
wait:                           # 64        # addr: 36
    li   t0, 1                  # 64 +8     # addr: 36
    slli t0, t0, 17             # 72 +4     # addr: 40
.L1:                            # 76        # addr: 44
    addi t0, t0, -1             # 76 +4     # addr: 44
    bnez t0, .L1                # 80 +4     # addr: 48
    ret                         # 84 +4     # addr: 52