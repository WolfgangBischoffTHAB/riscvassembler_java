fail:
fence

main:
lui	    a1, 0x80000     # expecting 0x80000000 in a1/x11
addiw	a1, a1, -1
lui	    a2, 0x00008
addiw	a2, a2, -1
add	    a4, a1, a2      # expecting 0x80007FFE in a4/x14

lui	    t2, 0x10
addiw	t2, t2, 1
slli	t2, t2, 0xf
addi	t2, t2, -2

bne	    a4, t2, fail
nop
nop
nop


; lui	    a1, 0x80000
; addiw	a1, a1, -1
;
;     0000000080000000
; + 	FFFFFFFFFFFFFFFF
; -----------------------
; 	000000007FFFFFFF
;
; lui	    a2, 0x00008
; addiw	a2, a2, -1
;
;     0000000000008000
; + 	FFFFFFFFFFFFFFFF
; -----------------------
; 	0000000000007FFF
;
; add	    a4, a1, a2
;
; 	7FFFFFFF
; +	00007FFF
; ------------
; 	80007FFE



; lui	    t2, 0x10
; addiw	t2, t2, 1
;
;	        00010000
; +	0000000000000001
; --------------------
;    0000000000010001
;
; slli	t2, t2, 0xf
; addi	t2, t2, -2
;
; 	        80008000
; +	FFFFFFFFFFFFFFFE
; --------------------	
; 	        80007FFE
	


