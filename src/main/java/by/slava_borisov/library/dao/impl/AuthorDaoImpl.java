package by.slava_borisov.library.dao.impl;

import by.slava_borisov.library.dao.AbstractDao;
import by.slava_borisov.library.dao.AuthorDao;
import by.slava_borisov.library.model.Author;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDaoImpl extends AbstractDao<Author, Long> implements AuthorDao {

    public AuthorDaoImpl() {
        super(Author.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> findByFirstNameAndLastName(String firstName, String lastName) {
        try {
            Author author = entityManager.createQuery(
                            "select a from Author a where a.firstName = :firstName and a.lastName = :lastName",
                            Author.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .getSingleResult();
            return Optional.of(author);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findByLastName(String lastName) {
        return entityManager.createQuery(
                        "select a from Author a where a.lastName = :lastName", Author.class)
                .setParameter("lastName", lastName)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByFirstNameAndLastName(String firstName, String lastName) {
        Long count = entityManager.createQuery(
                        "select count(a) from Author a where a.firstName = :firstName and a.lastName = :lastName",
                        Long.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getSingleResult();
        return count > 0;
    }
}