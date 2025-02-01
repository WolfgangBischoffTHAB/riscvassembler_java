addi  sp, sp, -32           # reserve stack space for function
sw    s0, 28(sp)            # store previous frame
sw    ra, 24(sp)            # store return address
addi  s0, sp, 16            # set new frame location
sw    a0, -4(s0)            # store parameter on stack
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 0           # set a1 to zero
beq   a0, a1, 12            # compare a0 with a1, if equal jump +3
addi  a0, zero, 0           # set a0 to zero
jal   zero, 8               # skip next instruction
addi  a0, zero, 1           # set a0 to one
addi  zero, zero, 0
beq   a0, zero, 16          # if a0 is zero, jump forward
addi  a0, zero, 0           # else set return value to zero
jal   zero, 136             # jump to function exit
jal   zero, 132
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 1           # set a1 to one
beq   a0, a1, 12            # compare a0 with a1, if equal jump +3
addi  a0, zero, 0           # set a0 to zero
jal   zero, 8               # skip next instruction
addi  a0, zero, 1           # set a0 to one
addi  zero, zero, 0
beq   a0, zero, 16          # if a0 is zero, jump forward
addi  a0, zero, 1           # else set return value to one
jal   zero, 84              # jump to function exit
jal   zero, 80
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 1           # set a1 to one
sub   a0, a0, a1            # subtract a1 from a0
jal   ra, -144              # call function fib() into a0
addi  sp, sp, -16           # store result on stack
sw    a0, 0(sp)
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 2           # set a1 to two
sub   a0, a0, a1            # subtract a1 from a0
jal   ra, -176              # call function fib() into a1
addi  a1, a0, 0
lw    a0, 0(sp)             # retrieve result off stack into a0
addi  sp, sp, 16
add   a0, a0, a1            # add a1 to a0
jal   zero, 4               # jump to function exit
addi  sp, s0, 16            # trim stack space
lw    ra, -8(sp)            # recover return address
lw    s0, -4(sp)            # recover previous frame
jalr  zero, ra, 0           # return from function



# fe010113
# 00812e23
# 00112c23
# 01010413
# fea42e23
# 00040513
# ffc50513
# 00052503
# 00000593
# 00b50663
# 00000513
# 0080006f
# 00100513
# 00000013
# 00050863
# 00000513
# 0880006f
# 0840006f
# 00040513
# ffc50513
# 00052503
# 00100593
# 00b50663
# 00000513
# 0080006f
# 00100513
# 00000013
# 00050863
# 00100513
# 0540006f
# 0500006f
# 00040513
# ffc50513
# 00052503
# 00100593
# 40b50533
# f71ff0ef
# ff010113
# 00a12023
# 00040513
# ffc50513
# 00052503
# 00200593
# 40b50533
# f51ff0ef
# 00050593
# 00012503
# 01010113
# 00b50533
# 0040006f
# 01040113
# ff812083
# ffc12403
# 00008067


# example from https://github.com/mausimus/rvcc
#
#
# C Source            Internal IL     Binary     Disassembly               Comment
#-------------------+---------------+----------+-------------------------+--------------------------------------
# int fib(int n) {    fib(int n)      fe010113   addi  sp, sp, -32;        reserve stack space for function
#                                     00812e23   sw    s0, 28(sp);         store previous frame
#                                     00112c23   sw    ra, 24(sp);         store return address
#                                     01010413   addi  s0, sp, 16;         set new frame location
#                                     fea42e23   sw    a0, -4(s0);         store parameter on stack
#   if (n == 0)       x10 := &n       00040513   addi  a0, s0, 0;          get address of variable n
#                                     ffc50513   addi  a0, a0, -4;
#                     x10 := *x10     00052503   lw    a0, 0(a0);          read value from address into a0
#                     x11 := 0        00000593   addi  a1, zero, 0;        set a1 to zero
#                     x10 ?= x11      00b50663   beq   a0, a1, pc + 12;    compare a0 with a1, if equal jump +3
#                                     00000513   addi  a0, zero, 0;        set a0 to zero
#                                     0080006f   jal   zero, pc + 8;       skip next instruction
#                                     00100513   addi  a0, zero, 1;        set a0 to one
#                                     00000013   addi  zero, zero, 0;
#                     if 0 -> +4      00050863   beq   a0, zero, pc + 16;  if a0 is zero, jump forward
#     return 0;       x10 := 0        00000513   addi  a0, zero, 0;        else set return value to zero
#                     return fib      0880006f   jal   zero, pc + 136;     jump to function exit
#                                     0840006f   jal   zero, pc + 132;
#   else if (n == 1)  x10 := &n       00040513   addi  a0, s0, 0;          get address of variable n
#                                     ffc50513   addi  a0, a0, -4;
#                     x10 := *x10     00052503   lw    a0, 0(a0);          read value from address into a0
#                     x11 := 1        00100593   addi  a1, zero, 1;        set a1 to one
#                     x10 ?= x11      00b50663   beq   a0, a1, pc + 12;    compare a0 with a1, if equal jump +3
#                                     00000513   addi  a0, zero, 0;        set a0 to zero
#                                     0080006f   jal   zero, pc + 8;       skip next instruction
#                                     00100513   addi  a0, zero, 1;        set a0 to one
#                                     00000013   addi  zero, zero, 0;
#                     if 0 -> +4      00050863   beq   a0, zero, pc + 16;  if a0 is zero, jump forward
#     return 1;       x10 := 1        00100513   addi  a0, zero, 1;        else set return value to one
#                     return fib      0540006f   jal   zero, pc + 84;      jump to function exit
#   else return                       0500006f   jal   zero, pc + 80;
#     fib(n - 1)      x10 := &n       00040513   addi  a0, s0, 0;          get address of variable n
#                                     ffc50513   addi  a0, a0, -4;
#                     x10 := *x10     00052503   lw    a0, 0(a0);          read value from address into a0
#                     x11 := 1        00100593   addi  a1, zero, 1;        set a1 to one
#                     x10 -= x11      40b50533   sub   a0, a0, a1;         subtract a1 from a0
#                     x10 := fib()    f71ff0ef   jal   ra, pc - 144;       call function fib() into a0
#     +               push x10        ff010113   addi  sp, sp, -16;        store result on stack
#                                     00a12023   sw    a0, 0(sp);
#     fib(n - 2)      x10 := &n       00040513   addi  a0, s0, 0;          get address of variable n
#                                     ffc50513   addi  a0, a0, -4;
#                     x10 := *x10     00052503   lw    a0, 0(a0);          read value from address into a0
#                     x11 := 2        00200593   addi  a1, zero, 2;        set a1 to two
#                     x10 -= x11      40b50533   sub   a0, a0, a1;         subtract a1 from a0
#                     x11 := fib()    f51ff0ef   jal   ra, pc - 176;       call function fib() into a1
#                                     00050593   addi  a1, a0, 0;
#                     pop x10         00012503   lw    a0, 0(sp);          retrieve result off stack into a0
#                                     01010113   addi  sp, sp, 16;
#                     x10 += x11      00b50533   add   a0, a0, a1;         add a1 to a0
#     ;               return fib      0040006f   jal   zero, pc + 4;       jump to function exit
# }                   exit fib        01040113   addi  sp, s0, 16;         trim stack space
#                                     ff812083   lw    ra, -8(sp);         recover return address
#                                     ffc12403   lw    s0, -4(sp);         recover previous frame
#                                     00008067   jalr  zero, ra, 0;        return from function