package ru.webrise.user_subscription_service.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import ru.webrise.user_subscription_service.service.SubscriptionService;

@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @Nested
    @DisplayName("GET /subscriptions/top")
    class GetTopSubscriptions {

        @Test
        @DisplayName("should return 200 OK and list of top subscriptions")
        void getTopSubscriptions_success() throws Exception {
            List<String> top = List.of("YouTube Premium", "Netflix", "VK Музыка");
            when(subscriptionService.getTop3PopularSubscriptions()).thenReturn(top);

            mvc.perform(get("/subscriptions/top"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(top.size()))
                    .andExpect(jsonPath("$[0]").value("YouTube Premium"))
                    .andExpect(jsonPath("$[1]").value("Netflix"))
                    .andExpect(jsonPath("$[2]").value("VK Музыка"));
        }

        @Test
        @DisplayName("should return empty array when no subscriptions")
        void getTopSubscriptions_empty() throws Exception {
            when(subscriptionService.getTop3PopularSubscriptions()).thenReturn(List.of());

            mvc.perform(get("/subscriptions/top"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("[]"));
        }
    }
}