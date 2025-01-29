# Computer Architecture - A quantative approach - page C-18

add x1, x2, x3      # 003100b3
lb x4, 0(x1)        # 00008203
sb x4, 12(x1)       # 00408623

# Assumption for initial register values:
# x0 = 0
# x1 = 1
# x2 = 2
# x3 = 3
#
# Assumption for initial memory values:
# memory(5) = 10
#
# Register values after execution:
# x0 = 0
# x1 = 5
# x2 = 2
# x3 = 3
# x4 = 10
#
# Memory values after execution:
# memory(5) = 10
# memory(12) = 10