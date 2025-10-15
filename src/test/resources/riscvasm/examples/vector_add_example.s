# vector-vector add routine of 32-bit integers
#
# void vvaddint32(size_t n, const int* x, const int* y, int* z)
# { 
#     for (size_t i=0; i<n; i++) 
#     { 
#         z[i] = x[i] + y[i];
#     } 
# }
#
# a0 = n, a1 = x, a2 = y, a3 = z
#
# Non-vector instructions are indented
main:
vvaddint32:
    vsetvli     t0, a0, e32,      ta, ma    # Set vector length based on vectors with 32-bit elements
    
    vle32.v     v0, 0(a1)                   # Get first vector
                                            # load vector x (a1) into vector register v0
                                            # This transfers data into the vector engine
    
    sub         a0, a0, t0                  # Decrement number done
                                            # subtract the amount of elements that the vector-engine
                                            # processes per vadd.vv call (t0) from the length of the
                                            # vectors (a0). This value is used at the bottom to 
                                            # decide if more elements are left over and if another
                                            # iteration is required. This is the case if the vectors
                                            # to add are larger than fit into the vector-engine in 
                                            # one go
    
    slli        t0, t0, 2                   # Multiply number done by 4 bytes
                                            # reuse t0 because it is recomputed in each loop

    add         a1, a1, t0                  # Bump pointer, point to the next index for stripmining in 
                                            # vector x
    
    vle32.v     v1, 0(a2)                   # Get second vector
                                            # load vector y (a2) into vector register v1
                                            # This transfers data into the vector engine

    add         a2, a2, t0                  # Bump pointer, point to the next index for stripmining in 
                                            # vector y

    vadd.vv     v2, v0, v1                  # Sum vectors

    vse32.v     v2, 0(a3)                   # Store result
                                            # This transfers data out of the vector engine
    
    add         a3, a3, t0                  # Bump pointer, point to the next index for stripmining in 
                                            # vector z

    bnez        a0, vvaddint32              # Loop back, use a0 to see if the vectors have been processed
                                            # or if elements are left to process.

    ret                                     # Finished