package com.dream.six.enums;

public enum LeadEnum {

    QUALIFIED("Qualified"),
    RECTIFIED("Rectified"),
    DISQUALIFIED("Disqualified"),
    VALID("Valid"),
    TBD("To Be Determined"),
    ZB("ZB"),
    E_REJECTED("E-Rejected");

    private final String value;

    LeadEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
