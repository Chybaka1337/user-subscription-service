package ru.webrise.user_subscription_service.service;

import ru.webrise.user_subscription_service.dto.request.AddSubscriptionRequest;
import ru.webrise.user_subscription_service.dto.respone.UserSubscriptionResponse;
import ru.webrise.user_subscription_service.entity.UserSubscription;

import java.util.List;

public interface UserSubscriptionService {

    void addSubscription(Long userId, AddSubscriptionRequest request);

    void removeSubscription(Long userId, Long subscriptionId);

    List<UserSubscriptionResponse> getUserSubscriptions(Long userId);
}
