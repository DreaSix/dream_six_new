package com.dream.six.enums;

public enum ProductEnum {
    CS("CS"),
    DT("DT"),
    DT_HQL("DT + HQL"),
    HQL("HQL"),
    BANT("BANT"),
    WATERFALL("Waterfall"),
    DISPLAY_CS_RETARGETING("Display - CS Retargeting"),
    DISPLAY_NATIVE("Display - Native"),
    DISPLAY_TRADITIONAL_RETARGETING("Display - Traditional Retargeting"),
    DISPLAY_VIDEO("Display - Video"),
    EMAIL_BLAST_CPM("Email Blast/CPM"),
    EMAIL_NURTURE("Email Nurture"),
    LIVE_WEBINAR("Live Webinar"),
    OPT_IN("Opt-In"),
    SURVEY("Survey"),
    TRIAL_DEMO_PROMOTION("Trial/Demo Promotion");

    private final String value;

    ProductEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
