
.text
	
Frutinha:

#la tp,exceptionHandling	# carrega em tp o endereo base das rotinas do sistema ECALL
#csrw tp,utvec 		# seta utvec para o endere�o tp
#csrsi ustatus,1 	# seta o bit de habilita��o de interrup��o em ustatus (reg 0)
				
								
addi sp, sp, -20
sw s0, 0(sp)
sw s1, 4(sp)
sw s2, 8(sp)
sw s10, 12(sp)
sw s11, 16(sp)


		li a0, 1
		addi s10, ra, 0		#Salva o endere�o de retorno, para a fun��o chamou pela primeira vez	
		jal randomNew				# S�o gerados pares de num. aleat�rios
	Fim:
		addi a0, t0,0
		addi s2, a0, 0 #Guarda coordenada do ponto
		li a1, 0x07070707		#cor vermelha do ponto
		jal PontoDireita
		
		addi ra, s10, 0		#retorna o valor original de ra
		
		addi a0, s2, 0# Retorna coordenada do ponto em a0
		lw s0, 0(sp)#*********************************************************************
		lw s1, 4(sp)
		lw s2, 8(sp)
		lw s10, 12(sp)
		lw s11, 16(sp)
		addi sp, sp, 20
		#####################################
#		li a7, 10
#		ecall
		########################################
		ret
randomNew:
		addi t5, s0, 0				# Salva valor de s0, antes de usa-lo
		addi s0, a0, 0				# Salva o n�mero de pares X Y
		#addi s1, sp, 0				# Endere�o atual da pilha
		slli t0, s0, 2				# Multiplica s0 por 4
		addi t0, t0, 4				# t0 agora est� em 32 bits
		#sub sp, sp, t0				# Monta pilha
		#sw s0, (sp)				# Salva o n�mero de itens na estrutura
		#addi s0, sp, 4				# Anda para o proximo item da pilha
		
		li t4, 2			#constante contendo o menor valor q x pode assumir
		slli t4, t4, 16 		
		li t6, 6			#constante contendo o menor valor q y pode assumir
		li s11, 1
		
random_for:
		slt t3, t2, t6					# compara se o valor aleatorio de y � menor q a constante t6 
		beq t3, s11, Outro_Ponto		# se for menor, escolho outro ponto, se nao verifica o valor de x
		
		slt t3, t1, t4					# verifica se o valor de x � menor q t4
		beq t3, zero, random_exit		# se for maior termina o loop, se for menor continua no loop
		
		
		Outro_Ponto:
		li a7, 41						# cahamada para gerar um num aleat�rio em a0
		ecall							# a0 retorna com um num. aleat�rio
		li t0, 0x00000026				# Limita��o em X
# wbi has removed this
#		remu t1, a0, t0					# Resto da divis�o de a0 por 40
	
		slli t1, t1, 16					# X ocupa os 16 MSB de t1
		li a7, 41				
		ecall							# a0 retorna com um num. aleat�rio
		li t0, 0x0000001B				# Limita��o de Y
# wbi has removed this
#		remu t2, a0, t0					# Resto da divis�o de a0 por 320

		or t0, t1, t2					# une os valores de X e Y numa mesma word de 32 bits
		#sw t0, (s0)					# Salva o numero
		addi s0, s0, 4					# s0 � incrementado

		j random_for

random_exit:

		#addi a0, sp, 0				# a0 recebe End. de retorno
		addi s0, t5, 0
		add a1, zero,a0				# Valor de s0 � recuperado
		j Fim
		
#.include "PontoDireita.asm"
#.include "SYSTEMv17b.s"
