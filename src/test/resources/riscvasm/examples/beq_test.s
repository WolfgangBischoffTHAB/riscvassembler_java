start:
    addi x5, x0, 0x0    # initialize register 5 with the same value as register 6
    addi x6, x0, 0x0    # initialize register 6 with the same value as register 5

    addi x0, x0, 0x0    # waste time for the above statements to write back
    addi x0, x0, 0x0    # waste time for the above statements to write back
    addi x0, x0, 0x0    # waste time for the above statements to write back
    addi x0, x0, 0x0    # waste time for the above statements to write back
    addi x0, x0, 0x0    # waste time for the above statements to write back

    beq x5, x6, start

    sub x0, x5, x6      # these statements should be flushed out of the pipeline
    sub x0, x5, x6      # these statements should be flushed out of the pipeline
    sub x0, x5, x6      # these statements should be flushed out of the pipeline
    sub x0, x5, x6      # these statements should be flushed out of the pipeline
    sub x0, x5, x6      # these statements should be flushed out of the pipeline