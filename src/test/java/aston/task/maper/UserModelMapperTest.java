package aston.task.mapper;

import aston.task.controller.UserController;
import aston.task.dto.UserDto;
import aston.task.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

class UserModelMapperTest {

    @Test
    void shouldMapUserDtoToUserModelWithLinks() {
        UserDto dto = new UserDto(42L, "Толик", "tolik@mail.com", 34);

        UserModel model = UserModelMapper.toModel(dto);

        assertThat(model.getId()).isEqualTo(dto.id());
        assertThat(model.getName()).isEqualTo(dto.name());
        assertThat(model.getEmail()).isEqualTo(dto.email());
        assertThat(model.getAge()).isEqualTo(dto.age());

        Link selfLink = linkTo(methodOn(UserController.class).getById(dto.id())).withSelfRel();
        Link allUsersLink = linkTo(methodOn(UserController.class).getAll()).withRel("all-users");

        assertThat(model.getLinks()).contains(selfLink, allUsersLink);
    }
}