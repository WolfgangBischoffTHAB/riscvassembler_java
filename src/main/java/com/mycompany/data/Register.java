package com.mycompany.data;

public interface Register {

    static <T extends Register> String toStringAbi(T register) {
        return register.toStringAbi();
    }

    static <T extends Register> String toString(T register) {
        return register.toString();
    }

    String toStringAbi();

    int getIndex();

}
