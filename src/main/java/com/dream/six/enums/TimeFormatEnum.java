package com.dream.six.enums;

public enum TimeFormatEnum {
    YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND("yyyy-MM-dd-HH-mm-ss-SSS"),
    YEAR_MONTH_DAY_HOUR_MINUTE_SECOND("yyyy-MM-dd-HH-mm-ss"),
    YEAR_MONTH_DAY("yyyy-MM-dd"),
    HOUR_MINUTE_SECOND("HH:mm:ss"),
    HOUR_MINUTE("HH:mm");

    private final String value;

    TimeFormatEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
