package aston.task.app;

import java.util.Scanner;

import aston.task.dao.UserDao;
import aston.task.dao.UserDaoImpl;
import aston.task.entity.User;
import aston.task.exception.UserNotFoundException;
import aston.task.exception.UserServiceException;
import aston.task.service.UserService;
import aston.task.service.UserServiceImpl;
import aston.task.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class UserConsoleApp {

    private static final Logger logger = LogManager.getLogger(UserConsoleApp.class);

    public static void main(String[] args) {
        logger.info("Запуск приложения");
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        UserDao userDao = new UserDaoImpl(factory);
        UserService userService = new UserServiceImpl(userDao);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nВыберите операцию: (1) Создать (2) Читать (3) Обновить (4) Удалить (5) Выход");
            int choice = Validator.getValidatedInt(scanner);
            logger.debug("Выбран пункт меню: {}", choice);

            if (choice == 5) {
                logger.info("Завершение приложения по запросу пользователя");
                System.out.println("Выход из программы. До свидания!");
                break;
            }

            switch (choice) {
                case 1 -> {
                    System.out.print("Введите имя: ");
                    String name = scanner.nextLine().trim();

                    System.out.print("Введите email: ");
                    String email = Validator.getValidatedEmail(scanner);

                    System.out.print("Введите возраст: ");
                    int age = Validator.getValidatedInt(scanner);

                    User user = new User(name, email, age);
                    logger.debug("Попытка создать пользователя: {}", user);

                    try {
                        userService.createUser(user);
                        System.out.println("Пользователь создан!");
                        logger.info("Пользователь успешно создан: {}", user);
                    } catch (UserServiceException e) {
                        System.out.println("Не удалось создать пользователя: " + e.getMessage());
                        logger.error("Ошибка при создании пользователя", e);
                    }
                }

                case 2 -> {
                    System.out.print("Введите ID пользователя: ");
                    long id = Validator.getValidatedLong(scanner);
                    logger.debug("Попытка найти пользователя по ID: {}", id);

                    try {
                        User foundUser = userService.getUserById(id);
                        System.out.println("Найден пользователь: " + foundUser);
                        logger.info("Пользователь найден: {}", foundUser);
                    } catch (UserNotFoundException e) {
                        System.out.println("Пользователь не найден: " + e.getMessage());
                        logger.warn("Пользователь не найден по ID {}", id);
                    } catch (UserServiceException e) {
                        System.out.println("Ошибка при получении пользователя: " + e.getMessage());
                        logger.error("Ошибка при получении пользователя", e);
                    }
                }

                case 3 -> {
                    System.out.print("Введите ID пользователя для обновления: ");
                    long updateId = Validator.getValidatedLong(scanner);
                    logger.debug("Попытка обновления пользователя с ID: {}", updateId);

                    try {
                        User updateUser = userService.getUserById(updateId);

                        System.out.print("Введите новое имя: ");
                        updateUser.setName(scanner.nextLine().trim());

                        System.out.print("Введите новый email: ");
                        updateUser.setEmail(Validator.getValidatedEmail(scanner));

                        System.out.print("Введите новый возраст: ");
                        updateUser.setAge(Validator.getValidatedInt(scanner));

                        userService.updateUser(updateUser);
                        System.out.println("Пользователь обновлён!");
                        logger.info("Пользователь успешно обновлён: {}", updateUser);
                    } catch (UserNotFoundException | IllegalArgumentException e) {
                        System.out.println("Пользователь не найден: " + e.getMessage());
                        logger.warn("Ошибка при обновлении: {}", e.getMessage());
                    } catch (UserServiceException e) {
                        System.out.println("Ошибка при обновлении: " + e.getMessage());
                        logger.error("Ошибка при обновлении пользователя", e);
                    }
                }

                case 4 -> {
                    System.out.print("Введите ID пользователя для удаления: ");
                    long deleteId = Validator.getValidatedLong(scanner);
                    logger.debug("Попытка удалить пользователя с ID: {}", deleteId);

                    try {
                        userService.deleteUserById(deleteId);
                        System.out.println("Пользователь удалён!");
                        logger.info("Пользователь с ID {} удалён", deleteId);
                    } catch (UserNotFoundException e) {
                        System.out.println("Пользователь не найден: " + e.getMessage());
                        logger.warn("Удаление не удалось: пользователь с ID {} не найден", deleteId);
                    } catch (UserServiceException e) {
                        System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
                        logger.error("Ошибка при удалении пользователя", e);
                    }
                }

                default -> {
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
                    logger.warn("Некорректный выбор меню: {}", choice);
                }
            }
        }
    }
}