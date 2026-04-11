package by.slava_borisov.library.dao;

import by.slava_borisov.library.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(Long id);

    List<User> findAll();

    User save(User user);

    User update(User user);

    void delete(User user);

    void deleteById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findAllActiveUsers();
}