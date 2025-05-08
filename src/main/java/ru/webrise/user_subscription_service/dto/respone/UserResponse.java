package ru.webrise.user_subscription_service.dto.respone;

public record UserResponse(
        Long id,
        String username,
        String email
) { }
