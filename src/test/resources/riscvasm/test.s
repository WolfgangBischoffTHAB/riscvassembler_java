uart:
        #.word   268435456
        .word   0x10000000                      # [00]
putchar:
        addi    sp, sp, -32                     # [04]
        sw      ra, 28(sp)                      # [08]
        sw      s0, 24(sp)                      # [0c]
        addi    s0, sp, 32                      # [10]
        mv      a5, a0                          # [14]
        sb      a5, -17(s0)                     # [18]
        nop                                     # [1c]
.L2:
        lui     a5, %hi(uart)                   # [20] this is removed by the linker!!! optimized into lw	a5,0(zero) along with the next line
        lw      a5, %lo(uart)(a5)               # [24] this is removed by the linker!!! optimized into lw	a5,0(zero) along with the prev line
        lui     a4, %hi(LSR.2)                  # [28]
        lbu     a4, %lo(LSR.2)(a4)              # [2c]
        add     a5, a5, a4                      # [30]
        lbu     a5, 0(a5)                       # [34]
        andi    a4, a5, 0xff                    # [38]
        lui     a5, %hi(LSR_RI.1)               # [3c]
        lbu     a5, %lo(LSR_RI.1)(a5)           # [40]
        and     a5, a4, a5                      # [44]
        andi    a5, a5, 0xff                    # [48]
        beq     a5, zero, .L2                   # [4c]
        lui     a5, %hi(uart)                   # [50]
        lw      a5, %lo(uart)(a5)               # [54]
        lui     a4, %hi(THR.0)                  # [58]
        lbu     a4, %lo(THR.0)(a4)              # [5c]
        add     a4, a5, a4                      # [60]
        lbu     a5, -17(s0)                     # [64]
        sb      a5, 0(a4)                       # [68]
        mv      a0, a5                          # [6c]
        lw      ra, 28(sp)                      # [70]
        lw      s0, 24(sp)                      # [74]
        addi    sp, sp, 32                      # [78]
        jr      ra                              # [7c]
puts:
        addi    sp, sp, -32                     # [80]
        sw      ra, 28(sp)                      # [84]
        sw      s0, 24(sp)                      # [88]
        addi    s0, sp, 32                      # [8C]
        sw      a0, -20(s0)                     # [90]
        j       .L5                             # [94]
.L6:
        lw      a5, -20(s0)                     # [98]
        addi    a4, a5, 1                       # [9c]
        sw      a4, -20(s0)                     # [a0]
        lbu     a5, 0(a5)                       # [a4]
        mv      a0, a5                          # [a8]
        call    putchar                         # [ac]   pseudo instruction is resolved to (97000000 = auipc ra,0x0) and (f58080e7 jalr -168(ra) # 4 <putchar>)
                                                # [b0]   here goes the (f58080e7 jalr -168(ra) # 4 <putchar>) instruction
.L5:
        lw      a5, -20(s0)                     # [b4]
        lbu     a5, 0(a5)                       # [b8]
        bne     a5, zero, .L6                   # [bc]
        li      a0, 10                          # [c0]
        call    putchar                         # [c4] first instruction: auipc	ra,0x0
                                                # [c8] second instruction: jalr	-192(ra) # 4 <putchar>
        nop                                     # [cc]
        lw      ra, 28(sp)                      # [d0]
        lw      s0, 24(sp)                      # [d4]
        addi    sp, sp, 32                      # [d8]
        jr      ra                              # [dc]
.LC0:
        .string "Hello RISC-V"                  # [ec] 13 bytes including zero termination
enter:
        addi    sp, sp, -16                     # [ed]
        sw      ra, 12(sp)                      # [f1]
        sw      s0, 8(sp)                       # [f5]
        addi    s0, sp, 16                      # [f9]
        lui     a5, %hi(.LC0)                   # [fd]
        addi    a0, a5, %lo(.LC0)               # [0101]
        call    puts                            # [0105] first instruction: auipc
                                                # [0109] second instruction: jalr
        nop                                     # [010d]
        lw      ra, 12(sp)                      # [0111]
        lw      s0, 8(sp)                       # [0115]
        addi    sp, sp, 16                      # [0119]
        jr      ra                              # [011d]
THR.0:
        .byte   0                               # [0121]
LSR.2:
        .byte   5                               # [0122]
LSR_RI.1:
        .byte   64                              # [0123]
