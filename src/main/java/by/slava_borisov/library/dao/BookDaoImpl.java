package by.slava_borisov.library.dao.impl;

import by.slava_borisov.library.dao.AbstractDao;
import by.slava_borisov.library.dao.BookDao;
import by.slava_borisov.library.model.Book;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class BookDaoImpl extends AbstractDao<Book, Long> implements BookDao {

    public BookDaoImpl() {
        super(Book.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByIsbn(String isbn) {
        try {
            Book book = entityManager.createQuery(
                            "select b from Book b where b.isbn = :isbn", Book.class)
                    .setParameter("isbn", isbn)
                    .getSingleResult();
            return Optional.of(book);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        Long count = entityManager.createQuery(
                        "select count(b) from Book b where b.isbn = :isbn", Long.class)
                .setParameter("isbn", isbn)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByTitleContaining(String title) {
        return entityManager.createQuery(
                        "select b from Book b where lower(b.title) like lower(concat('%', :title, '%'))",
                        Book.class)
                .setParameter("title", title)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByCategoryId(Long categoryId) {
        return entityManager.createQuery(
                        "select b from Book b where b.category.id = :categoryId", Book.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAuthorId(Long authorId) {
        return entityManager.createQuery(
                        "select distinct b from Book b join b.authors a where a.id = :authorId",
                        Book.class)
                .setParameter("authorId", authorId)
                .getResultList();
    }
}