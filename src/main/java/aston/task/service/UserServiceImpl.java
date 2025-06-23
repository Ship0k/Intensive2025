package aston.task.service;

import aston.task.dao.UserDao;
import aston.task.entity.User;
import aston.task.exception.DataAccessException;
import aston.task.exception.UserNotFoundException;
import aston.task.exception.UserServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void createUser(User user) {
        logger.info("Создание пользователя: {}", user);
        try {
            userDao.save(user);
        } catch (DataAccessException e) {
            logger.error("Ошибка при создании пользователя: {}", user, e);
            throw new UserServiceException("Не удалось создать пользователя", e);
        }
    }

    @Override
    public User getUserById(Long id) {
        logger.debug("Получение пользователя по ID: {}", id);
        try {
            User user = userDao.findById(id);
            if (user == null) {
                logger.warn("Пользователь с ID {} не найден", id);
                throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
            }
            logger.info("Пользователь найден: {}", user);
            return user;
        } catch (DataAccessException e) {
            logger.error("Ошибка при поиске пользователя с ID {}: {}", id, e.getMessage());
            throw new UserServiceException("Ошибка доступа при получении пользователя", e);
        }
    }

    @Override
    public void updateUser(User user) {
        Long userId = user.getId();
        logger.debug("Обновление пользователя ID: {}", userId);
        if (userId == null) {
            logger.warn("Попытка обновления пользователя без ID");
            throw new IllegalArgumentException("ID пользователя не указан");
        }
        try {
            if (userDao.findById(userId) == null) {
                logger.warn("Обновление невозможно: пользователь с ID {} не найден", userId);
                throw new UserNotFoundException("Невозможно обновить: пользователь не найден");
            }
            userDao.update(user);
            logger.info("Пользователь обновлен: {}", user);
        } catch (DataAccessException e) {
            logger.error("Ошибка при обновлении пользователя: {}", user, e);
            throw new UserServiceException("Не удалось обновить пользователя", e);
        }
    }

    @Override
    public void deleteUserById(Long id) {
        logger.debug("Попытка удалить пользователя с ID {}", id);
        try {
            boolean deleted = userDao.delete(id);
            if (!deleted) {
                logger.warn("Удаление не удалось: пользователь с ID {} не найден", id);
                throw new UserNotFoundException("Удаление не удалось: пользователь не найден");
            }
            logger.info("Пользователь с ID {} успешно удалён", id);
        } catch (DataAccessException e) {
            logger.error("Ошибка при удалении пользователя с ID {}: {}", id, e.getMessage());
            throw new UserServiceException("Ошибка при удалении пользователя", e);
        }
    }
}