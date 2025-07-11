package aston.user.service;

import aston.user.dto.UserDto;
import aston.user.entity.User;
import aston.user.exception.UserNotFoundException;
import aston.user.exception.UserServiceException;
import aston.user.mapper.UserMapper;
import aston.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;
    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        kafkaProducer = mock(KafkaProducer.class);
        userService = new UserServiceImpl(userRepository, kafkaProducer);
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
    void shouldThrowWhenKafkaProducerFailsDuringCreateUser() {
        UserDto dto = new UserDto(null, "Тестовый", "test@mail.com", 28);
        User user = UserMapper.fromDto(dto);
        doThrow(new UserServiceException("Kafka упал", new RuntimeException("boom")))
                .when(kafkaProducer)
                .sendUserEvent("CREATE", user.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(user);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("Kafka упал");
        verify(kafkaProducer).sendUserEvent("CREATE", user.getEmail());
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
    void shouldThrowWhenUpdatingNonexistentUser() {
        Long id = 999L;
        UserDto dto = new UserDto(id, "Имя", "email@mail.com", 30);
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(id, dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void shouldDeleteUserAndSendKafkaMessage() {
        Long id = 1L;
        User user = new User("Tolik", "tolik@mail.com", 33);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.deleteUserById(id);
        verify(userRepository).deleteById(id);
        verify(userRepository).findById(id);
        verify(kafkaProducer).sendUserEvent("DELETE", user.getEmail());
    }

    @Test
    void shouldThrowWhenDeletingNonexistentUser() {
        Long id = 41L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteUserById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("не существует");
        verify(userRepository, never()).deleteById(anyLong()); // убедимся, что удаление не происходило
        verify(kafkaProducer, never()).sendUserEvent(anyString(), anyString()); // и Kafka ничего не слал
    }

    @Test
    void shouldThrowWhenKafkaFailsDuringDeleteUser() {
        Long id = 42L;
        User user = new User("Тест", "delete@mail.com", 30);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doThrow(new UserServiceException("Kafka упал", new RuntimeException()))
                .when(kafkaProducer)
                .sendUserEvent("DELETE", user.getEmail());
        assertThatThrownBy(() -> userService.deleteUserById(id))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("Kafka упал");
        verify(userRepository).deleteById(id);
        verify(kafkaProducer).sendUserEvent("DELETE", user.getEmail());
    }
}