package ru.webrise.user_subscription_service.service;

import ru.webrise.user_subscription_service.dto.request.UserRequest;
import ru.webrise.user_subscription_service.dto.respone.UserResponse;
import ru.webrise.user_subscription_service.entity.User;

public interface UserService {

    User getEntityById(Long id);

    UserResponse getById(Long id);

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    void deleteById(Long id);

    boolean existsById(Long id);
}
