package by.slava_borisov.library.dao.impl;

import by.slava_borisov.library.dao.AbstractDao;
import by.slava_borisov.library.dao.BookCopyDao;
import by.slava_borisov.library.model.BookCopy;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class BookCopyDaoImpl extends AbstractDao<BookCopy, Long> implements BookCopyDao {

    public BookCopyDaoImpl() {
        super(BookCopy.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookCopy> findByInventoryNumber(String inventoryNumber) {
        try {
            BookCopy bookCopy = entityManager.createQuery(
                            "select bc from BookCopy bc where bc.inventoryNumber = :inventoryNumber",
                            BookCopy.class)
                    .setParameter("inventoryNumber", inventoryNumber)
                    .getSingleResult();
            return Optional.of(bookCopy);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByInventoryNumber(String inventoryNumber) {
        Long count = entityManager.createQuery(
                        "select count(bc) from BookCopy bc where bc.inventoryNumber = :inventoryNumber",
                        Long.class)
                .setParameter("inventoryNumber", inventoryNumber)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopy> findByBookId(Long bookId) {
        return entityManager.createQuery(
                        "select bc from BookCopy bc where bc.book.id = :bookId",
                        BookCopy.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopy> findByStatus(String status) {
        return entityManager.createQuery(
                        "select bc from BookCopy bc where bc.status = :status",
                        BookCopy.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopy> findAvailableByBookId(Long bookId) {
        return entityManager.createQuery(
                        "select bc from BookCopy bc where bc.book.id = :bookId and bc.status = :status",
                        BookCopy.class)
                .setParameter("bookId", bookId)
                .setParameter("status", "AVAILABLE")
                .getResultList();
    }
}