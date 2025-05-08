package ru.webrise.user_subscription_service.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.webrise.user_subscription_service.dto.request.UserRequest;
import ru.webrise.user_subscription_service.dto.respone.UserResponse;
import ru.webrise.user_subscription_service.exception.NotFoundException;
import ru.webrise.user_subscription_service.service.UserService;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("POST /users")
    class CreateUser {

        @Test
        @DisplayName("should return 201 Created with Location and body")
        void createUser_success() throws Exception {
            UserRequest req = new UserRequest("bob", "bob@example.com");
            UserResponse res = new UserResponse(5L, "bob", "bob@example.com");

            when(userService.create(req)).thenReturn(res);

            mvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/users/5"))
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.username").value("bob"))
                    .andExpect(jsonPath("$.email").value("bob@example.com"));
        }

        @Test
        @DisplayName("invalid payload should return 400 Bad Request")
        void createUser_validationError() throws Exception {
            // Missing email
            UserRequest req = new UserRequest("alice", "");

            mvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUser {

        @Test
        @DisplayName("existing user should return 200 OK with body")
        void getUser_found() throws Exception {
            UserResponse res = new UserResponse(1L, "alice", "alice@example.com");
            when(userService.getById(1L)).thenReturn(res);

            mvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("alice"))
                    .andExpect(jsonPath("$.email").value("alice@example.com"));
        }

        @Test
        @DisplayName("non-existing user should return 404 Not Found")
        void getUser_notFound() throws Exception {
            when(userService.getById(99L))
                    .thenThrow(new NotFoundException("User not found with id 99"));

            mvc.perform(get("/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("User not found with id 99"));
        }
    }

    @Nested
    @DisplayName("PUT /users/{id}")
    class UpdateUser {

        @Test
        @DisplayName("existing user should return 200 OK with updated body")
        void updateUser_success() throws Exception {
            UserRequest req = new UserRequest("updated", "up@example.com");
            UserResponse res = new UserResponse(1L, "updated", "up@example.com");

            when(userService.update(1L, req)).thenReturn(res);

            mvc.perform(put("/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("updated"))
                    .andExpect(jsonPath("$.email").value("up@example.com"));
        }

        @Test
        @DisplayName("non-existing user should return 404 Not Found")
        void updateUser_notFound() throws Exception {
            UserRequest req = new UserRequest("no", "no@example.com");
            when(userService.update(99L, req))
                    .thenThrow(new NotFoundException("User not found with id 99"));

            mvc.perform(put("/users/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("User not found with id 99"));
        }
    }

    @Nested
    @DisplayName("DELETE /users/{id}")
    class DeleteUser {

        @Test
        @DisplayName("existing user should return 204 No Content")
        void deleteUser_success() throws Exception {
            doNothing().when(userService).deleteById(1L);

            mvc.perform(delete("/users/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("non-existing user should return 404 Not Found")
        void deleteUser_notFound() throws Exception {
            doThrow(new NotFoundException("User not found with id 99"))
                    .when(userService).deleteById(99L);

            mvc.perform(delete("/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("User not found with id 99"));
        }
    }
}
