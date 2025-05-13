package com.mycompany.data;

import java.util.Map;

import ch.qos.logback.core.util.StringUtil;

public class ASTNode {

    public ASTNode parent;

    public boolean isRegister;

    public Register register;

    public boolean isNumeric;

    public long numeric;

    public boolean isHexNumeric;

    public long hexNumeric;

    public boolean isStringLiteral;

    public String identifier;

    public String operatorAsString;

    public ASTNode rhs;

    public ASTNode lhs;

    public void replace(Map<String, Object> equMap) {

        if (isStringLiteral && equMap.containsKey(identifier)) {
            long temp = (long) equMap.get(identifier);

            isStringLiteral = false;
            identifier = null;

            isNumeric = true;
            numeric = temp;
        }

        // replaces values in children
        if (lhs != null) {
            lhs.replace(equMap);
        }
        if (rhs != null) {
            rhs.replace(equMap);
        }

    }

    public Long evaluate() {

        if (StringUtil.isNullOrEmpty(operatorAsString)) {
            return retrieveValue();
        }

        Long lhsValue = null;
        if (lhs != null) {
            lhsValue = lhs.evaluate();
        }

        Long rhsValue = null;
        if (rhs != null) {
            rhsValue = rhs.evaluate();
        }

        ASTNodeExpressionType operator = ASTNodeExpressionType.fromString(operatorAsString);
        switch (operator) {

            case ADD: {
                return lhsValue + rhsValue;
            }

            case SUB: {
                return lhsValue - rhsValue;
            }

            default:
                throw new RuntimeException("Unknown operand! " + operatorAsString);

        }
    }

    private Long retrieveValue() {

        if (isHexNumeric) {
            return this.hexNumeric;
        } else if (isNumeric) {
            return this.numeric;
        }

        if (isStringLiteral) {
            if ("pc".equalsIgnoreCase(identifier)) {
                // PC is the current PC. For a PC relative instruction such as BGE, PC is always
                // 0!
                return 0L;
            }
        }

        throw new RuntimeException("Unhandled return value type!");
    }

}
