package com.restaurant.businessservice.core.repository;

import com.restaurant.businessservice.model.BusinessOutBox;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface IBusinessOutboxRepository extends JpaRepository<BusinessOutBox, UUID> {
    // Claim only message ids so the scheduler does not load the payload column for the whole batch.
    @Query(value = """
        SELECT id FROM business_outbox
        WHERE status IN ('PENDING', 'RETRY', 'PROCESSING')
          AND next_retry_at <= NOW()
        ORDER BY created_at ASC
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<UUID> findBatchIdsToProcess(@Param("limit") int limit);

    @Modifying
    @Query(value = """
        UPDATE business_outbox
        SET status = :status,
            next_retry_at = :nextRetryAt
        WHERE id IN (:ids)
    """, nativeQuery = true)
    int claimBatch(
            @Param("ids") List<UUID> ids,
            @Param("status") String status,
            @Param("nextRetryAt") Instant nextRetryAt
    );
}
