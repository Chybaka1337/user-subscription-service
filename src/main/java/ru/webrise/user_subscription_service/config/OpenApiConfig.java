package ru.webrise.user_subscription_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "user subscription service api",
                version = "1.0.0",
                description = "Управление пользователями и подписками",
                contact =  @Contact(
                        name = "Andrey",
                        email = "zommer.02@mail.ru",
                        url = "https://t.me/andreyzommer"
                )
        )
)
public class OpenApiConfig {
}
