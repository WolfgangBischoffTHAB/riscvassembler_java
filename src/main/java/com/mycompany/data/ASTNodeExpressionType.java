package com.mycompany.data;

public enum ASTNodeExpressionType {

    ADD("+"),

    SUB("-"),

    UNKNOWN("UNKNOWN");

    private String name;

    ASTNodeExpressionType(final String name) {
        this.name = name;
    }

    public static ASTNodeExpressionType fromString(final String operator) {

        if (operator.equalsIgnoreCase("+")) {
            return ADD;
        } else if (operator.equalsIgnoreCase("-")) {
            return SUB;
        }

        throw new RuntimeException("Unknown instruction: \"" + operator + "\"");
    }

    public static String toString(final ASTNodeExpressionType operator) {

        switch (operator) {

            case ADD:
                return "+";

            case SUB:
                return "-";

            default:
                throw new RuntimeException("Unknown operator: \"" + operator + "\"");
        }
    }

}
