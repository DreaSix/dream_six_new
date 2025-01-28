package com.dream.six.enums;

public enum ABMEnum {
    DOMAIN("Domain"),
    COMPANY_NAME("Company Name"),
    NULL("Null");
    // '0-Domain,1-Company Name,2-Null',

    private final String value;

    ABMEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
