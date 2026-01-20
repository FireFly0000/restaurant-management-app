package com.restaurant.userservice.core.repository;

import com.restaurant.userservice.model.UserOutBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IUserOutboxRepository extends JpaRepository<UserOutBox, UUID> {
    @Query(value = """
        SELECT * FROM user_outbox 
        WHERE status IN ('PENDING', 'RETRY') 
          AND next_retry_at <= NOW()
        ORDER BY created_at ASC 
        LIMIT :limit 
        FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<UserOutBox> findBatchToProcess(@Param("limit") int limit);
}
