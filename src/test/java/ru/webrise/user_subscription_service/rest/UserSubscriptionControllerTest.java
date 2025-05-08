package ru.webrise.user_subscription_service.rest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.webrise.user_subscription_service.dto.request.AddSubscriptionRequest;
import ru.webrise.user_subscription_service.dto.respone.UserSubscriptionResponse;
import ru.webrise.user_subscription_service.exception.NotFoundException;
import ru.webrise.user_subscription_service.service.UserSubscriptionService;

@WebMvcTest(controllers = UserSubscriptionController.class)
class UserSubscriptionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private UserSubscriptionService userSubscriptionService;

    @Nested
    @DisplayName("POST /users/{userId}/subscriptions")
    class AddSubscription {

        @Test
        @DisplayName("should return 201 Created on success")
        void add_success() throws Exception {
            doNothing().when(userSubscriptionService).addSubscription(eq(1L), any(AddSubscriptionRequest.class));

            AddSubscriptionRequest req = new AddSubscriptionRequest("Netflix");
            mvc.perform(post("/users/1/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("should return 400 Bad Request on invalid payload")
        void add_invalidPayload() throws Exception {
            AddSubscriptionRequest req = new AddSubscriptionRequest("");
            mvc.perform(post("/users/1/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 404 Not Found if user does not exist")
        void add_userNotFound() throws Exception {
            doThrow(new NotFoundException("User not found")).when(userSubscriptionService)
                    .addSubscription(eq(99L), any(AddSubscriptionRequest.class));

            AddSubscriptionRequest req = new AddSubscriptionRequest("Netflix");
            mvc.perform(post("/users/99/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }
    }

    @Nested
    @DisplayName("GET /users/{userId}/subscriptions")
    class GetSubscriptions {

        @Test
        @DisplayName("should return 200 OK and list of subscriptions")
        void get_success() throws Exception {
            List<UserSubscriptionResponse> list = List.of(
                    new UserSubscriptionResponse(1L, "Netflix", LocalDateTime.of(2025,1,1,12,0))
            );
            when(userSubscriptionService.getUserSubscriptions(1L)).thenReturn(list);

            mvc.perform(get("/users/1/subscriptions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].subscriptionId").value(1))
                    .andExpect(jsonPath("$[0].name").value("Netflix"))
                    .andExpect(jsonPath("$[0].createdAt").exists());
        }

        @Test
        @DisplayName("should return 404 Not Found if user does not exist")
        void get_userNotFound() throws Exception {
            when(userSubscriptionService.getUserSubscriptions(99L))
                    .thenThrow(new NotFoundException("User not found"));

            mvc.perform(get("/users/99/subscriptions"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }
    }

    @Nested
    @DisplayName("DELETE /users/{userId}/subscriptions/{subscriptionId}")
    class RemoveSubscription {

        @Test
        @DisplayName("should return 204 No Content on success")
        void delete_success() throws Exception {
            doNothing().when(userSubscriptionService).removeSubscription(1L, 2L);

            mvc.perform(delete("/users/1/subscriptions/2"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 Not Found if subscription not found for user")
        void delete_notFound() throws Exception {
            doThrow(new NotFoundException("UserSubscription not found"))
                    .when(userSubscriptionService).removeSubscription(1L, 2L);

            mvc.perform(delete("/users/1/subscriptions/2"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("UserSubscription not found"));
        }
    }
}
