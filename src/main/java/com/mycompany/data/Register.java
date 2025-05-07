package com.mycompany.data;

public interface Register {

    static <T extends Register> String toStringAbi(T register) {
        //return register.toString();
        return register.toStringAbi();
    }

    String toStringAbi();

    int getIndex();

}
