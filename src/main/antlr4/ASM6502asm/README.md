# Comments

A semicolon (;) starts a single line comment

# Registers

The 6502 processor has six 8-bit registers, with the exception of the Program Counter,
which is 16-bit. The registers are as follows:

Accumulator(A) - The accumulator can read and write to memory.
It is used to store arithmetic and logic results such as addition and subtraction.

X Index(X) - The x index is can read and write to memory.
It is used primarily as a counter in loops, or for addressing memory,
but can also temporarily store data like the accumulator.

Y Index(Y) - Much like the x index, however they are not completely
interchangeable. Some operations are only available for each register.

Flag(P) - The register holds value of 7 different flags which can only have a
value of 0 or 1 and hence can be represented in a single register.
The bits represent the status of the processor.

Stack Pointer(SP) - The stack pointer hold the address to the current location
on the Stack. The stack is a way to store data by pushing or popping data to
and from a section of memory.

Program Counter(PC) - This is a 16-bit register unlike other registers which
are only 8-bit in length, it indicates where the processor is in the program
sequence.

# Labels and Symbols

_usmul_4:

# Label and Symbol assignment

https://cc65.github.io/doc/ca65.html#ss6.1

io := $d000

# Numeric Constants

two = 2
mulplr	= $c0		; ZP location = $c0
partial	= mulplr+2	; ZP location = $c2
mulcnd	= partial+2	; ZP location = $c4

There is somehow a difference between labels and symbol definitions.
https://cc65.github.io/doc/ca65.html#ss6.1


# Addressing

## Immediate

https://stackoverflow.com/questions/68077785/what-is-the-significance-of-and-in-6502

The # symbol is used to specify immediate addressing:

LDA 0   ; Load the byte from address 0 in memory into register A
LDA #0  ; Load the value 0 into register A

LDA #$FF ; Load the value $FF into register A

## ???

lda ($00), Y

# Literals and Number Formats

see https://cc65.github.io/doc/ca65.html#ss4.6

$0001 - hex value
0001h - hex value

%1010 - binary value

1234 - decimal value

There are currently no octal values and no floats.

# Operators (Modifiers)

https://cc65.github.io/doc/ca65.html#ss5.5

## HI and LO modifier

lda #<340	; Low byte of 16-bit decimal value 340  (value: $54)
lda #>340	; High byte of 16-bit decimal value 340 (value: $01) (makes $0154)

# Mnemonics

lda
ldy

sta

adc

ror

dey

bit
bpl
bne

jsr

rts

pla
pha

tay
tya
