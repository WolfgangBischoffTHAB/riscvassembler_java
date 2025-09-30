# https://michaeljclark.github.io/asm.html#:~:text=The%20la%20(load%20address)%20instruction,command%20line%20options%20or%20an%20.
#
# https://github.com/riscv/riscv-isa-manual/issues/144
# https://www.youtube.com/watch?v=ROkjMXuR2u8
#
# file: la.s
#
# Load Address
# The la (load address) instruction is an assembler pseudo-instruction used to
# load the address of a symbol or label. The instruction can emit absolute or
# relative addresses depending on the -fpic or -fno-pic assembler command line
# options or an .options pic or .options nopic assembler directive.
#
# Format: la rd, symbol
#
# Resolved by:
# auipc rd, symbol[31:12]
# addi rd, rd, symbol[11:0]
#
# auipc rd, %hi(symbol)
# addi rd, rd, %lo(symbol)

main:
    la t0, test_2
    addi t1, t1, 1
    addi t1, t1, 1
    addi t1, t1, 1
test_2:
    addi t1, t1, 1



# Encoding:
#
# la a1, 0x10000
#
# is encoded to
#
# 10000-4 = b1111 1111 1111 1100
#
# auipc a1, %hi(0x10000)
# addi a1, a1, %lo(0x10000)
#
# auipc x11, 16
# addi x11, x11, -4
#
# Rules (see: https://www.youtube.com/watch?v=ROkjMXuR2u8) :
# la rd,label
# --->
# auipc rd, ((label-.)) >>U 12) + ((label-.) & 0x00000800 ? 1 : 0)
# addi rd, rd, ((label-.) & 0xfff)
#
# Example:
#
# Assumptions:
# . = 4
# label's absolute address is 0x10000
#
# Computation for auipc:
# ((label-.)) >>U 12)
# ((0x10000 - 4)) >>U 12) = b1111
#
# ((label-.) & 0x00000800 ? 1 : 0)
# ((0x10000 - 4) & 0x00000800 ? 1 : 0) is 1
#
# in total b1111 + 1 = 16 = 0x10
#
#
# Computation for addi:
# ((label-.) & 0xfff)
# ((0x10000-4) & 0xfff) = b 1111 1111 1100 = -4