package com.restaurant.userservice.core.service.user;

import com.restaurant.userservice.core.repository.IUserRepository;
import com.restaurant.userservice.core.service.user.dto.UpdateUserRequest;
import com.restaurant.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository _repo;

    private final static Logger _log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(
            IUserRepository repo
    ){
        this._repo = repo;
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public User getByIdAndThrow(Long id) {
        return null;
    }

    @Override
    public User getByEmailAndThrow(String email) {
        return null;
    }

    @Override
    public User getByPhoneNumberAndThrow(String phoneNumber) {
        return null;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return _repo.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return _repo.existsByEmail(email);
    }

    @Override
    public boolean deleteUser(UUID id) {
        return false;
    }

    @Override
    public User updateUser(UUID id, UpdateUserRequest request) {
        return null;
    }

    @Override
    public User save(User entity) {
        if(entity != null){
            User user = _repo.save(entity);
            _log.info("Saved user {}", user.getId());
            return user;
        }
        _log.warn("The user is null");
        return null;
    }

    @Override
    public List<User> getActivedUser(int page, int size, String sort, String sortDirection) {
        return List.of();
    }

    @Override
    public List<User> getAllUser(int page, int size, String sort, String sortDirection) {
        return List.of();
    }

    @Override
    public List<User> getUsersByIds(List<UUID> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return new ArrayList<>();
        }
        return _repo.getUsersByIds(ids);
    }
}
