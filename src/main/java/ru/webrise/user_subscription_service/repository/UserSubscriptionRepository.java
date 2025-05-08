package ru.webrise.user_subscription_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.webrise.user_subscription_service.entity.UserSubscription;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    boolean existsByUserIdAndSubscriptionId(Long userId, Long subscriptionId);

    void deleteByUserIdAndSubscriptionId(Long userId, Long subscriptionId);

    List<UserSubscription> findAllByUserId(Long userId);
}
