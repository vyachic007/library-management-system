package by.slava_borisov.library.dao.impl;

import by.slava_borisov.library.dao.AbstractDao;
import by.slava_borisov.library.dao.RoleDao;
import by.slava_borisov.library.model.Role;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class RoleDaoImpl extends AbstractDao<Role, Long> implements RoleDao {

    public RoleDaoImpl() {
        super(Role.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        try {
            Role role = entityManager.createQuery(
                            "select r from Role r where r.name = :name",
                            Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(role);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        Long count = entityManager.createQuery(
                        "select count(r) from Role r where r.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }
}