package aston.task.mapper;

import aston.task.dto.UserDto;
import aston.task.model.UserModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class UserModelMapper {

    public static UserModel toModel(UserDto dto) {
        UserModel model = new UserModel(dto.id(), dto.name(), dto.email(), dto.age());

        model.add(linkTo(methodOn(aston.task.controller.UserController.class).getById(dto.id())).withSelfRel());
        model.add(linkTo(methodOn(aston.task.controller.UserController.class).getAll()).withRel("all-users"));

        return model;
    }
}