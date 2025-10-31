.global _start
    .section .text

_start:
    li a0, 9                # source matrix dimensions
    li a1, 0                # start x
    li a2, 0                # start y
    li a3, 3                # sub matrix dimensions

    li t6, 0                # counter for sub_matrix cell amount

    li t1, 0                # j
cols_a:
    bge t1, a3, end_cols_a

    li t0, 0                # i
rows_b:
    bge t0, a3, end_rows_b

    



    # index = (start_x + t0) + (start_y + t1) * source_matrix_dimensions
    # t2 = (a1 + t0) + (a2 + t1) * a0
    add t2, a1, t0

    add t3, a2, t1
    mul t3, t3, a0

    add t2, t2, t3

    ; #
    ; # DEBUG print the indexes
    ; #

    ; # add ASCII offset
    ; addi t2, t2, 0x30

    ; # save a0, a7
    ; addi sp, sp, -8         # stack grows downwards
    ; sw a0, 0(sp)
    ; sw a7, 4(sp)
    ; # perform ecall
    ; mv a0, t2               # char to print
    ; li a7, 0x50             # ecall: putchar
    ; ecall 
    ; # restore a7, a0
    ; lw a7, 4(sp)
    ; lw a0, 0(sp)
    ; addi sp, sp, 8          # stack grows downwards and we move it after we loaded the integers


    #
    # Load data from the source matrix into the target matrix
    #

    # offset by matrix ptr
    # la t4, matrix_a + t3  # pseudo instruction load address. Resolved: pc relative!
    la t4, matrix_a         # pseudo instruction load address. Resolved: pc relative!
    slli t2, t2, 2          # mult by 4 for sizeof(word)
    add t4, t4, t2          # advance pointer
    lw t5, (t4)             # load matrix A cell

    print_reg t5            # DEBUG output the register content to the console

    # load data into sub matrix
    la t4, sub_matrix_a
    slli t6, t6, 2          # mult by 4 for sizeof(word)
    add t4, t4, t6          # advance pointer
    sw t5, (t4)             # load matrix A cell

    addi t6, t6, 1


    addi t0, t0, 1          # increment for loop counter (t0 = i)
    j rows_b                # next for loop iteration
end_rows_b:
    nop                     # end of rows B, end of matrix mult

    addi t1, t1, 1          # increment for loop counter (t1 = j)
    j cols_a                # next for loop iteration
end_cols_a:
    nop                     # end of cols A


    

    .section .data
result_data:    .zero 36       # 9x4 = 36

sub_matrix_a:   .zero 36       # 9x4 = 36

sub_matrix_b:   .zero 36       # 9x4 = 36

matrix_a:   .word 0x00000001, 0x00000002, 0x00000003, 0x00000004, 0x00000005, 0x00000006, 0x00000007, 0x00000008, 0x00000009
            .word 0x0000000A, 0x0000000B, 0x0000000C, 0x0000000D, 0x0000000E, 0x0000000F, 0x00000010, 0x00000011, 0x00000012
            .word 0x00000013, 0x00000014, 0x00000015, 0x00000016, 0x00000017, 0x00000018, 0x00000019, 0x0000001A, 0x0000001B
            .word 0x0000001C, 0x0000001D, 0x0000001E, 0x0000001F, 0x00000020, 0x00000021, 0x00000022, 0x00000023, 0x00000024
            .word 0x00000025, 0x00000026, 0x00000027, 0x00000028, 0x00000029, 0x0000002A, 0x0000002B, 0x0000002C, 0x0000002D
            .word 0x0000002E, 0x0000002F, 0x00000030, 0x00000031, 0x00000032, 0x00000033, 0x00000034, 0x00000035, 0x00000036
            .word 0x00000037, 0x00000038, 0x00000039, 0x0000003A, 0x0000003B, 0x0000003C, 0x0000003D, 0x0000003E, 0x0000003F
            .word 0x00000040, 0x00000041, 0x00000042, 0x00000043, 0x00000044, 0x00000045, 0x00000046, 0x00000047, 0x00000048
            .word 0x00000049, 0x0000004A, 0x0000004B, 0x0000004C, 0x0000004D, 0x0000004E, 0x0000004F, 0x00000050, 0x00000051


matrix_b:   .word 0x00000001, 0x00000002, 0x00000003, 0x00000004, 0x00000005, 0x00000006, 0x00000007, 0x00000008, 0x00000009
            .word 0x0000000A, 0x0000000B, 0x0000000C, 0x0000000D, 0x0000000E, 0x0000000F, 0x00000010, 0x00000011, 0x00000012
            .word 0x00000013, 0x00000014, 0x00000015, 0x00000016, 0x00000017, 0x00000018, 0x00000019, 0x0000001A, 0x0000001B
            .word 0x0000001C, 0x0000001D, 0x0000001E, 0x0000001F, 0x00000020, 0x00000021, 0x00000022, 0x00000023, 0x00000024
            .word 0x00000025, 0x00000026, 0x00000027, 0x00000028, 0x00000029, 0x0000002A, 0x0000002B, 0x0000002C, 0x0000002D
            .word 0x0000002E, 0x0000002F, 0x00000030, 0x00000031, 0x00000032, 0x00000033, 0x00000034, 0x00000035, 0x00000036
            .word 0x00000037, 0x00000038, 0x00000039, 0x0000003A, 0x0000003B, 0x0000003C, 0x0000003D, 0x0000003E, 0x0000003F
            .word 0x00000040, 0x00000041, 0x00000042, 0x00000043, 0x00000044, 0x00000045, 0x00000046, 0x00000047, 0x00000048
            .word 0x00000049, 0x0000004A, 0x0000004B, 0x0000004C, 0x0000004D, 0x0000004E, 0x0000004F, 0x00000050, 0x00000051
