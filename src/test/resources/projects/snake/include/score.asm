#.data

.text

Score: 
	#Fazer o t0 receber o valor da pontua�o
	
	li a7,101
	mv a0,s7
	li a1,75
	li a2,20
	li a3,0x00FF
	li a4,0
	ecall

	ret
