package com.interview.waiwingchoyProtfolioMonitor.enums;

public enum SecurityType {
    STOCK("stock"),
    CALL("call"),
    PUT("put"),
    OTHER("other");
    private final String value;

    SecurityType(String value) { this.value = value;}

    public static SecurityType fromValue(String value) {
        for (SecurityType type : SecurityType.values()) {
            if (type.value.equals(value))
                return type;
        }
        return OTHER;
    }
}
