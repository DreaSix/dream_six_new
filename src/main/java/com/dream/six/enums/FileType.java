package com.dream.six.enums;

public enum FileType {

    SUPPRESSION("Suppression"),
    ICP("ICP"),
    UNSUBSCRIBE("Unsubscribe"),
    LEAD("Lead"),
    QA_LEAD("qa-lead");

    private final String value;

    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
