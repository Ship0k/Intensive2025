package aston.task.controller;

import aston.task.dto.UserDto;
import aston.task.model.UserModel;
import aston.task.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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
    void shouldReturnUserByIdWithLinks() throws Exception {
        UserModel model = new UserModel(1L, "Анатолий", "tolik@mail.com", 33);
        EntityModel<UserModel> entityModel = EntityModel.of(model);
        entityModel.add(Link.of("/api/users/1").withSelfRel());
        entityModel.add(Link.of("/api/users").withRel("all-users"));

        when(userService.getUserDtoById(1L)).thenReturn(new aston.task.dto.UserDto(1L, "Анатолий", "tolik@mail.com", 33));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Анатолий"))
                .andExpect(jsonPath("$.email").value("tolik@mail.com"))
                .andExpect(jsonPath("$.age").value(33))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists());
    }

    @Test
    void shouldReturnAllUsersWithLinks() throws Exception {
        List<aston.task.dto.UserDto> users = List.of(
                new aston.task.dto.UserDto(1L, "Толя", "tolya@mail.com", 30),
                new aston.task.dto.UserDto(2L, "Наташа", "natasha@mail.com", 25)
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userModelList.length()").value(2))
                .andExpect(jsonPath("$._embedded.userModelList[0].name").value("Толя"))
                .andExpect(jsonPath("$._embedded.userModelList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.userModelList[1].name").value("Наташа"))
                .andExpect(jsonPath("$._embedded.userModelList[1]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
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