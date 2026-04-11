package by.slava_borisov.library.dao;

import by.slava_borisov.library.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDao {

    Optional<Role> findById(Long id);

    List<Role> findAll();

    Role save(Role role);

    Role update(Role role);

    void delete(Role role);

    void deleteById(Long id);

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}