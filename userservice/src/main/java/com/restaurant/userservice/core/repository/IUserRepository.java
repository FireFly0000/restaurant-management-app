package com.restaurant.userservice.core.repository;

import com.restaurant.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
}
