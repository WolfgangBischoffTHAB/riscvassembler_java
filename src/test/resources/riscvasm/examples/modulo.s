	lui x5, 15
	lui x6, 6

module_label:
	blt x5, x6, modulo_end
	sub x5, x5, x6
	mv x7, x5
	j module_label
	
modulo_end: