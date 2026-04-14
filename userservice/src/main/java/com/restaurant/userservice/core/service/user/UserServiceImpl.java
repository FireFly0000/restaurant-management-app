package com.restaurant.userservice.core.service.user;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.exception.AppException;
import com.restaurant.userservice.core.repository.IUserRepository;
import com.restaurant.userservice.core.service.user.dto.UpdateUserRequest;
import com.restaurant.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        Optional<User> obj = _repo.findByEmail(email);

        if(obj.isPresent()){
            _log.debug("findByEmail, user is found with {}", email);
            return obj.get();
        }

        _log.warn("findByEmail, user not found with email {}", email);
        return null;
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public User findById(UUID id) {
        Optional<User> obj = _repo.findById(id);

        if(obj.isPresent()){
            _log.debug("findById, user is found with {}", id);
            return obj.get();
        }

        _log.warn("findById, user not found with {}", id);
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
    public List<User> saveAll(List<User> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            _log.warn("saveAll, User list is empty");
            return new ArrayList<>();
        }
        List<User> savedUsers = _repo.saveAll(entities);
        _log.info("saveAll, Saved {} users", savedUsers.size());
        return savedUsers;
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

    @Override
    public User verifyAccount(UUID id) {
        User user = findById(id);

        if (user == null) {
            return null;
        }

        user.setIsVerified(true);
        return save(user);
    }

    @Override
    public User updatePassword(UUID id, String newPassword) {
        User user = findById(id);

        if (user == null) {
            _log.warn("updatePassword, user not found with id={}", id);
            return null;
        }

        user.setPassword(newPassword);
        return save(user);
    }
}
