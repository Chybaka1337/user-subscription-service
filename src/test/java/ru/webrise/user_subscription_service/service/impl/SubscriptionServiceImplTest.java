package ru.webrise.user_subscription_service.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.webrise.user_subscription_service.entity.Subscription;
import ru.webrise.user_subscription_service.exception.NotFoundException;
import ru.webrise.user_subscription_service.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private Subscription subEntity;

    @BeforeEach
    void setUp() {
        subEntity = Subscription.builder()
                .id(10L)
                .name("Netflix")
                .build();
    }

    @Test
    void getByName_found() {
        when(subscriptionRepository.findByName("Netflix"))
                .thenReturn(Optional.of(subEntity));

        Subscription result = subscriptionService.getByName("Netflix");

        assertThat(result).isSameAs(subEntity);
    }

    @Test
    void getByName_notFound() {
        when(subscriptionRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.getByName("HBO"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Subscribe not found with this name HBO");
    }

    @Test
    void getTop3PopularSubscriptions_nonEmpty() {
        List<String> top = List.of("A", "B", "C");
        when(subscriptionRepository.findTop3PopularSubscriptions())
                .thenReturn(top);

        List<String> result = subscriptionService.getTop3PopularSubscriptions();

        assertThat(result).containsExactlyElementsOf(top);
    }

    @Test
    void getTop3PopularSubscriptions_empty() {
        when(subscriptionRepository.findTop3PopularSubscriptions())
                .thenReturn(List.of());

        List<String> result = subscriptionService.getTop3PopularSubscriptions();

        assertThat(result).isEmpty();
    }
}
