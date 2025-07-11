package aston.user.mapper;

import aston.user.dto.UserDto;
import aston.user.entity.User;

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