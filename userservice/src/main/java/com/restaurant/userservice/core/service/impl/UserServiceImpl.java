package com.restaurant.userservice.core.service.impl;

import com.restaurant.userservice.core.repository.IUserRepository;
import com.restaurant.userservice.core.service.IUserService;
import com.restaurant.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    public User save(User entity) {
        if(entity != null){
            User user = _repo.save(entity);
            _log.info("Saved user {}", user.getId());
            return user;
        }
        _log.warn("The user is null");
        return null;
    }
}
