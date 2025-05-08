package ru.webrise.user_subscription_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.webrise.user_subscription_service.entity.Subscription;
import ru.webrise.user_subscription_service.exception.NotFoundException;
import ru.webrise.user_subscription_service.repository.SubscriptionRepository;
import ru.webrise.user_subscription_service.service.SubscriptionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription getByName(String name) {
        log.debug("getByName(): fetching subscription with name={}", name);
        return subscriptionRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("getByName(): subscription with name={} not found", name);
                    return new NotFoundException("Subscribe not found with this name " + name);
                });

    }

    @Override
    public List<String> getTop3PopularSubscriptions() {
        log.debug("getTop3PopularSubscriptions(): querying top-3 popular");
        List<String> top3PopularSubscriptions = subscriptionRepository.findTop3PopularSubscriptions();
        log.info("getTop3PopularSubscriptions(): top-3 = {}", top3PopularSubscriptions);
        return top3PopularSubscriptions;
    }
}
