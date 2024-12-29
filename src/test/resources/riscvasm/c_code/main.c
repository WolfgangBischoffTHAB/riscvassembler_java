//#include <stdio.h>

int add3(int a, int b, int c) {
    return a + b + c;
}

int main(int argc, char *argv[]) {

    int a = 23;
    int b = 24;
    int c = 25;

    //printf("a = %d\n", a);
    //printf("b = %d\n", b);
    //printf("c = %d\n", c);

    int result = add3(a, b, c);

    return result;
}