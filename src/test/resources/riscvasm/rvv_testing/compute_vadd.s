# Source: https://research.samsung.com/blog/RISC-V-and-Vectorization

    .section .data

# num1: .quad 0x123456789ABCDEF0, 0x0FEDCBA987654321, 0x0011223344556677, 0x8899AABBCCDDEEFF
# num2: .quad 0x1122334455667788, 0x99AABBCCDDEEFF00, 0x1234567890ABCDEF, 0xFEDCBA9876543210

num1: .quad 0x0000000000000001, 0x0000000000000002, 0x0000000000000003, 0x0000000000000004
num2: .quad 0x0000000000000001, 0x0000000000000002, 0x0000000000000003, 0x0000000000000004

# Reserve 32 bytes (4 x 64bit variables) (256 bits) for the result
result: .space 32 

    .section .text
    .globl _start
_start:
main:
    
    # Set up vector registers - https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
    li          t0, 4           # Set AVL (Application Vector Length) to 4 (256 bits / 64-bit elements)
    vsetvli     t0, t0, e64     # Set vector length and element width to 64-bit    

    # Load 256-bit numbers into vector registers
    la          a0, num1        
    vle64.v     v0, (a0)        # Load the first array into vector register v0
    la          a0, num2
    vle64.v     v1, (a0)        # Load the second array into vector register v1    
    
    # Perform the addition
    vadd.vv     v2, v0, v1      # Add vector register v0 and v1, store the result in vector register v2
    la          a0, result

    # Store the result back to memory
    vse64.v     v2, (a0)        # Store the result from vector register v2 to the memory location result

    # Exit (for demonstration purposes, assuming an environment that supports this)
    li          a0, 0           # Trigger success output in the custom implementation of the exit-ecall
    li          a7, 93          # ECALL number for exit
    ecall