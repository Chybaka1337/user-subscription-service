package ru.webrise.user_subscription_service.service;

import ru.webrise.user_subscription_service.entity.Subscription;

import java.util.List;

public interface SubscriptionService {

    Subscription getByName(String name);

    List<String> getTop3PopularSubscriptions();
}
