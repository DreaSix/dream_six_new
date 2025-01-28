package com.dream.six.enums;

import lombok.Getter;

@Getter
public enum Status {

    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected");
    private final String statusValue;

    Status(String statusValue) {
        this.statusValue = statusValue;
    }

}
