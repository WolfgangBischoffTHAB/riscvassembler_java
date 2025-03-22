inita:      li t0, 0                # t0 = 0        # x5 = 0
            li t1, 0                # t1 = 0        # x6 = 0

#            li t2, 999999           # t2 = 10       # x7 = 10

            lui t2, 2441
            addi t2, t2, 1662

#            lui t2, 0
#            addi t2, t2, 10

loop_head:  bge t0, t2, loop_end

                                    # Repeated code goes here

            addi t0, t0, 1          # increment loop index
            j loop_head

            # toggle the led
 loop_end:  lw t1, 60(x0)          # load memory mapped LED memory cell into t1
            xori t1, t1, 1          # toggle lsb
            sw t1, 60(x0)
            j inita



# while (true) {
#   for (int i = 0; i < 10; i++) {
#       // Repeated code goes here.
#   }
# }

# lui x3, 2441

# 00000293
# 00000313
# 009891b7 # lui x3, 2441 --> lui x15, 2441
# 67e78793
# 0072d663
# 00128293
# ff9ff06f
# 03c02303
# 00134313
# 02602e23
# fd9ff06f


# 00000293  # 0
# 00000313  # 4
# 00a00393  # 8
# 0072d663  # 12
# 00128293  # 16
# ff9ff06f  # 20
# 03c02303  # 24
# 00134313  # 28
# 02602e23  # 32
# fddff06f  # 36



# 00000293      # inita:        addi x5, x0, 0x0
# 00000313      #               addi x6, x0, 0x0
# 00a00393      #               addi x7, x0, 0xA
# 0072d663      # loop_head:    bge x5, x7, 0xC     # if (x5 >= x7) jump to loop_end
# 00128293      #               addi x5, x5, 1
# ff9ff06f      #               jal x0, -8          # jal loop head
# 03c02303      # loop_end:     lw x6, 60(x0)
# 00134313      #               xori x6, x6, 1
# 02602e23      #               sw x6, 60(x0)
# fddff06f      #               jal x0, -36         # jal inita