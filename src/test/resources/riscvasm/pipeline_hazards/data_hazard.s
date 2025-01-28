# See Computer Organization and Design - The Hardware Software Interface - RISC V Edition, page 527 ff
#
# 003100b3
# 40508233

add x1, x2, x3
sub x4, x1, x5

# Assumption: registers are preloaded
# x2 = 5
# x3 = 10
#
# The two writebacks are:
# x1 = 15
# x4 = 15

# The problem is that the sub instruction reads from the same register x1 that
# the first instruction writes into. The read happens in the instruction
# decode (ID) stage and the write back happens in the write back (WB) stage.
# The WB stage is after the ID phase! Therefore the second instruction has
# to stall the pipeline until the first instruction has passed the WB stage!
# The value that is written into the x1 register during the WB phase is computed
# in the EX phase which perfectly lines up with the second instructions ID phase!
# A strategy to prevent the stall is to not wait until the result value is written
# into the register, but to use the value for the second instruction as soon
# as it is computed! This strategy is called forwarding.
#
# The instruction decode (ID) stage of the sub (second) instruction has to
# identify the data hazard! It has to check both it's source register for
# equality with the rd register of the preceding instruction.
# (Here: add x1 == sub x1 and hence a data hazard is detected!)
#
# When the rs registers equal the rd register, then a data hazard has occured.
# The second instruction has to use the output of the ALU (rd register) and
# the that rd register value has to be forwarded to be usable by the second instruction.
# The second instruction will copy the output of the ALU into the ID/EX pipeline
# storage space between the ID and the EX (= ID/EX) stages. This is the forwarding
# step!
#
# From the ID/EX pipeline storage, the second instruction can read the forwarded
# rd value and use it in the EX stage. Now the pipeline can immediately execute
# the second instruction without stalling. Otherwise, without forwarding,
# the second instruction has to stall until the register rd is written by
# the first instruction during it's writeback pipeline stage!