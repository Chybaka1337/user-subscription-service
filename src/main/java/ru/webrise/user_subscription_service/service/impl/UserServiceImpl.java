package ru.webrise.user_subscription_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.webrise.user_subscription_service.converter.UserMapper;
import ru.webrise.user_subscription_service.dto.request.UserRequest;
import ru.webrise.user_subscription_service.dto.respone.UserResponse;
import ru.webrise.user_subscription_service.entity.User;
import ru.webrise.user_subscription_service.exception.NotFoundException;
import ru.webrise.user_subscription_service.repository.UserRepository;
import ru.webrise.user_subscription_service.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        log.debug("create(): received request: {}", request);
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        log.info("create(): user created with id={}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public User getEntityById(Long id) {
        log.debug("getEntityById(): checking existence for id={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("getEntityById(): user with id={} not found", id);
                    return new NotFoundException("User not found with id " + id);
                });
    }

    @Override
    public UserResponse getById(Long id) {
        log.debug("getById(): fetching user with id={}", id);
        User user = getEntityById(id);
        log.info("getById(): found user with id={} and username={}", id, user.getUsername());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        log.debug("update(): updating user id={} with {}", id, request);
        User user = getEntityById(id);
        userMapper.update(user, request);
        User updated = userRepository.save(user);
        log.info("update(): user id={} updated", updated.getId());
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("delete(): deleting user with id={}", id);
        if (!userRepository.existsById(id)) {
            log.warn("delete(): user with id={} not found", id);
            throw new NotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
        log.info("delete(): user with id={} deleted", id);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
