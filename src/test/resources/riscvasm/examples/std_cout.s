# https://godbolt.org/
# RISC-V 64 bit, gcc 14.3.0
# -march=rv64gcv -O3

# #include <iostream>
# void foo ()
# {
#	std::cout << "test" << std::endl;
# }

.LC0:
        .string "test"
foo():
        addi    sp,sp,-32
        sd      s1,8(sp)
        lui     a1,%hi(.LC0)
        lui     s1,%hi(_ZSt4cout)
        addi    a1,a1,%lo(.LC0)
        addi    a0,s1,%lo(_ZSt4cout)
        li      a2,4
        sd      s0,16(sp)
        sd      ra,24(sp)
        call    std::basic_ostream<char, std::char_traits<char> >& std::__ostream_insert<char, std::char_traits<char> >(std::basic_ostream<char, std::char_traits<char> >&, char const*, long)
        addi    a4,s1,%lo(_ZSt4cout)
        ld      a5,0(a4)
        ld      a5,-24(a5)
        add     a5,a5,a4
        ld      s0,240(a5)
        beq     s0,zero,.L9
        lbu     a5,56(s0)
        beq     a5,zero,.L5
        lbu     a1,67(s0)
.L6:
        addi    a0,s1,%lo(_ZSt4cout)
        call    std::basic_ostream<char, std::char_traits<char> >::put(char)
        ld      s0,16(sp)
        ld      ra,24(sp)
        ld      s1,8(sp)
        addi    sp,sp,32
        tail    _ZNSo5flushEv
.L5:
        mv      a0,s0
        call    std::ctype<char>::_M_widen_init() const
        ld      a4,0(s0)
        lui     a5,%hi(_ZNKSt5ctypeIcE8do_widenEc)
        addi    a5,a5,%lo(_ZNKSt5ctypeIcE8do_widenEc)
        ld      a4,48(a4)
        li      a1,10
        beq     a4,a5,.L6
        mv      a0,s0
        jalr    a4
        mv      a1,a0
        j       .L6
.L9:
        call    std::__throw_bad_cast()