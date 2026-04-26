package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.AuthorDao;
import by.slava_borisov.library.dao.BookCopyDao;
import by.slava_borisov.library.dao.BookDao;
import by.slava_borisov.library.dao.CategoryDao;
import by.slava_borisov.library.dto.request.BookCreateRequestDto;
import by.slava_borisov.library.dto.request.BookUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.dto.response.BookDetailsResponseDto;
import by.slava_borisov.library.dto.response.BookResponseDto;
import by.slava_borisov.library.mapper.BookCopyMapper;
import by.slava_borisov.library.mapper.BookMapper;
import by.slava_borisov.library.model.Author;
import by.slava_borisov.library.model.Book;
import by.slava_borisov.library.model.Category;
import by.slava_borisov.library.service.BookService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final CategoryDao categoryDao;
    private final AuthorDao authorDao;
    private final BookCopyDao bookCopyDao;
    private final BookMapper bookMapper;
    private final BookCopyMapper bookCopyMapper;

    @Override
    @Transactional
    public BookResponseDto create(BookCreateRequestDto requestDto) {
        if (bookDao.existsByIsbn(requestDto.isbn())) {
            throw new IllegalArgumentException(Messages.BOOK_ALREADY_EXISTS_BY_ISBN);
        }

        Book book = bookMapper.toEntity(requestDto);

        Category category = getCategoryEntityById(requestDto.categoryId());
        Set<Author> authors = getAuthorEntitiesByIds(requestDto.authorIds());

        book.setCategory(category);
        book.setAuthors(authors);

        Book savedBook = bookDao.save(book);
        return bookMapper.toResponseDto(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDto update(Long bookId, BookUpdateRequestDto requestDto) {
        Book book = getBookEntityById(bookId);

        bookDao.findByIsbn(requestDto.isbn())
                .filter(existingBook -> !existingBook.getId().equals(bookId))
                .ifPresent(existingBook -> {
                    throw new IllegalArgumentException(Messages.BOOK_ALREADY_EXISTS_BY_ISBN);
                });

        bookMapper.updateEntityFromDto(requestDto, book);

        Category category = getCategoryEntityById(requestDto.categoryId());
        Set<Author> authors = getAuthorEntitiesByIds(requestDto.authorIds());

        book.setCategory(category);
        book.setAuthors(authors);

        Book updatedBook = bookDao.update(book);
        return bookMapper.toResponseDto(updatedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getById(Long bookId) {
        Book book = getBookEntityById(bookId);
        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailsResponseDto getDetailsById(Long bookId) {
        Book book = getBookEntityById(bookId);
        return bookMapper.toDetailsResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getAll() {
        return bookMapper.toResponseDtoList(bookDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getByCategoryId(Long categoryId) {
        getCategoryEntityById(categoryId);
        return bookMapper.toResponseDtoList(bookDao.findByCategoryId(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getByAuthorId(Long authorId) {
        getAuthorEntityById(authorId);
        return bookMapper.toResponseDtoList(bookDao.findByAuthorId(authorId));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getByIsbn(String isbn) {
        Book book = bookDao.findByIsbn(isbn)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.BOOK_NOT_FOUND_BY_ISBN.formatted(isbn)
                ));

        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchByTitle(String title) {
        return bookMapper.toResponseDtoList(bookDao.findByTitleContaining(title));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchByAuthor(String authorLastName) {
        List<Author> authors = authorDao.findByLastName(authorLastName);

        Set<Long> authorIds = authors.stream()
                .map(Author::getId)
                .collect(Collectors.toSet());

        return bookMapper.toResponseDtoList(
                authorIds.stream()
                        .flatMap(authorId -> bookDao.findByAuthorId(authorId).stream())
                        .distinct()
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchByCategory(Long categoryId) {
        return getByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getAvailableCopies(Long bookId) {
        getBookEntityById(bookId);
        return bookCopyMapper.toResponseDtoList(bookCopyDao.findAvailableByBookId(bookId));
    }

    @Override
    @Transactional
    public void delete(Long bookId) {
        Book book = getBookEntityById(bookId);
        bookDao.delete(book);
    }

    private Book getBookEntityById(Long bookId) {
        return bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.BOOK_NOT_FOUND_BY_ID.formatted(bookId)
                ));
    }

    private Category getCategoryEntityById(Long categoryId) {
        return categoryDao.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.CATEGORY_NOT_FOUND_BY_ID.formatted(categoryId)
                ));
    }

    private Author getAuthorEntityById(Long authorId) {
        return authorDao.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.AUTHOR_NOT_FOUND_BY_ID.formatted(authorId)
                ));
    }

    private Set<Author> getAuthorEntitiesByIds(Set<Long> authorIds) {
        return authorIds.stream()
                .map(this::getAuthorEntityById)
                .collect(Collectors.toSet());
    }
}