package com.dream.six.enums;

public enum MarketingMethodsEnum {

    TELE_MARKETING("Telemarketing"),
    EMAIL_MARKETING("Email Marketing"),
    CALL_VERIFIED("Call Verified");

    // '0-Telemarketing,1-Email Marketing,2-Call Verified'

    private final String value;

    MarketingMethodsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

