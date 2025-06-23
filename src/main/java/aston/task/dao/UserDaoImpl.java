package aston.task.dao;

import aston.task.exception.DataAccessException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import aston.task.entity.User;

public class UserDaoImpl implements UserDao {
    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("Пользователь сохранен: {}", user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при сохранении пользователя", e);
            throw new DataAccessException("Не удалось сохранить пользователя", e);
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                logger.info("Найден пользователь: {}", user);
            } else {
                logger.warn("Пользователь с ID {} не найден", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя", e);
            throw new DataAccessException("Ошибка при получении пользователя с ID " + id, e);
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("Пользователь обновлен: {}", user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при обновлении пользователя", e);
            throw new DataAccessException("Не удалось обновить пользователя", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("Пользователь удален: {}", user);
                return true;
            } else {
                logger.warn("Попытка удаления несуществующего пользователя с ID {}", id);
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка при удалении пользователя", e);
            throw new DataAccessException("Ошибка при удалении пользователя с ID " + id, e);
        }
    }
}