# riscvassembler_java

riscvassembler written in Java

## Testsuite

src/test/resources/riscvasm/examples/argmax.s
src/test/resources/riscvasm/examples/blinker.s
src/test/resources/riscvasm/examples/memory.s
src/test/resources/riscvasm/examples/uart.s
src/test/resources/riscvasm/examples/modifiers.s
src/test/resources/riscvasm/examples/hello_world.s // fails! la is resolved differently

src/test/resources/riscvasm/examples/function.s // GNU riscv elf 32 bit does not know ld sd instructions because double word is 64 bit

next steps:
fix:
src/test/resources/riscvasm/examples/hello_world.s
src/test/resources/riscvasm/instructions/li_wierd_encoding_5.s 

copy the source code from the samples above into src/test/resources/riscvasm/test.s.
Run App.java and also run the Makefile src/test/resources/riscvasm/Makefile
Compare both results. The results have to match.

## Registers

| Register Name  | ABI Name  | Description  | Saved By  |
|---|---|---|---|
| x0  | zero | Always zero  |   |
| x1  | ra  | Return address  | Caller  |
| x2  | sp  | Stack pointer  | Callee  |
| x3  | gp  | Global pointer  |   |
| x4  | tp  | Thread pointer  |   |
| x5  | t0  | Temporary / alternate return address  | Caller  |
| x6  | t1  | Temporary  | Caller  |
| x7  | t2  | Temporary  | Caller  |
| x8  | s0 / fp | Saved register / frame pointer | Callee |
| x9 | s1 | Saved register | Callee |
| x10 | a0 | Function arguments / return values | Caller |
| x11 | a1 | Function arguments / return values | Caller |
| x12 | a2 | Function arguments | Caller |
| x13 | a3 | Function arguments | Caller |
| x14 | a4 | Function arguments | Caller |
| x15 | a5 | Function arguments | Caller |
| x16 | a6 | Function arguments | Caller |
| x17 | a7 | Function arguments | Caller |
| x18 | s2 | Saved registers | Callee |
| x19 | s3 | Saved registers | Callee |
| x20 | s4 | Saved registers | Callee |
| x21 | s5 | Saved registers | Callee |
| x22 | s6 | Saved registers | Callee |
| x23 | s7 | Saved registers | Callee |
| x24 | s8 | Saved registers | Callee |
| x25 | s9 | Saved registers | Callee |
| x26 | s10 | Saved registers | Callee |
| x27 | s11 | Saved registers | Callee |
| x28 | t3 | Temporaries | Caller |
| x29 | t4 | Temporaries | Caller |
| x30 | t5 | Temporaries | Caller |
| x31 | t6 | Temporaries | Caller |


## RISCV GNU Toolchain

https://github.com/riscv-collab/riscv-gnu-toolchain

## Online RISCV assemblers

NOTE: Watch out when using online assemblers! Online assemblers are preconfigured
and do not allow you to fine tune parameters. 

For example there is no way to tell the toolchain to output 32 bit or 64 bit code
using a instruction in your source file! Instead, the toolchain itself is compiled
to output either 32 or 64 bit code!

Since 32 or 64 bit code has drastic impact on the machine code that is produced
(see: https://stackoverflow.com/questions/79295083/why-encode-riscv-pseudoinstruction-li-to-four-instructions-instead-of-two)
Make sure that you use an online assembler that is preconfigured exactly matching
your requirements.

If you cannot find an online assembler that is configured to suit your requirements,
go ahead and download one of the precompiled GNU RISCV toolchains from https://github.com/riscv-collab/riscv-gnu-toolchain instead. Using the toolchain, you have fine grained control over what code is generated.

The downside of the GNU toolchain is that it is not easily available on windows and you
would have to use Linux which is the exact reason why online assembler are attractive to
windows users that want to do some quick experiments.

Here is a list of online assemblers and encoders:

https://luplab.gitlab.io/rvcodecjs/

https://rizwan3d.github.io/SharpRISCV

https://riscvasm.lucasteske.dev/# (encodes to 64 bit machine code by default and cannot be changed to 32 bit)

https://ripes.me/ (encodes to 32 bit machine code. It seems that Ripes version: v2.2.6-49-g290c0e6
    is not able to assemble, when .data sections are defined and data is placed into those data sections
    using .byte or .space)

## Assembling using the RISCV gnu toolchain

Assumption: You have downloaded the riscv32-elf-ubuntu-22.04-gcc-nightly-2024.12.16-nightly.tar.xz file
from https://github.com/riscv-collab/riscv-gnu-toolchain
and extracted it to your user's Downloads folder. A riscv folder has been created by extracting the 
archive and this riscv folder now contains the 32 bit elf toolchain for linux.

For Windows, maybe this precompiled version will work: https://gnutoolchains.com/risc-v/
I have never tested it!

Options: https://gcc.gnu.org/onlinedocs/gcc/RISC-V-Options.html

Cheatsheet / Quickstart:

```
cd riscvassembler_java/src/test/resources/riscvasm
rm a.out intermediate.o disassembly.txt listing.lst
~/Downloads/riscv/bin/riscv32-unknown-elf-as -mabi=ilp32 -march=rv32i -misa-spec=2.2 -ahls -o intermediate.o test.s > listing.lst
~/Downloads/riscv/bin/riscv32-unknown-elf-ld --no-relax --no-check-uleb128 --verbose -T ../linker_script/standard.ld intermediate.o -o a.out
~/Downloads/riscv/bin/riscv32-unknown-elf-objdump -D -S a.out > disassembly.txt
~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy --only-section .text* -O binary a.out hex_test.txt
xxd -u -plain -cols 4 -groupsize 4 hex_test.txt
```

readelf cheatsheet:

output relocations if any exist
``` 
readelf --relocs a.out
```

output sections
```
readelf --sections a.out
```

On linux, current versions of the gnu as assembler create object files in the elf file format.
For cross compilation PE or MachO could be created.
There is also the old a.out format.

```
~/Downloads/riscv/bin/riscv32-unknown-elf-as -help
~/Downloads/riscv/bin/riscv32-unknown-elf-as --dump-config
~/Downloads/riscv/bin/riscv32-unknown-elf-as -mabi=ilp32 -march=rv32i -misa-spec=2.2 -ahls -o intermediate.o test.s > listing.lst
```

```
~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy -O srec intermediate.o a.srec
~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy -O ihex intermediate.o a.hex
~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy -O binary intermediate.o a.binary

~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy --only-section .text* -O binary a.out hex_test.txt
xxd -u -plain -cols 4 -groupsize 4 hex_test.txt
```

```
~/Downloads/riscv/bin/riscv32-unknown-elf-objdump -s intermediate.o > dump.txt
~/Downloads/riscv/bin/riscv32-unknown-elf-objdump -D -S intermediate.o > disassembly.txt
```

After running the assembler, there are relocation entries added which are only
resolved by the linker. In the listing that the assembler outputs, labels have
no correct addresses and the encoded instructions are not correct yet!
See.: https://stackoverflow.com/questions/78741255/risc-v-i-dont-understand-what-the-gnu-assembler-does-with-labels-in-the-data

The linker is allowed to apply actions to the code that will actually change
the assembly instructions that are placed into the binary!

At this point of the development of my own assembler, optimizations
are not part of the picture just yet. Therefore if the GNU linker optimizes
the output, I cannot compare my assembler's output to the GNU toolchain output 
any more because instructions are removed or added and therefore addresses change.
Addresses are encoded into instructions in RISCV assembly so the entire output is
not comparable any more!

I want to disable any optimizations if possible to be able to compare the outputs
of my own assembler to the output of the riscv GNU toolchain.

One flag to prevent the linker from removing zero loads into registers, is to
use the --no-relax --no-check-uleb128 flags. 

In a random attempt for turning off optimizations I added these flags without 
really knowing what they do but after adding those flags, the source code that 
the linker outputs remained unoptmized which is exactly what I was hoping for!

The linker will now use the assembler's code and only replace addresses in code
but not throw away instructions. (As far as I can tell. More tests are necessary!)

Now lets use the linker so it resolves the relocation entries that the assembler
has inserted into the elf formatted object file.

```
~/Downloads/riscv/bin/riscv32-unknown-elf-ld -help
~/Downloads/riscv/bin/riscv32-unknown-elf-ld --no-relax --no-check-uleb128 --verbose -T ../linker_script/standard.ld intermediate.o -o a.out
```

```
~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy -O srec a.out a.srec
~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy -O ihex a.out a.hex
~/Downloads/riscv/bin/riscv32-unknown-elf-objcopy -O binary a.out a.binary
```

```
~/Downloads/riscv/bin/riscv32-unknown-elf-objdump -s a.out > dump.txt
~/Downloads/riscv/bin/riscv32-unknown-elf-objdump -D -S a.out > disassembly.txt
```

The disassembly listing should now contain the original sourcecode with addresses
and pseudo instructions resolved!

The GNU GCC (C/C++ compiler) can also be used to translate assembly to machine code.
(Not tested yet!)

```
riscv32-unknown-elf-gcc -march=rv32im -mabi=ilp32 -nostartfiles -O3 -x c -Wl,-T,/work/test5.x -o test.o test.c
riscv64-unknown-elf-gcc -I /usr/riscv64-linux-gnu/include -nostdlib hello.S -o hello
```

https://stackoverflow.com/questions/64745868/how-do-i-set-up-instruction-data-memory-address-when-using-riscv32-unknown-el
