    addi    sp, sp, -8
    sw      ra, 0(sp)
#    la      a0, prompt     # parameter 1
#    mv      a1, t0         # parameter 2
#    mv      a2, t1         # parameter 3
    sw      ra, 0(sp)       #
    call    square
square:
    addi    sp, sp, -32     # build a stack frame
    sw      ra, 28(sp)      # store the return address
    sw      s0, 24(sp)      # store the old stack pointer
    addi    s0, sp, 32      # set the new bottom of the stack (in case more functions are called)
    sw      a0, -20(s0)     # copy parameter a0 into memory and back int a5
    lw      a5, -20(s0)     # copy value back into a5
    mul     a5, a5, a5      # perform the square operation
    mv      a0, a5          # move result into a0 which is where the result is placed
    lw      ra, 28(sp)
    lw      s0, 24(sp)
    addi    sp, sp, 32
    jr      ra



#/* Type your code here, or load an example. */
#int square(int num) {
#    return num * num;
#}
