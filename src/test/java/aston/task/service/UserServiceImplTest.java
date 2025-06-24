package aston.task.service;

import aston.task.dao.UserDao;
import aston.task.entity.User;
import aston.task.exception.DataAccessException;
import aston.task.exception.UserNotFoundException;
import aston.task.exception.UserServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldCallDaoSave() {
        User user = new User("Test", "test@mail.com", 30);
        assertDoesNotThrow(() -> userService.createUser(user));
        verify(userDao).save(user);
    }

    @Test
    void createUser_whenDaoFails_shouldThrowServiceException() {
        User user = new User("Test", "test@mail.com", 30);
        doThrow(new DataAccessException("DB error", new RuntimeException())).when(userDao).save(user);
        assertThrows(UserServiceException.class, () -> userService.createUser(user));
    }

    @Test
    void getUserById_shouldReturnCorrectUser() {
        User user = new User("Alice", "a@mail.com", 22);
        when(userDao.findById(1L)).thenReturn(user);
        User result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("a@mail.com", result.getEmail());
        verify(userDao).findById(1L);
    }

    @Test
    void getUserById_whenNotFound_shouldThrowUserNotFoundException() {
        when(userDao.findById(99L)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getUserById_whenDaoFails_shouldThrowServiceException() {
        when(userDao.findById(1L)).thenThrow(new DataAccessException("fail", null));
        assertThrows(UserServiceException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_shouldUpdateIfExists() {
        User user = new User("Bob", "b@mail.com", 33);
        user.setId(1L);
        when(userDao.findById(1L)).thenReturn(user);
        assertDoesNotThrow(() -> userService.updateUser(user));
        verify(userDao).update(user);
    }

    @Test
    void updateUser_whenIdIsNull_shouldThrowIllegalArgumentException() {
        User user = new User("NullID", "null@mail.com", 22);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(user));
    }

    @Test
    void updateUser_whenUserMissing_shouldThrowUserNotFoundException() {
        User user = new User("Ghost", "ghost@mail.com", 40);
        user.setId(42L);
        when(userDao.findById(42L)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void updateUser_whenDaoUpdateFails_shouldThrowUserServiceException() {
        User user = new User("UpdateFail", "fail@mail.com", 50);
        user.setId(3L);
        when(userDao.findById(3L)).thenReturn(user);
        doThrow(new DataAccessException("Update fail", null)).when(userDao).update(user);
        assertThrows(UserServiceException.class, () -> userService.updateUser(user));
    }

    @Test
    void deleteUser_whenSuccessful_shouldNotThrow() {
        when(userDao.delete(1L)).thenReturn(true);
        assertDoesNotThrow(() -> userService.deleteUserById(1L));
    }

    @Test
    void deleteUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userDao.delete(9L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(9L));
    }

    @Test
    void deleteUser_whenDaoFails_shouldThrowServiceException() {
        when(userDao.delete(5L)).thenThrow(new DataAccessException("DB fail", null));
        assertThrows(UserServiceException.class, () -> userService.deleteUserById(5L));
    }
}