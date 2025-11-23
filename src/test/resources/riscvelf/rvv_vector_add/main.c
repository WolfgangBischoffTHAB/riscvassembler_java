// cd /home/vbox/dev/riscv/rvv_add_example
//
// RISCV=/opt/riscv
// PATH=$RISCV/bin:$PATH
//
// riscv64-unknown-elf-gcc -O3 main.c -o main.elf -march=rv64gcv_zba -lm
// riscv64-unknown-elf-objdump -S -d main.elf > main.lst

#define ELEMENTS 40

void foo(int *x, int *y, int *z, int n)
{
	for (int i = 0; i < n; ++i)
	{
		x[i] = y[i] + z[i];
	}
}

int main() {

    int x[ELEMENTS] = { 0, 0, 0, 0 };
    int y[ELEMENTS] = { 1, 2, 3, 4 };
    int z[ELEMENTS] = { 1, 2, 3, 4 };

    foo(x, y, z, ELEMENTS);

    return 0;
}