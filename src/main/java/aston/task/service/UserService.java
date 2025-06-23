package aston.task.service;

import aston.task.entity.User;

public interface UserService {
    void createUser(User user);
    User getUserById(Long id);
    void updateUser(User user);
    void deleteUserById(Long id);
}