package by.slava_borisov.library.dao;

import by.slava_borisov.library.model.BookCopy;

import java.util.List;
import java.util.Optional;

public interface BookCopyDao {

    Optional<BookCopy> findById(Long id);

    List<BookCopy> findAll();

    BookCopy save(BookCopy bookCopy);

    BookCopy update(BookCopy bookCopy);

    void delete(BookCopy bookCopy);

    void deleteById(Long id);

    Optional<BookCopy> findByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumber(String inventoryNumber);

    List<BookCopy> findByBookId(Long bookId);

    List<BookCopy> findByStatus(String status);

    List<BookCopy> findAvailableByBookId(Long bookId);
}