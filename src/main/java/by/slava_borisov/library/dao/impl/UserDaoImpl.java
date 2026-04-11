package by.slava_borisov.library.dao.impl;

import by.slava_borisov.library.dao.AbstractDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl extends AbstractDao<User, Long> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        try {
            User user = entityManager.createQuery(
                            "select u from User u where u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        try {
            User user = entityManager.createQuery(
                            "select u from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery(
                        "select count(u) from User u where u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "select count(u) from User u where u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllActiveUsers() {
        return entityManager.createQuery(
                        "select u from User u where u.isActive = true", User.class)
                .getResultList();
    }
}