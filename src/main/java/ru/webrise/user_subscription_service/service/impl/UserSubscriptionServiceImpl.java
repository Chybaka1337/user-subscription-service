package ru.webrise.user_subscription_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.webrise.user_subscription_service.converter.UserSubscriptionMapper;
import ru.webrise.user_subscription_service.dto.request.AddSubscriptionRequest;
import ru.webrise.user_subscription_service.dto.respone.UserSubscriptionResponse;
import ru.webrise.user_subscription_service.entity.Subscription;
import ru.webrise.user_subscription_service.entity.User;
import ru.webrise.user_subscription_service.entity.UserSubscription;
import ru.webrise.user_subscription_service.exception.AlreadyExistException;
import ru.webrise.user_subscription_service.exception.NotFoundException;
import ru.webrise.user_subscription_service.repository.UserSubscriptionRepository;
import ru.webrise.user_subscription_service.service.SubscriptionService;
import ru.webrise.user_subscription_service.service.UserService;
import ru.webrise.user_subscription_service.service.UserSubscriptionService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserService userService;

    private final SubscriptionService subscriptionService;

    private final UserSubscriptionRepository userSubscriptionRepository;

    private final UserSubscriptionMapper userSubscriptionMapper;

    @Override
    @Transactional
    public void addSubscription(Long userId, AddSubscriptionRequest request) {
        log.debug("addSubscription(): userId={} request={}", userId, request);
        User user = userService.getEntityById(userId);

        Subscription subscription = subscriptionService.getByName(request.name());

        if (userSubscriptionRepository.existsByUserIdAndSubscriptionId(userId, subscription.getId())) {
            log.warn("addSubscription(): subscription id={} already exist for user id ={}",
                    subscription.getId(), user.getId());
            throw new AlreadyExistException("Subscription already exist for user id" + userId);
        }

        UserSubscription userSubscription = UserSubscription.builder()
                .user(user)
                .subscription(subscription)
                .createdAt(LocalDateTime.now())
                .build();
        userSubscriptionRepository.save(userSubscription);
        log.info("addSubscription(): added subscription id={} to user id={}",
                subscription.getId(), user.getId());
    }

    @Override
    @Transactional
    public void removeSubscription(Long userId, Long subscriptionId) {
        log.debug("removeSubscription(): userId={}, subscriptionId={}", userId, subscriptionId);
        userService.getEntityById(userId);

        if (!userSubscriptionRepository.existsByUserIdAndSubscriptionId(userId, subscriptionId)) {
            log.warn("removeSubscription(): no subscription id={} for user id={}",
                    subscriptionId, userId);
            throw new NotFoundException("UserSubscription not found for user id" + userId +
                    " and subscription id" + subscriptionId);
        }

        userSubscriptionRepository.deleteByUserIdAndSubscriptionId(userId, subscriptionId);
        log.info("removeSubscription(): removed subscription id={} from user id={}",
                subscriptionId, userId);
    }

    @Override
    public List<UserSubscriptionResponse> getUserSubscriptions(Long userId) {
        log.debug("getUserSubscriptions(): userId={}", userId);
        userService.getEntityById(userId);

        List<UserSubscriptionResponse> list = userSubscriptionRepository.findAllByUserId(userId)
                .stream()
                .map(userSubscriptionMapper::toResponse)
                .toList();
        log.info("getUserSubscriptions(): user id={} has {} subscriptions", userId, list.size());
        return list;
    }
}
