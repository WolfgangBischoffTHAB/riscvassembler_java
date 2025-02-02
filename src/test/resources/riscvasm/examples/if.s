    li      a4, 5           # a4 = 5, a4 is used to hold the constant 5
    li      a5, 5           # a5 = 5, a5 is the variable a5 used in the if statement
    li      a6, 1           # a6 = 1
    bne     a4, a5, .L3     # if (a5 == 5)
    li      a5, 3           #   a5 = 3,    code executed when the if is taken
.L3:
    li      a6, 8              # some code after the if branch
