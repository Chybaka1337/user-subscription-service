package ru.webrise.user_subscription_service.converter;

import org.mapstruct.Mapper;
import ru.webrise.user_subscription_service.dto.request.AddSubscriptionRequest;
import ru.webrise.user_subscription_service.dto.respone.SubscriptionResponse;
import ru.webrise.user_subscription_service.entity.Subscription;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    Subscription toEntity(AddSubscriptionRequest request);

    SubscriptionResponse toResponse(Subscription subscription);
}
