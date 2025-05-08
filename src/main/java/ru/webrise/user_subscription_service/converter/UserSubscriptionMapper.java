package ru.webrise.user_subscription_service.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.webrise.user_subscription_service.dto.respone.UserSubscriptionResponse;
import ru.webrise.user_subscription_service.entity.UserSubscription;

@Mapper(componentModel = "spring")
public interface UserSubscriptionMapper {

    @Mapping(source = "subscription.id", target = "subscriptionId")
    @Mapping(source = "subscription.name", target = "name")
    UserSubscriptionResponse toResponse(UserSubscription userSubscription);
}
