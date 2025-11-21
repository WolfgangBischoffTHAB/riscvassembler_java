.LC3:
.LC0:
main:
_start:
        addi    sp,sp,-240
        sw      ra,236(sp)
        sw      s0,232(sp)
        addi    s0,sp,240
        lui     a5,%hi(.LC3)
        addi    a0,a5,%lo(.LC3)
        call    puts
        lui     a5,%hi(.LC0)
        addi    a4,a5,%lo(.LC0)
        addi    a5,s0,-80
        mv      a3,a4
        
  puts:
  	jr ra