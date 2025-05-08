package ru.webrise.user_subscription_service.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.webrise.user_subscription_service.dto.request.AddSubscriptionRequest;
import ru.webrise.user_subscription_service.dto.respone.UserSubscriptionResponse;
import ru.webrise.user_subscription_service.service.UserSubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addSubscription(@PathVariable Long userId, @Valid @RequestBody AddSubscriptionRequest request) {
        userSubscriptionService.addSubscription(userId, request);
    }

    @GetMapping
    public List<UserSubscriptionResponse> getSubscriptions(@PathVariable Long userId) {
        return userSubscriptionService.getUserSubscriptions(userId);
    }

    @DeleteMapping("/{subId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSubscription(@PathVariable Long userId, @PathVariable Long subId) {
        userSubscriptionService.removeSubscription(userId, subId);
    }
}
