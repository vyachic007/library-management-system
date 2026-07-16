package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.AuthorDao;
import by.slava_borisov.library.dao.BookCopyDao;
import by.slava_borisov.library.dao.BookDao;
import by.slava_borisov.library.dao.CategoryDao;
import by.slava_borisov.library.dto.request.BookCreateRequestDto;
import by.slava_borisov.library.dto.request.BookUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookResponseDto;
import by.slava_borisov.library.exception.DuplicateException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.BookCopyMapper;
import by.slava_borisov.library.mapper.BookMapper;
import by.slava_borisov.library.model.Author;
import by.slava_borisov.library.model.Book;
import by.slava_borisov.library.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты BookServiceImpl")
class BookServiceImplTest {

    @Mock
    private BookDao bookDao;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private AuthorDao authorDao;

    @Mock
    private BookCopyDao bookCopyDao;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookCopyMapper bookCopyMapper;

    @InjectMocks
    private BookServiceImpl bookService;


    @Test
    @DisplayName("Должен вернуть книгу по ID, если книга существует")
    void getById_shouldReturnBook_whenBookExists() {
        Book book = new Book();
        book.setId(15L);
        book.setIsbn("12345M");
        book.setTitle("Война и мир");
        book.setDescription("Описание книги");
        book.setPublicationYear(2000);

        BookResponseDto bookResponseDto = new BookResponseDto(
                15L,
                "Война и мир",
                "12345M",
                2000
        );

        Mockito.when(bookDao.findById(15L))
                .thenReturn(Optional.of(book));

        Mockito.when(bookMapper.toResponseDto(book))
                .thenReturn(bookResponseDto);

        BookResponseDto result = bookService.getById(15L);

        assertEquals(15L, result.id());
        assertEquals("Война и мир", result.title());
        assertEquals("12345M", result.isbn());
        assertEquals(2000, result.publicationYear());

        Mockito.verify(bookDao).findById(15L);
        Mockito.verify(bookMapper).toResponseDto(book);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException, если книга по ID не найдена")
    void getById_shouldThrowException_whenBookNotFound() {
        Mockito.when(bookDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookService.getById(99L)
        );

        Mockito.verify(bookDao).findById(99L);
        verifyNoInteractions(bookMapper);
    }


    @Test
    @DisplayName("Должен вернуть список всех книг")
    void getAll_shouldReturnAllBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Война и мир");
        book1.setIsbn("ISBN-1");
        book1.setDescription("Описание первой книги");
        book1.setPublicationYear(2000);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Преступление и наказание");
        book2.setIsbn("ISBN-2");
        book2.setDescription("Описание второй книги");
        book2.setPublicationYear(2001);

        List<Book> books = List.of(book1, book2);

        BookResponseDto responseDto1 = new BookResponseDto(
                1L,
                "Война и мир",
                "ISBN-1",
                2000
        );

        BookResponseDto responseDto2 = new BookResponseDto(
                2L,
                "Преступление и наказание",
                "ISBN-2",
                2001
        );

        List<BookResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(bookDao.findAll())
                .thenReturn(books);

        Mockito.when(bookMapper.toResponseDtoList(books))
                .thenReturn(responseDtos);

        List<BookResponseDto> result = bookService.getAll();

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals("Война и мир", result.get(0).title());
        assertEquals("ISBN-1", result.get(0).isbn());
        assertEquals(2000, result.get(0).publicationYear());

        assertEquals(2L, result.get(1).id());
        assertEquals("Преступление и наказание", result.get(1).title());
        assertEquals("ISBN-2", result.get(1).isbn());
        assertEquals(2001, result.get(1).publicationYear());

        Mockito.verify(bookDao).findAll();
        Mockito.verify(bookMapper).toResponseDtoList(books);
    }


    @Test
    @DisplayName("Должен вернуть книгу по ISBN, если книга существует")
    void getByIsbn_shouldReturnBook_whenBookExists() {
        String isbn = "ISBN-1";

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");
        book.setIsbn(isbn);
        book.setDescription("Описание книги");
        book.setPublicationYear(2000);

        BookResponseDto responseDto = new BookResponseDto(
                1L,
                "Война и мир",
                isbn,
                2000
        );

        Mockito.when(bookDao.findByIsbn(isbn))
                .thenReturn(Optional.of(book));

        Mockito.when(bookMapper.toResponseDto(book))
                .thenReturn(responseDto);

        BookResponseDto result = bookService.getByIsbn(isbn);

        assertEquals(1L, result.id());
        assertEquals("Война и мир", result.title());
        assertEquals(isbn, result.isbn());
        assertEquals(2000, result.publicationYear());

        Mockito.verify(bookDao).findByIsbn(isbn);
        Mockito.verify(bookMapper).toResponseDto(book);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException, если книга по ISBN не найдена")
    void getByIsbn_shouldThrowException_whenBookNotFound() {
        String isbn = "UNKNOWN-ISBN";

        Mockito.when(bookDao.findByIsbn(isbn))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookService.getByIsbn(isbn)
        );

        Mockito.verify(bookDao).findByIsbn(isbn);
        verifyNoInteractions(bookMapper);
    }


    @Test
    @DisplayName("Должен создать книгу, если данные корректны")
    void create_shouldCreateBook_whenDataIsValid() {
        BookCreateRequestDto request = new BookCreateRequestDto(
                "Война и мир",
                "ISBN-1",
                "Описание книги",
                2000,
                10L,
                Set.of(100L, 101L)
        );

        Category category = new Category();
        category.setId(10L);
        category.setName("Роман");

        Author author1 = new Author();
        author1.setId(100L);
        author1.setFirstName("Лев");
        author1.setLastName("Толстой");

        Author author2 = new Author();
        author2.setId(101L);
        author2.setFirstName("Иван");
        author2.setLastName("Иванов");

        Book bookToSave = new Book();
        bookToSave.setTitle("Война и мир");
        bookToSave.setIsbn("ISBN-1");
        bookToSave.setDescription("Описание книги");
        bookToSave.setPublicationYear(2000);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Война и мир");
        savedBook.setIsbn("ISBN-1");
        savedBook.setDescription("Описание книги");
        savedBook.setPublicationYear(2000);
        savedBook.setCategory(category);
        savedBook.setAuthors(Set.of(author1, author2));

        BookResponseDto responseDto = new BookResponseDto(
                1L,
                "Война и мир",
                "ISBN-1",
                2000
        );

        Mockito.when(bookDao.existsByIsbn("ISBN-1"))
                .thenReturn(false);

        Mockito.when(bookMapper.toEntity(request))
                .thenReturn(bookToSave);

        Mockito.when(categoryDao.findById(10L))
                .thenReturn(Optional.of(category));

        Mockito.when(authorDao.findById(100L))
                .thenReturn(Optional.of(author1));

        Mockito.when(authorDao.findById(101L))
                .thenReturn(Optional.of(author2));

        Mockito.when(bookDao.save(bookToSave))
                .thenReturn(savedBook);

        Mockito.when(bookMapper.toResponseDto(savedBook))
                .thenReturn(responseDto);

        BookResponseDto result = bookService.create(request);

        assertEquals(1L, result.id());
        assertEquals("Война и мир", result.title());
        assertEquals("ISBN-1", result.isbn());
        assertEquals(2000, result.publicationYear());

        Mockito.verify(bookDao).existsByIsbn("ISBN-1");
        Mockito.verify(bookMapper).toEntity(request);
        Mockito.verify(categoryDao).findById(10L);
        Mockito.verify(authorDao).findById(100L);
        Mockito.verify(authorDao).findById(101L);
        Mockito.verify(bookDao).save(bookToSave);
        Mockito.verify(bookMapper).toResponseDto(savedBook);
    }


    @Test
    @DisplayName("Должен выбросить DuplicateException при создании, если ISBN уже существует")
    void create_shouldThrowException_whenIsbnAlreadyExists() {
        BookCreateRequestDto request = new BookCreateRequestDto(
                "Война и мир",
                "ISBN-1",
                "Описание книги",
                2000,
                10L,
                Set.of(100L)
        );

        Mockito.when(bookDao.existsByIsbn("ISBN-1"))
                .thenReturn(true);

        assertThrows(
                DuplicateException.class,
                () -> bookService.create(request)
        );

        Mockito.verify(bookDao).existsByIsbn("ISBN-1");
        Mockito.verify(bookDao, Mockito.never()).save(Mockito.any(Book.class));
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(categoryDao);
        verifyNoInteractions(authorDao);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при создании, если автор не найден")
    void create_shouldThrowException_whenAuthorNotFound() {
        BookCreateRequestDto request = new BookCreateRequestDto(
                "Война и мир",
                "ISBN-1",
                "Описание книги",
                2000,
                10L,
                Set.of(100L)
        );

        Category category = new Category();
        category.setId(10L);
        category.setName("Роман");

        Book bookToSave = new Book();
        bookToSave.setTitle("Война и мир");
        bookToSave.setIsbn("ISBN-1");
        bookToSave.setDescription("Описание книги");
        bookToSave.setPublicationYear(2000);

        Mockito.when(bookDao.existsByIsbn("ISBN-1"))
                .thenReturn(false);

        Mockito.when(bookMapper.toEntity(request))
                .thenReturn(bookToSave);

        Mockito.when(categoryDao.findById(10L))
                .thenReturn(Optional.of(category));

        Mockito.when(authorDao.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookService.create(request)
        );

        Mockito.verify(bookDao).existsByIsbn("ISBN-1");
        Mockito.verify(bookMapper).toEntity(request);
        Mockito.verify(categoryDao).findById(10L);
        Mockito.verify(authorDao).findById(100L);
        Mockito.verify(bookDao, Mockito.never()).save(Mockito.any(Book.class));
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при создании, если категория не найдена")
    void create_shouldThrowException_whenCategoryNotFound() {
        BookCreateRequestDto request = new BookCreateRequestDto(
                "Война и мир",
                "ISBN-1",
                "Описание книги",
                2000,
                99L,
                Set.of(100L)
        );

        Book bookToSave = new Book();
        bookToSave.setTitle("Война и мир");
        bookToSave.setIsbn("ISBN-1");
        bookToSave.setDescription("Описание книги");
        bookToSave.setPublicationYear(2000);

        Mockito.when(bookDao.existsByIsbn("ISBN-1"))
                .thenReturn(false);

        Mockito.when(bookMapper.toEntity(request))
                .thenReturn(bookToSave);

        Mockito.when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookService.create(request)
        );

        Mockito.verify(bookDao).existsByIsbn("ISBN-1");
        Mockito.verify(bookMapper).toEntity(request);
        Mockito.verify(categoryDao).findById(99L);
        Mockito.verify(bookDao, Mockito.never()).save(Mockito.any(Book.class));
        verifyNoInteractions(authorDao);
    }


    @Test
    @DisplayName("Должен обновить книгу, если книга существует и данные корректны")
    void update_shouldUpdateBook_whenBookExistsAndDataIsValid() {
        BookUpdateRequestDto request = new BookUpdateRequestDto(
                "Анна Каренина",
                "ISBN-2",
                "Новое описание книги",
                2001,
                10L,
                Set.of(100L)
        );

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");
        book.setIsbn("ISBN-1");
        book.setDescription("Старое описание книги");
        book.setPublicationYear(2000);

        Category category = new Category();
        category.setId(10L);
        category.setName("Роман");

        Author author = new Author();
        author.setId(100L);
        author.setFirstName("Лев");
        author.setLastName("Толстой");

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Анна Каренина");
        updatedBook.setIsbn("ISBN-2");
        updatedBook.setDescription("Новое описание книги");
        updatedBook.setPublicationYear(2001);
        updatedBook.setCategory(category);
        updatedBook.setAuthors(Set.of(author));

        BookResponseDto responseDto = new BookResponseDto(
                1L,
                "Анна Каренина",
                "ISBN-2",
                2001
        );

        Mockito.when(bookDao.findById(1L))
                .thenReturn(Optional.of(book));

        Mockito.when(bookDao.findByIsbn("ISBN-2"))
                .thenReturn(Optional.empty());

        Mockito.when(categoryDao.findById(10L))
                .thenReturn(Optional.of(category));

        Mockito.when(authorDao.findById(100L))
                .thenReturn(Optional.of(author));

        Mockito.when(bookDao.update(book))
                .thenReturn(updatedBook);

        Mockito.when(bookMapper.toResponseDto(updatedBook))
                .thenReturn(responseDto);

        BookResponseDto result = bookService.update(1L, request);

        assertEquals(1L, result.id());
        assertEquals("Анна Каренина", result.title());
        assertEquals("ISBN-2", result.isbn());
        assertEquals(2001, result.publicationYear());

        Mockito.verify(bookDao).findById(1L);
        Mockito.verify(bookDao).findByIsbn("ISBN-2");
        Mockito.verify(bookMapper).updateEntityFromDto(request, book);
        Mockito.verify(categoryDao).findById(10L);
        Mockito.verify(authorDao).findById(100L);
        Mockito.verify(bookDao).update(book);
        Mockito.verify(bookMapper).toResponseDto(updatedBook);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при обновлении, если книга не найдена")
    void update_shouldThrowException_whenBookNotFound() {
        BookUpdateRequestDto request = new BookUpdateRequestDto(
                "Анна Каренина",
                "ISBN-2",
                "Новое описание книги",
                2001,
                10L,
                Set.of(100L)
        );

        Mockito.when(bookDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookService.update(99L, request)
        );

        Mockito.verify(bookDao).findById(99L);
        Mockito.verify(bookDao, Mockito.never()).update(Mockito.any(Book.class));
        verifyNoInteractions(bookMapper);
        verifyNoInteractions(categoryDao);
        verifyNoInteractions(authorDao);
    }


    @Test
    @DisplayName("Должен выбросить DuplicateException при обновлении, если ISBN принадлежит другой книге")
    void update_shouldThrowException_whenIsbnAlreadyExists() {
        BookUpdateRequestDto request = new BookUpdateRequestDto(
                "Анна Каренина",
                "ISBN-2",
                "Новое описание книги",
                2001,
                10L,
                Set.of(100L)
        );

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");
        book.setIsbn("ISBN-1");

        Book existingBook = new Book();
        existingBook.setId(2L);
        existingBook.setTitle("Другая книга");
        existingBook.setIsbn("ISBN-2");

        Mockito.when(bookDao.findById(1L))
                .thenReturn(Optional.of(book));

        Mockito.when(bookDao.findByIsbn("ISBN-2"))
                .thenReturn(Optional.of(existingBook));

        assertThrows(
                DuplicateException.class,
                () -> bookService.update(1L, request)
        );

        Mockito.verify(bookDao).findById(1L);
        Mockito.verify(bookDao).findByIsbn("ISBN-2");
        Mockito.verify(bookDao, Mockito.never()).update(Mockito.any(Book.class));
        verifyNoInteractions(categoryDao);
        verifyNoInteractions(authorDao);
    }


    @Test
    @DisplayName("Должен удалить книгу, если книга существует")
    void delete_shouldDeleteBook_whenBookExists() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");
        book.setIsbn("ISBN-1");
        book.setDescription("Описание книги");
        book.setPublicationYear(2000);

        Mockito.when(bookDao.findById(1L))
                .thenReturn(Optional.of(book));

        bookService.delete(1L);

        Mockito.verify(bookDao).findById(1L);
        Mockito.verify(bookDao).delete(book);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при удалении, если книга не найдена")
    void delete_shouldThrowException_whenBookNotFound() {
        Mockito.when(bookDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookService.delete(99L)
        );

        Mockito.verify(bookDao).findById(99L);
        Mockito.verify(bookDao, Mockito.never()).delete(Mockito.any(Book.class));
    }
}