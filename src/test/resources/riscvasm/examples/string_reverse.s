# void strrev(char *str) {
#     int i;
#     int sz = strlen(str);
#     for (i = 0;i < sz / 2;i++) {
#         char c = str[i];
#         str[i] = str[sz - i - 1];
#         str[sz - i - 1] = c;
#     }
# }

.section .text

.global strrev
strrev:
    # s1 = str
    # a0 = sz
    # t0 = sz / 2
    # t1 = i
    # Enter stack frame
    addi    sp, sp, -16
    sw      ra, 0(sp)
    sw      s1, 8(sp)

    # Get the size of the string
    mv      s1, a0
    call    strlen
    srai    t0, a0, 1     # Divide sz by 2
    li      t1, 0         # i = 0
1:  # for loop
    bge     t1, t0, 1f
    add     t2, s1, t1    # str + i
    sub     t3, a0, t1    # sz - i
    addi    t3, t3, -1    # sz - i - 1
    add     t3, t3, s1    # str + sz - i - 1
    lb      t4, 0(t2)     # str[i]
    lb      t5, 0(t3)     # str[sz - i - 1]
    sb      t4, 0(t3)     # swap
    sb      t5, 0(t2)
    addi    t1, t1, 1
    j       1b
1:
    # Leave stack frame
    lw      s1, 8(sp)
    lw      ra, 0(sp)
    addi    sp, sp, 16
    ret

.global strlen

strlen:
                        # a0 = const char *str (a0 constains the address of the string input data for which to determine the string length)
    li     t0, 0        # i = 0                                                 (size: 8)  8 pseudo (lui, addi)
                        # initialize the string length to zero
1:                      # Start of for loop
    add    t1, t0, a0   # Add the byte offset for str[i]                        (size: 4) 12
                        # t1 now points to the current character in the string
    lb     t1, 0(t1)    # Dereference str[i]                                    (size: 8) 20
                        # fetch the character from memory into the t1 register
    beqz   t1, 1f       # if str[i] == 0, break for loop                        (size: 4) 24
                        # check for zero termination which marks the end of the string
    addi   t0, t0, 1    # Add 1 to our iterator                                 (size: 4) 28
    j      1b           # Jump back to condition (1 backwards)                  (size: 4) 32
1:                      # End of for loop
    mv     a0, t0       # Move t0 into a0 to return                             (size: 4) 36
                        # place the return value (the string length) into a0
    ret                 # Return back via the return address register
