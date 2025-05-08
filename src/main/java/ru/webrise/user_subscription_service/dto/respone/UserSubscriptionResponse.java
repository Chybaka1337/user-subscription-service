package ru.webrise.user_subscription_service.dto.respone;

import java.time.LocalDateTime;

public record UserSubscriptionResponse(
    Long subscriptionId,
    String name,
    LocalDateTime createdAt
) { }
