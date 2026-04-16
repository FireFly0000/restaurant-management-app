package com.restaurant.userservice.core.service.user;

import com.restaurant.userservice.core.service.user.dto.UpdateUserInfoRequest;
import com.restaurant.userservice.core.service.user.dto.UpdateUserInfoResponse;
import com.restaurant.userservice.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    User findById(UUID id);
    User getByIdAndThrow(Long id);
    User getByEmailAndThrow(String email);
    User getByPhoneNumberAndThrow(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean deleteUser(UUID id);
    UpdateUserInfoResponse updateUserInfo(UUID id, UpdateUserInfoRequest request);
    User updatePassword(UUID id, String newPassword);
    User save(User entity);
    List<User> saveAll(List<User> entities);
    List<User> getActivedUser(int page, int size, String sort, String sortDirection);
    List<User> getAllUser(int page, int size, String sort, String sortDirection);
    List<User> getUsersByIds(List<UUID> ids);
    User verifyAccount(UUID id);
}
