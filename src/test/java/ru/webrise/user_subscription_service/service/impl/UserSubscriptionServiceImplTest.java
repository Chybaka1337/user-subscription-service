package ru.webrise.user_subscription_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class UserSubscriptionServiceImplTest {

    @Mock private UserService userService;
    @Mock private SubscriptionService subscriptionService;
    @Mock private UserSubscriptionRepository userSubscriptionRepository;
    @Mock private UserSubscriptionMapper userSubscriptionMapper;

    @InjectMocks private UserSubscriptionServiceImpl service;

    private final Long userId = 1L;
    private final Long subscriptionId = 10L;
    private User user;
    private Subscription subscription;
    private AddSubscriptionRequest addReq;
    private UserSubscription userSub;
    private UserSubscriptionResponse subResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .username("alice")
                .email("alice@example.com")
                .build();

        subscription = Subscription.builder()
                .id(subscriptionId)
                .name("Netflix")
                .build();

        addReq = new AddSubscriptionRequest("Netflix");

        userSub = UserSubscription.builder()
                .id(100L)
                .user(user)
                .subscription(subscription)
                .createdAt(LocalDateTime.of(2025,1,1,12,0))
                .build();

        subResponse = new UserSubscriptionResponse(
                subscriptionId,
                "Netflix",
                userSub.getCreatedAt()
        );
    }

    @Test
    void addSubscription_success() {
        when(userService.getEntityById(userId)).thenReturn(user);
        when(subscriptionService.getByName(addReq.name())).thenReturn(subscription);
        when(userSubscriptionRepository.existsByUserIdAndSubscriptionId(userId, subscriptionId))
                .thenReturn(false);

        // stub save → return userSub
        when(userSubscriptionRepository.save(any(UserSubscription.class)))
                .thenReturn(userSub);

        service.addSubscription(userId, addReq);

        verify(userSubscriptionRepository).save(argThat(us ->
                us.getUser() == user &&
                        us.getSubscription() == subscription
        ));
    }

    @Test
    void addSubscription_alreadyExists() {
        when(userService.getEntityById(userId)).thenReturn(user);
        when(subscriptionService.getByName(addReq.name())).thenReturn(subscription);
        when(userSubscriptionRepository.existsByUserIdAndSubscriptionId(userId, subscriptionId))
                .thenReturn(true);

        assertThatThrownBy(() -> service.addSubscription(userId, addReq))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessageContaining("Subscription already exist for user id" + userId);

        verify(userSubscriptionRepository, never()).save(any());
    }

    @Test
    void removeSubscription_success() {
        when(userService.getEntityById(userId)).thenReturn(user);
        when(userSubscriptionRepository.existsByUserIdAndSubscriptionId(userId, subscriptionId))
                .thenReturn(true);

        service.removeSubscription(userId, subscriptionId);

        verify(userSubscriptionRepository)
                .deleteByUserIdAndSubscriptionId(userId, subscriptionId);
    }

    @Test
    void removeSubscription_notFound() {
        when(userService.getEntityById(userId)).thenReturn(user);
        when(userSubscriptionRepository.existsByUserIdAndSubscriptionId(userId, subscriptionId))
                .thenReturn(false);

        assertThatThrownBy(() -> service.removeSubscription(userId, subscriptionId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("UserSubscription not found for user id" + userId);

        verify(userSubscriptionRepository, never()).deleteByUserIdAndSubscriptionId(anyLong(), anyLong());
    }

    @Test
    void getUserSubscriptions_success() {
        when(userService.getEntityById(userId)).thenReturn(user);
        when(userSubscriptionRepository.findAllByUserId(userId))
                .thenReturn(List.of(userSub));
        when(userSubscriptionMapper.toResponse(userSub))
                .thenReturn(subResponse);

        List<UserSubscriptionResponse> result = service.getUserSubscriptions(userId);

        assertThat(result).containsExactly(subResponse);
    }

    @Test
    void getUserSubscriptions_userNotFound() {
        when(userService.getEntityById(userId))
                .thenThrow(new NotFoundException("User not found"));

        assertThatThrownBy(() -> service.getUserSubscriptions(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userSubscriptionRepository, never()).findAllByUserId(anyLong());
    }
}
