# signum (x)
# int x;
# {
#    if(x > 0) return 1;
#    else if(x < 0) return -1;
#    else return 0;
# }
#
# (x in a0)
# slt a1,a0,zero # a1 = 1 if x is negative, 0 if 0 or positive
# slt a0,zero,a0 # a0 = 1 if x is positive, 0 if 0 or negative
# sub a0,a0,a1   # 1-0 = 1 if positive, 0-0 = 0 if zero, 0-1 = -1 if negative
# (signum(x) in a0) (3 instructions)

main:
    slt a1, a0, zero    # a1 = 1 if x is negative, 0 if 0 or positive
    slt a0, zero, a0    # a0 = 1 if x is positive, 0 if 0 or negative
    sub a0, a0, a1      # 1-0 = 1 if positive, 0-0 = 0 if zero, 0-1 = -1 if negative