package com.restaurant.userservice.core.service;

import com.restaurant.userservice.model.User;

public interface IUserService {
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    User findById(Long id);
    User getByIdAndThrow(Long id);
    User getByEmailAndThrow(String email);
    User getByPhoneNumberAndThrow(String phoneNumber);

    User save(User entity);
}
