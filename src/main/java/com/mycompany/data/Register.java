package com.mycompany.data;

public interface Register {

    static <T extends Register> String toStringAbi(T register) {
        return register.toString();
    }

    int getIndex();

}
