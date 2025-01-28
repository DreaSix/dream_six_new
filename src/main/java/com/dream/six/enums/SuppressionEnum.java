package com.dream.six.enums;

public enum SuppressionEnum {

    ALL("All"),
    DOMAIN("Domain"),
    EMAIL("Email"),
    NULL("NULL"),
    COMPANY_NAME("Company Name");

    //'0-All, 1-Domain,2-Email,3-Company Name,4-Null',
    private final String value;

    SuppressionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
