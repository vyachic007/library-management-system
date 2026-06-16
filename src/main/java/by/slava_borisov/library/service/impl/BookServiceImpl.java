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
import by.slava_borisov.library.exception.DuplicateException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.BookCopyMapper;
import by.slava_borisov.library.mapper.BookMapper;
import by.slava_borisov.library.model.Author;
import by.slava_borisov.library.model.Book;
import by.slava_borisov.library.model.Category;
import by.slava_borisov.library.service.BookService;
import by.slava_borisov.library.util.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Создание книги: title={}, isbn={}, categoryId={}, authorIds={}",
                requestDto.title(), requestDto.isbn(), requestDto.categoryId(), requestDto.authorIds());

        if (bookDao.existsByIsbn(requestDto.isbn())) {
            log.warn("Попытка создать книгу с уже существующим ISBN: isbn={}", requestDto.isbn());
            throw new DuplicateException(Messages.BOOK_ALREADY_EXISTS_BY_ISBN);
        }

        Book book = bookMapper.toEntity(requestDto);

        Category category = getCategoryEntityById(requestDto.categoryId());
        Set<Author> authors = getAuthorEntitiesByIds(requestDto.authorIds());

        book.setCategory(category);
        book.setAuthors(authors);

        Book savedBook = bookDao.save(book);

        log.info("Книга успешно создана: id={}, title={}, isbn={}",
                savedBook.getId(), savedBook.getTitle(), savedBook.getIsbn());

        return bookMapper.toResponseDto(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDto update(Long bookId, BookUpdateRequestDto requestDto) {
        log.info("Обновление книги: id={}, title={}, isbn={}, categoryId={}, authorIds={}",
                bookId, requestDto.title(), requestDto.isbn(), requestDto.categoryId(), requestDto.authorIds());

        Book book = getBookEntityById(bookId);

        bookDao.findByIsbn(requestDto.isbn())
                .filter(existingBook -> !existingBook.getId().equals(bookId))
                .ifPresent(existingBook -> {
                    log.warn("Попытка обновить книгу на уже существующий ISBN: id={}, isbn={}",
                            bookId, requestDto.isbn());
                    throw new DuplicateException(Messages.BOOK_ALREADY_EXISTS_BY_ISBN);
                });

        bookMapper.updateEntityFromDto(requestDto, book);

        Category category = getCategoryEntityById(requestDto.categoryId());
        Set<Author> authors = getAuthorEntitiesByIds(requestDto.authorIds());

        book.setCategory(category);
        book.setAuthors(authors);

        Book updatedBook = bookDao.update(book);

        log.info("Книга успешно обновлена: id={}, title={}, isbn={}",
                updatedBook.getId(), updatedBook.getTitle(), updatedBook.getIsbn());

        return bookMapper.toResponseDto(updatedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getById(Long bookId) {
        log.info("Получение книги по id={}", bookId);

        Book book = getBookEntityById(bookId);

        log.info("Книга найдена: id={}, title={}, isbn={}",
                book.getId(), book.getTitle(), book.getIsbn());

        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailsResponseDto getDetailsById(Long bookId) {
        log.info("Получение подробной информации о книге: id={}", bookId);

        Book book = getBookEntityById(bookId);

        log.info("Подробная информация о книге получена: id={}, title={}, isbn={}",
                book.getId(), book.getTitle(), book.getIsbn());

        return bookMapper.toDetailsResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getAll() {
        log.info("Получение списка всех книг");

        List<Book> books = bookDao.findAll();

        log.info("Получен список книг, количество={}", books.size());

        return bookMapper.toResponseDtoList(books);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getByCategoryId(Long categoryId) {
        log.info("Получение книг по категории: categoryId={}", categoryId);

        getCategoryEntityById(categoryId);
        List<Book> books = bookDao.findByCategoryId(categoryId);

        log.info("Получены книги по категории: categoryId={}, количество={}", categoryId, books.size());

        return bookMapper.toResponseDtoList(books);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> getByAuthorId(Long authorId) {
        log.info("Получение книг по автору: authorId={}", authorId);

        getAuthorEntityById(authorId);
        List<Book> books = bookDao.findByAuthorId(authorId);

        log.info("Получены книги по автору: authorId={}, количество={}", authorId, books.size());

        return bookMapper.toResponseDtoList(books);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getByIsbn(String isbn) {
        log.info("Получение книги по ISBN={}", isbn);

        Book book = bookDao.findByIsbn(isbn)
                .orElseThrow(() -> {
                    log.warn("Книга не найдена по ISBN={}", isbn);
                    return new NotFoundException(
                            Messages.BOOK_NOT_FOUND_BY_ISBN.formatted(isbn)
                    );
                });

        log.info("Книга найдена по ISBN: id={}, title={}, isbn={}",
                book.getId(), book.getTitle(), book.getIsbn());

        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchByTitle(String title) {
        log.info("Поиск книг по названию: title={}", title);

        List<Book> books = bookDao.findByTitleContaining(title);

        log.info("Поиск книг по названию завершён: title={}, найдено={}", title, books.size());

        return bookMapper.toResponseDtoList(books);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchByAuthor(String authorLastName) {
        log.info("Поиск книг по фамилии автора: authorLastName={}", authorLastName);

        List<Author> authors = authorDao.findByLastName(authorLastName);

        Set<Long> authorIds = authors.stream()
                .map(Author::getId)
                .collect(Collectors.toSet());

        List<Book> books = authorIds.stream()
                .flatMap(authorId -> bookDao.findByAuthorId(authorId).stream())
                .distinct()
                .toList();

        log.info("Поиск книг по фамилии автора завершён: authorLastName={}, найдено авторов={}, найдено книг={}",
                authorLastName, authors.size(), books.size());

        return bookMapper.toResponseDtoList(books);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchByCategory(Long categoryId) {
        log.info("Поиск книг по категории: categoryId={}", categoryId);

        List<BookResponseDto> books = getByCategoryId(categoryId);

        log.info("Поиск книг по категории завершён: categoryId={}, найдено={}", categoryId, books.size());

        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getAvailableCopies(Long bookId) {
        log.info("Получение доступных экземпляров книги: bookId={}", bookId);

        getBookEntityById(bookId);
        List<BookCopyResponseDto> availableCopies =
                bookCopyMapper.toResponseDtoList(bookCopyDao.findAvailableByBookId(bookId));

        log.info("Получены доступные экземпляры книги: bookId={}, количество={}",
                bookId, availableCopies.size());

        return availableCopies;
    }

    @Override
    @Transactional
    public void delete(Long bookId) {
        log.info("Удаление книги: id={}", bookId);

        Book book = getBookEntityById(bookId);
        bookDao.delete(book);

        log.info("Книга успешно удалена: id={}, title={}, isbn={}",
                book.getId(), book.getTitle(), book.getIsbn());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> search(
            String title,
            String author,
            Long categoryId,
            String isbn
    ) {
        log.info("Поиск книг: title={}, author={}, categoryId={}, isbn={}",
                title, author, categoryId, isbn);

        List<BookResponseDto> books = bookDao.search(title, author, categoryId, isbn)
                .stream()
                .map(bookMapper::toResponseDto)
                .toList();

        log.info("Поиск книг завершён: найдено={}", books.size());

        return books;
    }

    private Book getBookEntityById(Long bookId) {
        return bookDao.findById(bookId)
                .orElseThrow(() -> {
                    log.warn("Книга не найдена: id={}", bookId);
                    return new NotFoundException(
                            Messages.BOOK_NOT_FOUND_BY_ID.formatted(bookId)
                    );
                });
    }

    private Category getCategoryEntityById(Long categoryId) {
        return categoryDao.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Категория не найдена: id={}", categoryId);
                    return new NotFoundException(
                            Messages.CATEGORY_NOT_FOUND_BY_ID.formatted(categoryId)
                    );
                });
    }

    private Author getAuthorEntityById(Long authorId) {
        return authorDao.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("Автор не найден: id={}", authorId);
                    return new NotFoundException(
                            Messages.AUTHOR_NOT_FOUND_BY_ID.formatted(authorId)
                    );
                });
    }

    private Set<Author> getAuthorEntitiesByIds(Set<Long> authorIds) {
        log.info("Получение авторов по списку id: authorIds={}", authorIds);

        Set<Author> authors = authorIds.stream()
                .map(this::getAuthorEntityById)
                .collect(Collectors.toSet());

        log.info("Авторы успешно получены, количество={}", authors.size());

        return authors;
    }
}