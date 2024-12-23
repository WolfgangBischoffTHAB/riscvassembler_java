# source: https://www.reddit.com/r/asm/comments/mz73lk/examples_of_riscv_assembly_programs/?rdt=49314
#
# void stringcopy(char *dst, const char *src) {
#    int i;
#    char c;
#    do {
#        c = *src++;
#        *dst++ = c;
#    } while (c != '\0');
# }

.section .text
.global stringcopy
stringcopy:
    # a0 = destination
    # a1 = source
1:
    lb      t0, 0(a1)  # Load a char from the src
    sb      t0, 0(a0)  # Store the value of the src
    beqz    t0, 1f     # Check if it's 0
    addi    a0, a0, 1
    addi    a1, a1, 1
    j       1b
1:
    ret