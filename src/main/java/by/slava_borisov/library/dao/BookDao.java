package by.slava_borisov.library.dao;

import by.slava_borisov.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    Optional<Book> findById(Long id);

    List<Book> findAll();

    Book save(Book book);

    Book update(Book book);

    void delete(Book book);

    void deleteById(Long id);

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    List<Book> findByTitleContaining(String title);

    List<Book> findByCategoryId(Long categoryId);

    List<Book> findByAuthorId(Long authorId);
}