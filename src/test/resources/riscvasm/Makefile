.DEFAULT_GOAL := output_hex_dump

.PHONY: clean
clean:
	rm a.out intermediate.o disassembly.txt listing.lst hex_test.txt

intermediate.o: test.s 
	~/Downloads/riscv/bin/riscv32-unknown-elf-as -mabi=ilp32 -march=rv32i -misa-spec=2.2 -ahls -o intermediate.o test.s > listing.lst

a.out: intermediate.o
	~/Downloads/riscv/bin/riscv32-unknown-elf-ld --no-relax --no-check-uleb128 --verbose -T ../linker_script/standard.ld intermediate.o -o a.out

disassemble: a.out
	~/Downloads/riscv/bin/riscv32-unknown-elf-objdump -D -S a.out > disassembly.txt

hex_test.txt: a.out
	~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy --only-section .text* -O binary a.out hex_test.txt

output_hex_dump: hex_test.txt
	xxd -u -plain -cols 4 -groupsize 4 hex_test.txt