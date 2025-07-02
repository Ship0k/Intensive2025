package aston.task.mapper;

import aston.task.dto.UserDto;
import aston.task.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge()
        );
    }

    public static User fromDto(UserDto dto) {
        User user = new User();
        user.setId(dto.id());
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        return user;
    }
}