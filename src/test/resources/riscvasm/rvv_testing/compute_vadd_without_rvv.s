    .section .data
num1: .quad 0x123456789ABCDEF0, 0x0FEDCBA987654321, 0x0011223344556677, 0x0899AABBCCDDEEFF
num2: .quad 0x1122334455667788, 0x09AABBCCDDEEFF00, 0x1234567890ABCDEF, 0x0EDCBA9876543210
result: .space 32 # Reserve 32 bytes (256 bits) for the result

    .section .text
    .globl _start
_start:
main:

    # A note on architecture: # lui uses 32-bit values even on 64 bit architectures. 
    # The reason for 32-bit is that here, we deal with addresses not values. 
    # To load 64-bit values, ld is used later. ld on a 64 bit architecture will load 64 bit values!
    
    # Pseudo instruction LI is resolved to lui + addi
    # Load the address of the first 64-bit part of the first number into a1
    lui a1, %hi(num1)           # lui uses 32-bit values even on 64 bit arch. Load 32 bit address
    addi a1, a1, %lo(num1)      # addi uses 32-bit values even on 64 bit arch. Load 32 bit address

    # Pseudo instruction LI is resolved to lui + addi
    # Load the address of the first 64-bit part of the second number into a2
    lui a2, %hi(num2)
    addi a2, a1, %lo(num2)

    # Pseudo instruction LI is resolved to lui + addi
    # load the address of the result variable / memory location into a3
    lui a3, %hi(result)
    addi a3, a3, %lo(result)

    #
    # add first array elements, first 64 bit add
    #

    # Add the first 64-bit parts
    ld  t1, 0(a1)           # load 64-bit of data from memory into t1. ld uses 64 bit values on a 64 bit arch!+
    ld  t2, 0(a2)           # load 64-bit of data from memory into t2
    add t3, t2, t1          # perform 64 bit add. Result is in t3
    sd  t3, 0(a3)           # Store the result

    #
    # add second array elements, second 64 bit add
    #

    # do the same stuff for every 64 bit word
    ld  t1, 8(a1)
    ld  t2, 8(a2)
    add t3, t2, t1
    sd  t3, 8(a3)

    #
    # add third array elements, third 64 bit add
    #

    ld  t1, 16(a1)
    ld  t2, 16(a2)
    add t3, t2, t1
    sd  t3, 16(a3)

    #
    # add fourth array elements, fourth 64 bit add
    #

    ld  t1, 24(a1)
    ld  t2, 24(a2)
    add t3, t2, t1
    sd  t3, 24(a3)

    # Exit (for demonstration purposes, assuming an environment that supports this)
    li a0, 0
    li a7, 93 # ECALL number for exit
    ecall