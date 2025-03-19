package com.dream.six.repository;

import com.dream.six.entity.Updates;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UpdatesRepository extends JpaRepository<Updates, UUID> {
}

