package ru.webrise.user_subscription_service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.webrise.user_subscription_service.dto.request.UserRequest;
import ru.webrise.user_subscription_service.dto.respone.UserResponse;
import ru.webrise.user_subscription_service.entity.User;
import ru.webrise.user_subscription_service.exception.NotFoundException;
import ru.webrise.user_subscription_service.repository.UserRepository;
import ru.webrise.user_subscription_service.converter.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest request;
    private User entityToSave;
    private User savedEntity;
    private UserResponse expectedDto;

    @BeforeEach
    void setUp() {
        // входной запрос
        request = new UserRequest("alice", "alice@example.com");

        // то, что возвращает mapper.toEntity(...)
        entityToSave = User.builder()
                .username(request.username())
                .email(request.email())
                .build();

        // то, что возвращает save(...) с проставленным id
        savedEntity = User.builder()
                .id(1L)
                .username(request.username())
                .email(request.email())
                .build();

        // то, что возвращает mapper.toResponse(...)
        expectedDto = new UserResponse(1L, "alice", "alice@example.com");
    }

    @Test
    void create_success() {
        // 1) маппер: DTO → Entity
        when(userMapper.toEntity(request)).thenReturn(entityToSave);

        // 2) репозиторий: save(any User) → savedEntity
        when(userRepository.save(any(User.class))).thenReturn(savedEntity);

        // 3) маппер: Entity → DTO
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedDto);

        // вызов
        UserResponse result = userService.create(request);

        // проверки
        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedDto);

        UserResponse result = userService.getById(1L);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void getById_notFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(2L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found with id 2");
    }

    @Test
    void update_success() {
        UserRequest updateReq = new UserRequest("alice2", "alice2@example.com");
        UserResponse updatedDto = new UserResponse(1L, "alice2", "alice2@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
        doAnswer(inv -> {
            User u = inv.getArgument(0);
            UserRequest r = inv.getArgument(1);
            u.setUsername(r.username());
            u.setEmail(r.email());
            return null;
        }).when(userMapper).update(savedEntity, updateReq);

        when(userRepository.save(savedEntity)).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(updatedDto);

        UserResponse result = userService.update(1L, updateReq);

        assertThat(result).isEqualTo(updatedDto);
        verify(userMapper).update(savedEntity, updateReq);
        verify(userRepository).save(savedEntity);
    }

    @Test
    void update_notFound() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());
        UserRequest updateReq = new UserRequest("x", "x@example.com");

        assertThatThrownBy(() -> userService.update(5L, updateReq))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_notFound() {
        when(userRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(42L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEntityById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getEntityById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEntityById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedEntity));

        User result = userService.getEntityById(1L);

        assertThat(result).isSameAs(savedEntity);
    }
}
