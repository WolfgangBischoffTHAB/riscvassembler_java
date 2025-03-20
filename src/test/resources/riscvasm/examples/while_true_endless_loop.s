inita:      li t0, 0                # t0 = 0        # x5 = 0
            li t2, 10               # t2 = 10       # x7 = 10
loop_head:  bge t0, t2, loop_end
                                    # Repeated code goes here
            addi t0, t0, 1
            j loop_head
loop_end:   j inita



# while (true) {
#   for (int i = 0; i < 10; i++) {
#       // Repeated code goes here.
#   }
# }


# 00000293      # addi x5, x0, 0
# 00a00393      # addi x7, x0, 10
# 0072d663      # bge x5, x7, 12
# 00128293      # addi x5, x5, 1
# ff9ff06f      # jal x0, -8
# fedff06f      # jal x0, -20