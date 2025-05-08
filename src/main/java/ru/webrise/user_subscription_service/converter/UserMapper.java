package ru.webrise.user_subscription_service.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.webrise.user_subscription_service.dto.request.UserRequest;
import ru.webrise.user_subscription_service.dto.respone.UserResponse;
import ru.webrise.user_subscription_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    void update(@MappingTarget User user, UserRequest request);
}
