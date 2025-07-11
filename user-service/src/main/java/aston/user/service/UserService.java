package aston.user.service;

import aston.user.dto.UserDto;

import java.util.List;

public interface UserService {
    void createUser(UserDto dto);
    UserDto getUserDtoById(Long id);
    List<UserDto> getAllUsers();
    void updateUser(Long id, UserDto dto);
    void deleteUserById(Long id);
}