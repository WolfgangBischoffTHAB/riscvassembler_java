// cd /home/vbox/dev/riscv/rvv_add_example
//
// RISCV=/opt/riscv
// PATH=$RISCV/bin:$PATH
//
// riscv64-unknown-elf-gcc -O3 main.c -o main.elf -march=rv64gcv_zba -lm
// riscv64-unknown-elf-objdump -S -d main.elf > main.lst
//
// Important:
// With lesser optimization, the compiler will not emit RVV instructions!
// For example using optimization zero: -O0 will not emit RVV instructions!
// riscv64-unknown-elf-gcc -O0 main.c -o main.elf -march=rv64gcv_zba -lm

#include <stdio.h>

#define ELEMENTS 40

void foo(int *x, int *y, int *z, int n)
{
	for (int i = 0; i < n; ++i)
	{
		x[i] = y[i] + z[i];
	}
}

int main() {

    int x[40] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    int y[40] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    int z[40] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    foo(x, y, z, 40);

    // force the compiler to keep the vector addition by using printf()
    for (int i=0; i < 40; i++) {
        printf("%d\n", x[i]);
    }

    return 0;
}