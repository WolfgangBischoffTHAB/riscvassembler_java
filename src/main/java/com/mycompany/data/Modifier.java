package com.mycompany.data;

public enum Modifier {

    HI,
    LO,
    PCREL_HI,
    PCREL_LO;

    public static Modifier fromString(final String modifier) {

        if (modifier.equalsIgnoreCase("%hi")) {
            return HI;
        } else if (modifier.equalsIgnoreCase("%lo")) {
            return LO;
        } else if (modifier.equalsIgnoreCase("%pcrel_hi")) {
            return PCREL_HI;
        } else if (modifier.equalsIgnoreCase("%pcrel_lo")) {
            return PCREL_LO;
        }

        throw new RuntimeException("Unknown modifier: \"" + modifier + "\"");
    }

    public static String toString(Modifier modifier) {
        switch (modifier) {
            case HI:
                return "%hi";
            case LO:
                return "%lo";
            case PCREL_HI:
                return "%pcrel_hi";
            case PCREL_LO:
                return "%pcrel_lo";
        }
        throw new UnsupportedOperationException("Unknown modifier " + modifier);
    }

}
