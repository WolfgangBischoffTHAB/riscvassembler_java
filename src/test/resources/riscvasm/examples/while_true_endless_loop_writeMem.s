inita:      li t0, 0                # t0 = 0        # x5 = 0
            li t1, 0                # t1 = 0        # x6 = 0
            li t2, 10               # t2 = 10       # x7 = 10
loop_head:  bge t0, t2, loop_end

                                    # Repeated code goes here

            addi t0, t0, 1          # increment loop index
            j loop_head

            # toggle the led
 loop_end:  lw t1, 255(x0)          # load memory mapped LED memory cell into t1
            xori t1, t1, 1          # toggle lsb
            sw t1, 255(x0)
            j inita



# while (true) {
#   for (int i = 0; i < 10; i++) {
#       // Repeated code goes here.
#   }
# }



# 00000293
# 00000313
# 00a00393
# 0072d663
# 00128293
# ff9ff06f
# 0ff02303
# 00134313
# 0e602fa3
# fddff06f



# 00000293      # addi x5, x0, 0
# 00000313      # addi x6, x0, 0
# 00a00393      # addi x7, x0, 10
# 0072dc63      # bge x5, x7, 24
# 0ff02303      # lw x6, 255(x0)
# 00134313      # xori x6, x6, 1
# 0e602fa3      # sw x6, 255(x0)
# 00128293      # addi x5, x5, 1
# fedff06f      # jal x0, -20
# fddff06f      # jal x0, -36