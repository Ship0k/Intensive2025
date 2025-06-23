package aston.task.dao;

import aston.task.entity.User;

public interface UserDao {
    void save(User user);
    User findById(Long id);
    void update(User user);
    boolean delete(Long id);
}