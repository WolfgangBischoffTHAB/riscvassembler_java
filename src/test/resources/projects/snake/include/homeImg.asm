.text

# Frame 0 starts at 0xFF[0]00000 and ends at 0xFF[0]12BFF
# Frame 1 starts at 0xFF[1]00000 and ends at 0xFF[1]12BFF

PrintHomeImg:	
	li 		t1, 0xFF100000		# endereco inicial da Memoria VGA - Frame 1 (this is frame 1, frame 0 would be: 0xFF000000)
	li 		t2, 0xFF112C00		# endereco final 
	li 		t5, 1				# Frame 1
	li 		t4, 0xFF200604
	la 		s8, HomeImg			# endereo dos dados da tela na memoria
	addi 	s8, s8, 8			# primeiro pixels depois das informaes de nlin ncol

LoopHomeImg: 	
	beq 	t1, t2, EndHomeImg	# Se for o ultimo endereo entro sai do loop
	lw 		t3, 0(s8)			# le um conjunto de 4 pixels : word
	sw 		t3, 0(t1)			# escreve a word na memoria VGA
	addi 	t1, t1, 4			# soma 4 ao endereo
	addi 	s8, s8, 4
	j 		LoopHomeImg			# volta a verificar

EndHomeImg:
	sw 		t5, (t4) 			# Muda Frame
	ret
