package com.dream.six.enums;

public enum PacingEnum {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    BI_WEEKLY("Bi-Weekly"),
    MONTHLY("Monthly"),
    FRONTLOAD("Frontload"),
    ASAP("ASAP");

    private final String value;

    PacingEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
