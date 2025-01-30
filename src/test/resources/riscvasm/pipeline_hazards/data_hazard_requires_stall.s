# Computer Architecture - A quantative approach - page C-19
#
# The problem is the first instruction which may provide the data not
# earlier than the mem phase. The subsequent instructions (sub, and)
# need the data earlier than the cycle in which mem is returned!
#
# The is no way to solve this issue via forwarding, only stalls solve
# the issue.

lb      x1, 0(x2)
sub     x4, x1, x5
and     x6, x1, x7
or      x8, x1, x9

# machine code after assembly
# 00010083
# 40508233
# 0070f333
# 0090e433

# Assumption for initial register values:
# x0 = 0
# x1 = 1
# x2 = 2
# x3 = 3
# x4 = 4
# x5 = 5
# x6 = 6
# ...
# x9 = 9

# Registers after execution
# x0 = 0
# x1 = 0
# x2 = 2
# x3 = 3
# x4 = -5
# x5 = 5
# x6 = 0
# x7 = 7
# x8 = 9
# x9 = 9