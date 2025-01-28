package com.dream.six.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.sql.Timestamp;
import java.time.Instant;

public class BaseEntityListener {

    @PrePersist
    public void prePersist(BaseEntity entity) {
        Timestamp now = Timestamp.from(Instant.now());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        entity.setUpdatedAt(Timestamp.from(Instant.now()));
    }
}