package ru.webrise.user_subscription_service.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.webrise.user_subscription_service.entity.Subscription;
import ru.webrise.user_subscription_service.repository.SubscriptionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) throws Exception {
        preloadSubscription();
    }

    private void preloadSubscription() {
        List<String> defaultNames = List.of(
                "Youtube Premium",
                "Netflix",
                "Yandex plus",
                "Vk Музыка"
        );

        defaultNames.forEach(name -> {
            if (subscriptionRepository.findByNameIgnoreCase(name).isEmpty()) {
                Subscription sub = Subscription.builder()
                        .name(name)
                        .build();
                subscriptionRepository.save(sub);
                log.info("DataInitializer: created subscription {}", name);
            } else {
                log.debug("DataInitializer: subscription {} already exist", name);
            }
        });
    }
}
