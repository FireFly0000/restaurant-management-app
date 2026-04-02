package com.restaurant.userservice.core.repository;

import com.restaurant.userservice.model.UserInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface IUserInboxRepository extends JpaRepository<UserInbox, UUID> {
    List<UserInbox> findByConsumerNameAndEventIdIn(String consumerName, Collection<String> eventIds);

    @Query(value = """
        SELECT *
        FROM user_inbox
        WHERE status IN ('PENDING', 'RETRY', 'PROCESSING')
          AND next_retry_at <= NOW()
        ORDER BY created_at ASC
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<UserInbox> findBatchToProcess(@Param("limit") int limit);
}
