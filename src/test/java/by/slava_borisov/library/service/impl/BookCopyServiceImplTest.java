package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.BookCopyDao;
import by.slava_borisov.library.dao.BookDao;
import by.slava_borisov.library.dto.request.BookCopyCreateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyStatusUpdateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.exception.DuplicateException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.BookCopyMapper;
import by.slava_borisov.library.model.Book;
import by.slava_borisov.library.model.BookCopy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты BookCopyServiceImpl")
class BookCopyServiceImplTest {

    @Mock
    private BookCopyDao bookCopyDao;

    @Mock
    private BookDao bookDao;

    @Mock
    private BookCopyMapper bookCopyMapper;

    @InjectMocks
    private BookCopyServiceImpl bookCopyService;


    @Test
    @DisplayName("Должен создать экземпляр книги, если данные корректны")
    void create_shouldCreateBookCopy_whenDataIsValid() {
        BookCopyCreateRequestDto request = new BookCopyCreateRequestDto(
                10L,
                "INV-001",
                "AVAILABLE",
                "Новый экземпляр"
        );

        Book book = new Book();
        book.setId(10L);
        book.setTitle("Война и мир");

        BookCopy bookCopyToSave = new BookCopy();
        bookCopyToSave.setInventoryNumber("INV-001");
        bookCopyToSave.setStatus("AVAILABLE");
        bookCopyToSave.setConditionDescription("Новый экземпляр");

        BookCopy savedBookCopy = new BookCopy();
        savedBookCopy.setId(1L);
        savedBookCopy.setBook(book);
        savedBookCopy.setInventoryNumber("INV-001");
        savedBookCopy.setStatus("AVAILABLE");
        savedBookCopy.setConditionDescription("Новый экземпляр");

        BookCopyResponseDto responseDto = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "AVAILABLE",
                "Новый экземпляр"
        );

        Mockito.when(bookCopyDao.existsByInventoryNumber("INV-001"))
                .thenReturn(false);

        Mockito.when(bookDao.findById(10L))
                .thenReturn(Optional.of(book));

        Mockito.when(bookCopyMapper.toEntity(request))
                .thenReturn(bookCopyToSave);

        Mockito.when(bookCopyDao.save(bookCopyToSave))
                .thenReturn(savedBookCopy);

        Mockito.when(bookCopyMapper.toResponseDto(savedBookCopy))
                .thenReturn(responseDto);

        BookCopyResponseDto result = bookCopyService.create(request);

        assertEquals(1L, result.id());
        assertEquals(10L, result.bookId());
        assertEquals("Война и мир", result.bookTitle());
        assertEquals("INV-001", result.inventoryNumber());
        assertEquals("AVAILABLE", result.status());
        assertEquals("Новый экземпляр", result.conditionDescription());

        Mockito.verify(bookCopyDao).existsByInventoryNumber("INV-001");
        Mockito.verify(bookDao).findById(10L);
        Mockito.verify(bookCopyMapper).toEntity(request);
        Mockito.verify(bookCopyDao).save(bookCopyToSave);
        Mockito.verify(bookCopyMapper).toResponseDto(savedBookCopy);
    }


    @Test
    @DisplayName("Должен выбросить DuplicateException при создании, если инвентарный номер уже существует")
    void create_shouldThrowException_whenInventoryNumberAlreadyExists() {
        BookCopyCreateRequestDto request = new BookCopyCreateRequestDto(
                10L,
                "INV-001",
                "AVAILABLE",
                "Новый экземпляр"
        );

        Mockito.when(bookCopyDao.existsByInventoryNumber("INV-001"))
                .thenReturn(true);

        assertThrows(
                DuplicateException.class,
                () -> bookCopyService.create(request)
        );

        Mockito.verify(bookCopyDao).existsByInventoryNumber("INV-001");
        Mockito.verify(bookCopyDao, Mockito.never()).save(Mockito.any(BookCopy.class));
        verifyNoInteractions(bookDao);
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при создании, если книга не найдена")
    void create_shouldThrowException_whenBookNotFound() {
        BookCopyCreateRequestDto request = new BookCopyCreateRequestDto(
                99L,
                "INV-001",
                "AVAILABLE",
                "Новый экземпляр"
        );

        Mockito.when(bookCopyDao.existsByInventoryNumber("INV-001"))
                .thenReturn(false);

        Mockito.when(bookDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.create(request)
        );

        Mockito.verify(bookCopyDao).existsByInventoryNumber("INV-001");
        Mockito.verify(bookDao).findById(99L);
        Mockito.verify(bookCopyDao, Mockito.never()).save(Mockito.any(BookCopy.class));
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен обновить экземпляр книги, если данные корректны")
    void update_shouldUpdateBookCopy_whenDataIsValid() {
        BookCopyUpdateRequestDto request = new BookCopyUpdateRequestDto(
                10L,
                "INV-002",
                "DAMAGED",
                "Есть повреждения"
        );

        Book oldBook = new Book();
        oldBook.setId(5L);
        oldBook.setTitle("Старая книга");

        Book newBook = new Book();
        newBook.setId(10L);
        newBook.setTitle("Война и мир");

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(oldBook);
        bookCopy.setInventoryNumber("INV-001");
        bookCopy.setStatus("AVAILABLE");
        bookCopy.setConditionDescription("Новый экземпляр");

        BookCopy updatedBookCopy = new BookCopy();
        updatedBookCopy.setId(1L);
        updatedBookCopy.setBook(newBook);
        updatedBookCopy.setInventoryNumber("INV-002");
        updatedBookCopy.setStatus("DAMAGED");
        updatedBookCopy.setConditionDescription("Есть повреждения");

        BookCopyResponseDto responseDto = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-002",
                "DAMAGED",
                "Есть повреждения"
        );

        Mockito.when(bookCopyDao.findById(1L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(bookCopyDao.findByInventoryNumber("INV-002"))
                .thenReturn(Optional.empty());

        Mockito.when(bookDao.findById(10L))
                .thenReturn(Optional.of(newBook));

        Mockito.when(bookCopyDao.update(bookCopy))
                .thenReturn(updatedBookCopy);

        Mockito.when(bookCopyMapper.toResponseDto(updatedBookCopy))
                .thenReturn(responseDto);

        BookCopyResponseDto result = bookCopyService.update(1L, request);

        assertEquals(1L, result.id());
        assertEquals(10L, result.bookId());
        assertEquals("Война и мир", result.bookTitle());
        assertEquals("INV-002", result.inventoryNumber());
        assertEquals("DAMAGED", result.status());
        assertEquals("Есть повреждения", result.conditionDescription());

        Mockito.verify(bookCopyDao).findById(1L);
        Mockito.verify(bookCopyDao).findByInventoryNumber("INV-002");
        Mockito.verify(bookDao).findById(10L);
        Mockito.verify(bookCopyMapper).updateEntityFromDto(request, bookCopy);
        Mockito.verify(bookCopyDao).update(bookCopy);
        Mockito.verify(bookCopyMapper).toResponseDto(updatedBookCopy);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при обновлении, если экземпляр книги не найден")
    void update_shouldThrowException_whenBookCopyNotFound() {
        BookCopyUpdateRequestDto request = new BookCopyUpdateRequestDto(
                10L,
                "INV-002",
                "DAMAGED",
                "Есть повреждения"
        );

        Mockito.when(bookCopyDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.update(99L, request)
        );

        Mockito.verify(bookCopyDao).findById(99L);
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
        verifyNoInteractions(bookDao);
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен выбросить DuplicateException при обновлении, если инвентарный номер принадлежит другому экземпляру")
    void update_shouldThrowException_whenInventoryNumberAlreadyExists() {
        BookCopyUpdateRequestDto request = new BookCopyUpdateRequestDto(
                10L,
                "INV-002",
                "AVAILABLE",
                "Хорошее состояние"
        );

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setInventoryNumber("INV-001");

        BookCopy existingBookCopy = new BookCopy();
        existingBookCopy.setId(2L);
        existingBookCopy.setInventoryNumber("INV-002");

        Mockito.when(bookCopyDao.findById(1L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(bookCopyDao.findByInventoryNumber("INV-002"))
                .thenReturn(Optional.of(existingBookCopy));

        assertThrows(
                DuplicateException.class,
                () -> bookCopyService.update(1L, request)
        );

        Mockito.verify(bookCopyDao).findById(1L);
        Mockito.verify(bookCopyDao).findByInventoryNumber("INV-002");
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
        verifyNoInteractions(bookDao);
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при обновлении, если книга не найдена")
    void update_shouldThrowException_whenBookNotFound() {
        BookCopyUpdateRequestDto request = new BookCopyUpdateRequestDto(
                99L,
                "INV-002",
                "AVAILABLE",
                "Хорошее состояние"
        );

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setInventoryNumber("INV-001");

        Mockito.when(bookCopyDao.findById(1L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(bookCopyDao.findByInventoryNumber("INV-002"))
                .thenReturn(Optional.empty());

        Mockito.when(bookDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.update(1L, request)
        );

        Mockito.verify(bookCopyDao).findById(1L);
        Mockito.verify(bookCopyDao).findByInventoryNumber("INV-002");
        Mockito.verify(bookDao).findById(99L);
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
    }


    @Test
    @DisplayName("Должен вернуть экземпляр книги по ID, если он существует")
    void getById_shouldReturnBookCopy_whenBookCopyExists() {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Война и мир");

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setInventoryNumber("INV-001");
        bookCopy.setStatus("AVAILABLE");
        bookCopy.setConditionDescription("Новый экземпляр");

        BookCopyResponseDto responseDto = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "AVAILABLE",
                "Новый экземпляр"
        );

        Mockito.when(bookCopyDao.findById(1L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(bookCopyMapper.toResponseDto(bookCopy))
                .thenReturn(responseDto);

        BookCopyResponseDto result = bookCopyService.getById(1L);

        assertEquals(1L, result.id());
        assertEquals(10L, result.bookId());
        assertEquals("Война и мир", result.bookTitle());
        assertEquals("INV-001", result.inventoryNumber());
        assertEquals("AVAILABLE", result.status());
        assertEquals("Новый экземпляр", result.conditionDescription());

        Mockito.verify(bookCopyDao).findById(1L);
        Mockito.verify(bookCopyMapper).toResponseDto(bookCopy);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException, если экземпляр книги по ID не найден")
    void getById_shouldThrowException_whenBookCopyNotFound() {
        Mockito.when(bookCopyDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.getById(99L)
        );

        Mockito.verify(bookCopyDao).findById(99L);
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен вернуть список всех экземпляров книг")
    void getAll_shouldReturnAllBookCopies() {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Война и мир");

        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);
        bookCopy1.setBook(book);
        bookCopy1.setInventoryNumber("INV-001");
        bookCopy1.setStatus("AVAILABLE");

        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);
        bookCopy2.setBook(book);
        bookCopy2.setInventoryNumber("INV-002");
        bookCopy2.setStatus("BORROWED");

        List<BookCopy> bookCopies = List.of(bookCopy1, bookCopy2);

        BookCopyResponseDto responseDto1 = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "AVAILABLE",
                null
        );

        BookCopyResponseDto responseDto2 = new BookCopyResponseDto(
                2L,
                10L,
                "Война и мир",
                "INV-002",
                "BORROWED",
                null
        );

        List<BookCopyResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(bookCopyDao.findAll())
                .thenReturn(bookCopies);

        Mockito.when(bookCopyMapper.toResponseDtoList(bookCopies))
                .thenReturn(responseDtos);

        List<BookCopyResponseDto> result = bookCopyService.getAll();

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals("INV-001", result.get(0).inventoryNumber());
        assertEquals("AVAILABLE", result.get(0).status());

        assertEquals(2L, result.get(1).id());
        assertEquals("INV-002", result.get(1).inventoryNumber());
        assertEquals("BORROWED", result.get(1).status());

        Mockito.verify(bookCopyDao).findAll();
        Mockito.verify(bookCopyMapper).toResponseDtoList(bookCopies);
    }


    @Test
    @DisplayName("Должен вернуть экземпляры книги по ID книги, если книга существует")
    void getByBookId_shouldReturnBookCopies_whenBookExists() {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Война и мир");

        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);
        bookCopy1.setBook(book);
        bookCopy1.setInventoryNumber("INV-001");
        bookCopy1.setStatus("AVAILABLE");

        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);
        bookCopy2.setBook(book);
        bookCopy2.setInventoryNumber("INV-002");
        bookCopy2.setStatus("BORROWED");

        List<BookCopy> bookCopies = List.of(bookCopy1, bookCopy2);

        BookCopyResponseDto responseDto1 = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "AVAILABLE",
                null
        );

        BookCopyResponseDto responseDto2 = new BookCopyResponseDto(
                2L,
                10L,
                "Война и мир",
                "INV-002",
                "BORROWED",
                null
        );

        List<BookCopyResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(bookDao.findById(10L))
                .thenReturn(Optional.of(book));

        Mockito.when(bookCopyDao.findByBookId(10L))
                .thenReturn(bookCopies);

        Mockito.when(bookCopyMapper.toResponseDtoList(bookCopies))
                .thenReturn(responseDtos);

        List<BookCopyResponseDto> result = bookCopyService.getByBookId(10L);

        assertEquals(2, result.size());
        assertEquals("INV-001", result.get(0).inventoryNumber());
        assertEquals("INV-002", result.get(1).inventoryNumber());

        Mockito.verify(bookDao).findById(10L);
        Mockito.verify(bookCopyDao).findByBookId(10L);
        Mockito.verify(bookCopyMapper).toResponseDtoList(bookCopies);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при получении экземпляров, если книга не найдена")
    void getByBookId_shouldThrowException_whenBookNotFound() {
        Mockito.when(bookDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.getByBookId(99L)
        );

        Mockito.verify(bookDao).findById(99L);
        Mockito.verify(bookCopyDao, Mockito.never()).findByBookId(99L);
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен вернуть экземпляры книг по статусу")
    void getByStatus_shouldReturnBookCopies() {
        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);
        bookCopy1.setInventoryNumber("INV-001");
        bookCopy1.setStatus("AVAILABLE");

        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);
        bookCopy2.setInventoryNumber("INV-002");
        bookCopy2.setStatus("AVAILABLE");

        List<BookCopy> bookCopies = List.of(bookCopy1, bookCopy2);

        BookCopyResponseDto responseDto1 = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "AVAILABLE",
                null
        );

        BookCopyResponseDto responseDto2 = new BookCopyResponseDto(
                2L,
                11L,
                "Анна Каренина",
                "INV-002",
                "AVAILABLE",
                null
        );

        List<BookCopyResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(bookCopyDao.findByStatus("AVAILABLE"))
                .thenReturn(bookCopies);

        Mockito.when(bookCopyMapper.toResponseDtoList(bookCopies))
                .thenReturn(responseDtos);

        List<BookCopyResponseDto> result = bookCopyService.getByStatus("AVAILABLE");

        assertEquals(2, result.size());
        assertEquals("AVAILABLE", result.get(0).status());
        assertEquals("AVAILABLE", result.get(1).status());

        Mockito.verify(bookCopyDao).findByStatus("AVAILABLE");
        Mockito.verify(bookCopyMapper).toResponseDtoList(bookCopies);
    }


    @Test
    @DisplayName("Должен вернуть экземпляр книги по инвентарному номеру, если он существует")
    void getByInventoryNumber_shouldReturnBookCopy_whenBookCopyExists() {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Война и мир");

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setInventoryNumber("INV-001");
        bookCopy.setStatus("AVAILABLE");
        bookCopy.setConditionDescription("Новый экземпляр");

        BookCopyResponseDto responseDto = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "AVAILABLE",
                "Новый экземпляр"
        );

        Mockito.when(bookCopyDao.findByInventoryNumber("INV-001"))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(bookCopyMapper.toResponseDto(bookCopy))
                .thenReturn(responseDto);

        BookCopyResponseDto result = bookCopyService.getByInventoryNumber("INV-001");

        assertEquals(1L, result.id());
        assertEquals("INV-001", result.inventoryNumber());
        assertEquals("AVAILABLE", result.status());

        Mockito.verify(bookCopyDao).findByInventoryNumber("INV-001");
        Mockito.verify(bookCopyMapper).toResponseDto(bookCopy);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException, если экземпляр по инвентарному номеру не найден")
    void getByInventoryNumber_shouldThrowException_whenBookCopyNotFound() {
        Mockito.when(bookCopyDao.findByInventoryNumber("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.getByInventoryNumber("UNKNOWN")
        );

        Mockito.verify(bookCopyDao).findByInventoryNumber("UNKNOWN");
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен изменить статус экземпляра книги")
    void changeStatus_shouldChangeBookCopyStatus() {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Война и мир");

        BookCopyStatusUpdateRequestDto request = new BookCopyStatusUpdateRequestDto(
                "BORROWED"
        );

        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setInventoryNumber("INV-001");
        bookCopy.setStatus("AVAILABLE");

        BookCopy updatedBookCopy = new BookCopy();
        updatedBookCopy.setId(1L);
        updatedBookCopy.setBook(book);
        updatedBookCopy.setInventoryNumber("INV-001");
        updatedBookCopy.setStatus("BORROWED");

        BookCopyResponseDto responseDto = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "BORROWED",
                null
        );

        Mockito.when(bookCopyDao.findById(1L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(bookCopyDao.update(bookCopy))
                .thenReturn(updatedBookCopy);

        Mockito.when(bookCopyMapper.toResponseDto(updatedBookCopy))
                .thenReturn(responseDto);

        BookCopyResponseDto result = bookCopyService.changeStatus(1L, request);

        assertEquals(1L, result.id());
        assertEquals("BORROWED", result.status());

        Mockito.verify(bookCopyDao).findById(1L);
        Mockito.verify(bookCopyDao).update(bookCopy);
        Mockito.verify(bookCopyMapper).toResponseDto(updatedBookCopy);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при изменении статуса, если экземпляр не найден")
    void changeStatus_shouldThrowException_whenBookCopyNotFound() {
        BookCopyStatusUpdateRequestDto request = new BookCopyStatusUpdateRequestDto(
                "BORROWED"
        );

        Mockito.when(bookCopyDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.changeStatus(99L, request)
        );

        Mockito.verify(bookCopyDao).findById(99L);
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен вернуть доступные экземпляры книги, если книга существует")
    void getAvailableByBookId_shouldReturnAvailableBookCopies_whenBookExists() {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Война и мир");

        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);
        bookCopy1.setBook(book);
        bookCopy1.setInventoryNumber("INV-001");
        bookCopy1.setStatus("AVAILABLE");

        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);
        bookCopy2.setBook(book);
        bookCopy2.setInventoryNumber("INV-002");
        bookCopy2.setStatus("AVAILABLE");

        List<BookCopy> bookCopies = List.of(bookCopy1, bookCopy2);

        BookCopyResponseDto responseDto1 = new BookCopyResponseDto(
                1L,
                10L,
                "Война и мир",
                "INV-001",
                "AVAILABLE",
                null
        );

        BookCopyResponseDto responseDto2 = new BookCopyResponseDto(
                2L,
                10L,
                "Война и мир",
                "INV-002",
                "AVAILABLE",
                null
        );

        List<BookCopyResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(bookDao.findById(10L))
                .thenReturn(Optional.of(book));

        Mockito.when(bookCopyDao.findAvailableByBookId(10L))
                .thenReturn(bookCopies);

        Mockito.when(bookCopyMapper.toResponseDtoList(bookCopies))
                .thenReturn(responseDtos);

        List<BookCopyResponseDto> result = bookCopyService.getAvailableByBookId(10L);

        assertEquals(2, result.size());
        assertEquals("AVAILABLE", result.get(0).status());
        assertEquals("AVAILABLE", result.get(1).status());

        Mockito.verify(bookDao).findById(10L);
        Mockito.verify(bookCopyDao).findAvailableByBookId(10L);
        Mockito.verify(bookCopyMapper).toResponseDtoList(bookCopies);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при получении доступных экземпляров, если книга не найдена")
    void getAvailableByBookId_shouldThrowException_whenBookNotFound() {
        Mockito.when(bookDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.getAvailableByBookId(99L)
        );

        Mockito.verify(bookDao).findById(99L);
        Mockito.verify(bookCopyDao, Mockito.never()).findAvailableByBookId(99L);
        verifyNoInteractions(bookCopyMapper);
    }


    @Test
    @DisplayName("Должен удалить экземпляр книги, если он существует")
    void delete_shouldDeleteBookCopy_whenBookCopyExists() {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setInventoryNumber("INV-001");
        bookCopy.setStatus("AVAILABLE");

        Mockito.when(bookCopyDao.findById(1L))
                .thenReturn(Optional.of(bookCopy));

        bookCopyService.delete(1L);

        Mockito.verify(bookCopyDao).findById(1L);
        Mockito.verify(bookCopyDao).delete(bookCopy);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при удалении, если экземпляр книги не найден")
    void delete_shouldThrowException_whenBookCopyNotFound() {
        Mockito.when(bookCopyDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> bookCopyService.delete(99L)
        );

        Mockito.verify(bookCopyDao).findById(99L);
        Mockito.verify(bookCopyDao, Mockito.never()).delete(Mockito.any(BookCopy.class));
    }
}