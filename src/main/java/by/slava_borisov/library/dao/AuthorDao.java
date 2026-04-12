package by.slava_borisov.library.dao;

import by.slava_borisov.library.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {

    Optional<Author> findById(Long id);

    List<Author> findAll();

    Author save(Author author);

    Author update(Author author);

    void delete(Author author);

    void deleteById(Long id);

    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);

    List<Author> findByLastName(String lastName);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}