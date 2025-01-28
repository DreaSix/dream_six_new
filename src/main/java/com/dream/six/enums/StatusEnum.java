package com.dream.six.enums;

public enum StatusEnum {

    LIVE("Live"),
    NOT_LIVE("Not Live"),
    PAUSED("Paused"),
    COMPLETED("Completed");

//'0-Live,1-Not Live,2-Paused,3-Completed',

    private final String value;

    StatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

