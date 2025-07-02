package aston.task.service;

import aston.task.dto.UserDto;
import aston.task.entity.User;
import aston.task.exception.UserNotFoundException;
import aston.task.mapper.UserMapper;
import aston.task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldReturnUserDtoById() {
        User user = new User("Анатолий", "tolik@mail.com", 33);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto result = userService.getUserDtoById(1L);
        assertThat(result).isEqualTo(UserMapper.toDto(user));
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserDtoById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void shouldCreateUser() {
        UserDto dto = new UserDto(null, "Наталья", "nata@mail.com", 28);
        User user = UserMapper.fromDto(dto);
        userService.createUser(dto);
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateExistingUser() {
        Long id = 1L;
        User existing = new User("Тест", "test@mail.com", 20);
        existing.setId(id);
        UserDto dto = new UserDto(id, "Обновлён", "new@mail.com", 21);
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        userService.updateUser(id, dto);
        assertThat(existing.getName()).isEqualTo("Обновлён");
        assertThat(existing.getEmail()).isEqualTo("new@mail.com");
        assertThat(existing.getAge()).isEqualTo(21);
        verify(userRepository).save(existing);
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUserById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentUser() {
        when(userRepository.existsById(41L)).thenReturn(false);
        assertThatThrownBy(() -> userService.deleteUserById(41L)).isInstanceOf(UserNotFoundException.class);
    }
}