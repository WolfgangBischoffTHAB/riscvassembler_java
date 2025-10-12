# https://godbolt.org/#g:!((g:!((g:!((h:codeEditor,i:(filename:'1',fontScale:14,fontUsePx:'0',j:1,lang:___c,selection:(endColumn:2,endLineNumber:7,positionColumn:2,positionLineNumber:7,selectionStartColumn:2,selectionStartLineNumber:7,startColumn:2,startLineNumber:7),source:'void+foo+(int+*x,+int+*y,+int+*z,+int+*pred,+int+n)%0A%7B%0A%09for+(int+i+%3D+0%3B+i+%3C+n%3B+%2B%2Bi)%0A%09%7B%0A%09%09x%5Bi%5D+%3D+pred%5Bi%5D+!!%3D+1+%3F+y%5Bi%5D+%2B+z%5Bi%5D+:+y%5Bi%5D%3B%0A%09%7D%0A%7D'),l:'5',n:'0',o:'C+source+%231',t:'0')),k:42.972590401951834,l:'4',n:'0',o:'',s:0,t:'0'),(g:!((h:compiler,i:(compiler:rv64-cgcc1520,filters:(b:'0',binary:'1',binaryObject:'1',commentOnly:'0',debugCalls:'1',demangle:'0',directives:'0',execute:'1',intel:'0',libraryCode:'0',trim:'1',verboseDemangling:'0'),flagsViewOpen:'1',fontScale:14,fontUsePx:'0',j:1,lang:___c,libs:!(),options:'-march%3Drv64gcv+-O3',overrides:!(),selection:(endColumn:1,endLineNumber:1,positionColumn:1,positionLineNumber:1,selectionStartColumn:1,selectionStartLineNumber:1,startColumn:1,startLineNumber:1),source:1),l:'5',n:'0',o:'+RISC-V+(64-bits)+gcc+15.2.0+(Editor+%231)',t:'0')),k:52.30138117832041,l:'4',n:'0',o:'',s:0,t:'0'),(g:!((h:output,i:(compilerName:'RISC-V+(32-bits)+gcc+(trunk)',editorid:1,fontScale:14,fontUsePx:'0',j:1,wrap:'1'),l:'5',n:'0',o:'Output+of+RISC-V+(64-bits)+gcc+15.2.0+(Compiler+%231)',t:'0')),k:4.726028419727749,l:'4',n:'0',o:'',s:0,t:'0')),l:'2',n:'0',o:'',t:'0')),version:4
# https://godbolt.org/z/3ojYWE1T6

# void foo (int *x, int *y, int *z, int *pred, int n)
# {
# 	 for (int i = 0; i < n; ++i)
# 	 {
# 		 x[i] = pred[i] != 1 ? y[i] + z[i] : y[i];
# 	 }
# }

.equ vlenb, 0xFFF   # ID of the CSR register vlenb: This is an implementation detail
                    # CSR ids: 0x000-0xFFF: 0 - 4095

main:

    li a0, 0x1000
    li a1, 0x1010
    li a2, 0x1020
    li a3, 0x1030
    li a4, 1

# foo(int* x, int* y, int* z, int* pred, int n):
# foo():
foo:

        ble     a4, zero, .L13 				# return if vector length (n in a0) is zero

        addi    a6, a0, -4
        sub     a7, a6, a2
        sub     a5, a6, a1
        bgtu    a5, a7, .L17
        sub     a6, a6, a3
        bgtu    a5, a6, .L18

; # check against VLEN for potential computation without the vector-engine	
.L5:
        csrr    a6, vlenb					# csr control and status register vlenb is read. lenb == VLEN/8 (vector register length in bytes)
        addi    a6, a6, -8
        bleu    a5, a6, .L3
		
.L6:
        # This is where the "stripmining" starts
        #
		# I think here, the vector engine is asked, what stride it will use to perform the computation
		# This information needs to be update each iteration!
		# The stride is used by the source code to load data for processing in chunks into the vector engine
		# and to organize the iteration variable to implement the for-loop
		#
		# vsetvli s1, zero, e64,m4,ta,mu 	# LMUL=4 because m4 sets LMUL to 4
        vsetvli a5,a4, e32,m1,ta,mu			# (v)ector (set) (v)ector (l)ength (i)mmediate
											# vsetvli rd, rs1, vtypei # rd = new vl, rs1 = AVL, vtypei = new vtype setting
											#
											# AVL = Application Vector Length
											#
											# see page 26 of riscv-v-spec-1.0:
											# e8 	# SEW=8b       (SEW == Single Element Width)
											# e16 	# SEW=16b
											# e32 	# SEW=32b
											# e64 	# SEW=64b
											#
											# LMUL = 
											#
											# see page 26 of riscv-v-spec-1.0:
											# mf8 	# LMUL=1/8
											# mf4 	# LMUL=1/4
											# mf2 	# LMUL=1/2
											# m1 	# LMUL=1, assumed if m setting absent
											# m2 	# LMUL=2
											# m4 	# LMUL=4
											# m8 	# LMUL=8
											#
											# see page 13 of riscv-v-spec-1.0: 
											# ta # Tail agnostic
											# tu # Tail undisturbed
											# ma # Mask agnostic
											# mu # Mask undisturb
											
        vle32.v 	v0, 0(a3)				# vector unit-stride load, load vector 'pred' (a3) into vector register v0 
        vle32.v 	v1, 0(a1)				# vector unit-stride load, load vector 'y' (a1) into vector register v1
			
		slli    	a6, a5, 2				# multiply the stride by 4 for 32bit values???
		
		sub     	a4, a4, a5              # update the iteration variable stored in a4
		                                    # remove on stride's length from 'n'
											# the parameter 'n' in a4 is used to take care of the for-loop statement
											# The stride length is returned by the vsetvli instruction earlier
		
		add     	a3, a3, a6              # move the 'pred' vector pointer forward by a stride's length
		add     	a1, a1, a6				# move the 'y' vector pointer forward by a stride's length
			
        vmsne.vi    v0, v0, 1
        vle32.v 	v2, 0(a2), v0.t			# vector unit-stride load, load vector 'z' (a2) into vector register v2, at offset a2
			
		add     	a2, a2, a6              # move the offset into vector 'z' forward by a vector stride
        
		# add vector 'z' (v2) to vector 'y' (v1) only 
		# if the pred-vector has a 0 stored at that specific bit
		# use vector 'pred' (v0) as a mask
		vadd.vv 	v1, v2, v1, v0.t		# Vector Addition Vector-Vector (vadd.vv). rd: vector v1 'y'
											# Fourth parameter (v0.t) is vector masking (m) (1=disabled, 0=enabled)
											# if masking is enabled, a mask is read from the destination vector is only updated at bits
											# where the mask has 1 values store.
        
		# store result from 'v1' to the 'x' vector (a0) with offset 0
		#
		# vs3 store data, rs1 base address, vm is mask encoding (v0.t or <missing>)
		# vse32.v vs3, (rs1), vm # 32-bit unit-stride store
		#
		vse32.v 	v1, 0(a0)				# store result from 'v1' to the 'x' vector (a0)
			
		add     	a0, a0, a6				# move the pointer to the 'x' vector forward by a stride's length
		
		bne     	a4, zero, .L6			# loop if there are still vector elements to process left
		
		ret									# return from the subroutine
		
.L8:
        add     a6, a0, a5
        sw      a7, 0(a6)
        addi    a5, a5, 4
        bne     a4, a5, .L15
		
.L13:	# return
        ret									# return from the subroutine
		
.L18:
        mv      a5, a6
        csrr    a6, vlenb
        addi    a6, a6, -8
        bgtu    a5, a6, .L6
		
.L3:
        slli    a4, a4, 2
        li      a5, 0
        li      t4, 1

# This might be manual computation of the result
# Maybe if the vectors are too short to fill the stride of the vector-engine ???
.L15:
        add     a6, a3, a5
        lw      a6, 0(a6)
        add     a7, a1, a5
        lw      a7, 0(a7)
        add     t3, a2, a5
        add     t1, a0, a5
        beq     a6, t4, .L8
        lw      a6, 0(t3)
        addi    a5, a5, 4
        #addw    a6, a6, a7
        add    a6, a6, a7
        sw      a6, 0(t1)
        bne     a4, a5, .L15
        ret
		
.L17:
        mv      a5, a7
        sub     a6, a6, a3
        bleu    a5, a6, .L5 				# jump to check against VLEN for potential computation without the vector-engine
        j       .L18