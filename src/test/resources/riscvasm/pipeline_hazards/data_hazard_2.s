# Computer Architecture - A quantative approach - page C-16

add x1, x2, x3          # x1 = 5
sub x4, x1, x5          # x4 = 0
and x6, x1, x7          # x6 = 5 & 7 = 5 (101 & 111 = 101)
or  x8, x1, x9          # x8 = 5 | 9 = 13 (101 | 1001 = 1101)
xor x10, x1, x11        # x10 = 5 xor 11 = 14 (0101 xor 1011 = 1110)


# Assumption for initial register values:
# x0 = 0
# x1 = 1
# x2 = 2
# x3 = 3
# x4 = 4
# x5 = 5
# x6 = 6
# ...
# x11 = 11

# Registers after execution
# x0 = 0
# x1 = 5
# x2 = 2
# x3 = 3
# x4 = 0
# x5 = 5
# x6 = 5
# x7 = 7
# x8 = 13
# x9 = 9
# x10 = 14