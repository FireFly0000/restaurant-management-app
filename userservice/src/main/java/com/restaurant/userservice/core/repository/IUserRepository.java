package com.restaurant.userservice.core.repository;

import com.restaurant.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u where u.id IN (:ids) AND u.deletedAt = 0")
    List<User> getUsersByIds(List<UUID> ids);
}
