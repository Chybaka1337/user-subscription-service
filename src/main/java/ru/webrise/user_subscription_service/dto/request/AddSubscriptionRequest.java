package ru.webrise.user_subscription_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddSubscriptionRequest(
        @NotBlank String name
) { }
