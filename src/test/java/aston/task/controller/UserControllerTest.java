package aston.task.controller;

import aston.task.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import aston.task.dto.UserDto;
import aston.task.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnUserById() throws Exception {
        UserDto dto = new UserDto(1L, "Анатолий", "tolik@mail.com", 33);
        when(userService.getUserDtoById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Анатолий"))
                .andExpect(jsonPath("$.email").value("tolik@mail.com"))
                .andExpect(jsonPath("$.age").value(33));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        Long invalidId = 999L;
        when(userService.getUserDtoById(invalidId))
                .thenThrow(new UserNotFoundException("Пользователь с ID " + invalidId + " не найден"));

        mockMvc.perform(get("/api/users/{id}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999 не найден"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserDto dto = new UserDto(null, "Наталья", "nata@mail.com", 25);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(dto);
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "Толя", "tolya@mail.com", 33),
                new UserDto(2L, "Наташа", "natasha@mail.com", 25)
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        Long id = 1L;
        UserDto dto = new UserDto(id, "NewТолик", "new@mail.com", 34);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isNoContent());

        verify(userService, times(1)).updateUser(id, dto);
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/users/{id}", id))
                        .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(id);
    }
}