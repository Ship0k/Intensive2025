package aston.task.dao;

import aston.task.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    void setUp() {
        Configuration cfg = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(User.class);

        sessionFactory = cfg.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    void tearDown() {
        sessionFactory.close();
    }

    @Test
    void saveAndFindUser_shouldWorkCorrectly() {
        User user = new User("Alice", "alice@mail.com", 25);
        userDao.save(user);
        assertNotNull(user.getId());
        User found = userDao.findById(user.getId());
        assertEquals("Alice", found.getName());
        assertEquals("alice@mail.com", found.getEmail());
    }

    @Test
    void updateUser_shouldChangeData() {
        User user = new User("Bob", "bob@mail.com", 30);
        userDao.save(user);
        user.setName("Bobby");
        user.setAge(31);
        userDao.update(user);
        User updated = userDao.findById(user.getId());
        assertEquals("Bobby", updated.getName());
        assertEquals(31, updated.getAge());
    }

    @Test
    void deleteUser_shouldRemoveRecord() {
        User user = new User("Charlie", "charlie@mail.com", 22);
        userDao.save(user);
        boolean deleted = userDao.delete(user.getId());
        assertTrue(deleted);
        User result = userDao.findById(user.getId());
        assertNull(result);
    }

    @Test
    void deleteUser_whenNotFound_shouldReturnFalse() {
        boolean result = userDao.delete(9999L);
        assertFalse(result);
    }
}