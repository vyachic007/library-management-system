package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.BookCopyDao;
import by.slava_borisov.library.dao.BorrowRecordDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.request.BorrowBookRequestDto;
import by.slava_borisov.library.dto.request.ExtendBorrowRequestDto;
import by.slava_borisov.library.dto.request.ReturnBookRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.exception.IllegalBookStateException;
import by.slava_borisov.library.mapper.BorrowRecordMapper;
import by.slava_borisov.library.model.Book;
import by.slava_borisov.library.model.BookCopy;
import by.slava_borisov.library.model.BorrowRecord;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.model.enums.BookCopyStatus;
import by.slava_borisov.library.model.enums.BorrowRecordStatus;
import by.slava_borisov.library.service.AccessControlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты BorrowRecordServiceImpl")
class BorrowRecordServiceImplTest {

    @Mock
    private BorrowRecordDao borrowRecordDao;

    @Mock
    private UserDao userDao;

    @Mock
    private BookCopyDao bookCopyDao;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @Mock
    private AccessControlService accessControlService;

    @InjectMocks
    private BorrowRecordServiceImpl borrowRecordService;


    @Test
    @DisplayName("Должен оформить аренду книги, если экземпляр доступен")
    void borrowBook_shouldBorrowBook_whenBookCopyIsAvailable() {
        LocalDate borrowedAt = LocalDate.of(2026, 7, 1);
        LocalDate dueDate = LocalDate.of(2026, 7, 15);

        BorrowBookRequestDto request = new BorrowBookRequestDto(
                1L,
                10L,
                borrowedAt,
                dueDate
        );

        User user = createUser(1L, "testuser");

        Book book = createBook(100L, "Война и мир");

        BookCopy bookCopy = createBookCopy(
                10L,
                book,
                "INV-001",
                BookCopyStatus.AVAILABLE.name()
        );

        BorrowRecord borrowRecordToSave = new BorrowRecord();
        borrowRecordToSave.setBorrowedAt(borrowedAt);
        borrowRecordToSave.setDueDate(dueDate);

        BorrowRecord savedBorrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                borrowedAt,
                dueDate,
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        BorrowRecordResponseDto responseDto = createResponseDto(
                50L,
                1L,
                "testuser",
                10L,
                "INV-001",
                "Война и мир",
                borrowedAt,
                dueDate,
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(bookCopyDao.findById(10L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(borrowRecordMapper.toEntity(request))
                .thenReturn(borrowRecordToSave);

        Mockito.when(bookCopyDao.update(bookCopy))
                .thenReturn(bookCopy);

        Mockito.when(borrowRecordDao.save(borrowRecordToSave))
                .thenReturn(savedBorrowRecord);

        Mockito.when(borrowRecordMapper.toResponseDto(savedBorrowRecord))
                .thenReturn(responseDto);

        BorrowRecordResponseDto result = borrowRecordService.borrowBook(request);

        assertEquals(50L, result.id());
        assertEquals(1L, result.userId());
        assertEquals(10L, result.bookCopyId());
        assertEquals("INV-001", result.inventoryNumber());
        assertEquals("Война и мир", result.bookTitle());
        assertEquals(BorrowRecordStatus.BORROWED.name(), result.status());

        assertEquals(BookCopyStatus.BORROWED.name(), bookCopy.getStatus());
        assertEquals(user, borrowRecordToSave.getUser());
        assertEquals(bookCopy, borrowRecordToSave.getBookCopy());
        assertEquals(BorrowRecordStatus.BORROWED.name(), borrowRecordToSave.getStatus());

        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(bookCopyDao).findById(10L);
        Mockito.verify(borrowRecordMapper).toEntity(request);
        Mockito.verify(bookCopyDao).update(bookCopy);
        Mockito.verify(borrowRecordDao).save(borrowRecordToSave);
        Mockito.verify(borrowRecordMapper).toResponseDto(savedBorrowRecord);
    }


    @Test
    @DisplayName("Должен выбросить EntityNotFoundException при аренде, если пользователь не найден")
    void borrowBook_shouldThrowException_whenUserNotFound() {
        BorrowBookRequestDto request = new BorrowBookRequestDto(
                99L,
                10L,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15)
        );

        Mockito.when(userDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> borrowRecordService.borrowBook(request)
        );

        Mockito.verify(accessControlService).checkUserAccess(99L);
        Mockito.verify(userDao).findById(99L);
        verifyNoInteractions(borrowRecordMapper);
        Mockito.verify(bookCopyDao, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(borrowRecordDao, Mockito.never()).save(Mockito.any(BorrowRecord.class));
    }


    @Test
    @DisplayName("Должен выбросить EntityNotFoundException при аренде, если экземпляр книги не найден")
    void borrowBook_shouldThrowException_whenBookCopyNotFound() {
        BorrowBookRequestDto request = new BorrowBookRequestDto(
                1L,
                99L,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15)
        );

        User user = createUser(1L, "testuser");

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(bookCopyDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> borrowRecordService.borrowBook(request)
        );

        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(bookCopyDao).findById(99L);
        verifyNoInteractions(borrowRecordMapper);
        Mockito.verify(borrowRecordDao, Mockito.never()).save(Mockito.any(BorrowRecord.class));
    }


    @Test
    @DisplayName("Должен выбросить IllegalBookStateException при аренде, если экземпляр недоступен")
    void borrowBook_shouldThrowException_whenBookCopyIsNotAvailable() {
        BorrowBookRequestDto request = new BorrowBookRequestDto(
                1L,
                10L,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15)
        );

        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");

        BookCopy bookCopy = createBookCopy(
                10L,
                book,
                "INV-001",
                BookCopyStatus.BORROWED.name()
        );

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(bookCopyDao.findById(10L))
                .thenReturn(Optional.of(bookCopy));

        assertThrows(
                IllegalBookStateException.class,
                () -> borrowRecordService.borrowBook(request)
        );

        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(bookCopyDao).findById(10L);
        verifyNoInteractions(borrowRecordMapper);
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
        Mockito.verify(borrowRecordDao, Mockito.never()).save(Mockito.any(BorrowRecord.class));
    }


    @Test
    @DisplayName("Должен оформить возврат книги")
    void returnBook_shouldReturnBook_whenBorrowRecordIsActive() {
        LocalDate borrowedAt = LocalDate.of(2026, 7, 1);
        LocalDate dueDate = LocalDate.of(2026, 7, 15);
        LocalDate returnedAt = LocalDate.of(2026, 7, 10);

        ReturnBookRequestDto request = new ReturnBookRequestDto(
                50L,
                returnedAt
        );

        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");

        BookCopy bookCopy = createBookCopy(
                10L,
                book,
                "INV-001",
                BookCopyStatus.BORROWED.name()
        );

        BorrowRecord borrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                borrowedAt,
                dueDate,
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        BorrowRecord updatedBorrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                borrowedAt,
                dueDate,
                returnedAt,
                BorrowRecordStatus.RETURNED.name()
        );

        BorrowRecordResponseDto responseDto = createResponseDto(
                50L,
                1L,
                "testuser",
                10L,
                "INV-001",
                "Война и мир",
                borrowedAt,
                dueDate,
                returnedAt,
                BorrowRecordStatus.RETURNED.name()
        );

        Mockito.when(borrowRecordDao.findById(50L))
                .thenReturn(Optional.of(borrowRecord));

        Mockito.when(bookCopyDao.update(bookCopy))
                .thenReturn(bookCopy);

        Mockito.when(borrowRecordDao.update(borrowRecord))
                .thenReturn(updatedBorrowRecord);

        Mockito.when(borrowRecordMapper.toResponseDto(updatedBorrowRecord))
                .thenReturn(responseDto);

        BorrowRecordResponseDto result = borrowRecordService.returnBook(request);

        assertEquals(50L, result.id());
        assertEquals(returnedAt, result.returnedAt());
        assertEquals(BorrowRecordStatus.RETURNED.name(), result.status());

        assertEquals(returnedAt, borrowRecord.getReturnedAt());
        assertEquals(BorrowRecordStatus.RETURNED.name(), borrowRecord.getStatus());
        assertEquals(BookCopyStatus.AVAILABLE.name(), bookCopy.getStatus());

        Mockito.verify(borrowRecordDao).findById(50L);
        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(bookCopyDao).update(bookCopy);
        Mockito.verify(borrowRecordDao).update(borrowRecord);
        Mockito.verify(borrowRecordMapper).toResponseDto(updatedBorrowRecord);
    }


    @Test
    @DisplayName("Должен выбросить EntityNotFoundException при возврате, если запись аренды не найдена")
    void returnBook_shouldThrowException_whenBorrowRecordNotFound() {
        ReturnBookRequestDto request = new ReturnBookRequestDto(
                99L,
                LocalDate.of(2026, 7, 10)
        );

        Mockito.when(borrowRecordDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> borrowRecordService.returnBook(request)
        );

        Mockito.verify(borrowRecordDao).findById(99L);
        verifyNoInteractions(accessControlService);
        verifyNoInteractions(borrowRecordMapper);
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
    }


    @Test
    @DisplayName("Должен выбросить IllegalBookStateException при повторном возврате")
    void returnBook_shouldThrowException_whenBookAlreadyReturned() {
        LocalDate borrowedAt = LocalDate.of(2026, 7, 1);
        LocalDate returnedAt = LocalDate.of(2026, 7, 10);

        ReturnBookRequestDto request = new ReturnBookRequestDto(
                50L,
                returnedAt
        );

        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.AVAILABLE.name());

        BorrowRecord borrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                borrowedAt,
                LocalDate.of(2026, 7, 15),
                returnedAt,
                BorrowRecordStatus.RETURNED.name()
        );

        Mockito.when(borrowRecordDao.findById(50L))
                .thenReturn(Optional.of(borrowRecord));

        assertThrows(
                IllegalBookStateException.class,
                () -> borrowRecordService.returnBook(request)
        );

        Mockito.verify(borrowRecordDao).findById(50L);
        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
        Mockito.verify(borrowRecordDao, Mockito.never()).update(Mockito.any(BorrowRecord.class));
        verifyNoInteractions(borrowRecordMapper);
    }


    @Test
    @DisplayName("Должен выбросить IllegalBookStateException, если дата возврата раньше даты выдачи")
    void returnBook_shouldThrowException_whenReturnDateBeforeBorrowDate() {
        LocalDate borrowedAt = LocalDate.of(2026, 7, 10);
        LocalDate returnedAt = LocalDate.of(2026, 7, 1);

        ReturnBookRequestDto request = new ReturnBookRequestDto(
                50L,
                returnedAt
        );

        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.BORROWED.name());

        BorrowRecord borrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                borrowedAt,
                LocalDate.of(2026, 7, 20),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        Mockito.when(borrowRecordDao.findById(50L))
                .thenReturn(Optional.of(borrowRecord));

        assertThrows(
                IllegalBookStateException.class,
                () -> borrowRecordService.returnBook(request)
        );

        Mockito.verify(borrowRecordDao).findById(50L);
        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(bookCopyDao, Mockito.never()).update(Mockito.any(BookCopy.class));
        Mockito.verify(borrowRecordDao, Mockito.never()).update(Mockito.any(BorrowRecord.class));
        verifyNoInteractions(borrowRecordMapper);
    }


    @Test
    @DisplayName("Должен продлить срок аренды")
    void extendBorrowPeriod_shouldExtendBorrowPeriod_whenNewDueDateIsValid() {
        LocalDate borrowedAt = LocalDate.of(2026, 7, 1);
        LocalDate oldDueDate = LocalDate.of(2026, 7, 15);
        LocalDate newDueDate = LocalDate.of(2026, 7, 25);

        ExtendBorrowRequestDto request = new ExtendBorrowRequestDto(
                newDueDate
        );

        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.BORROWED.name());

        BorrowRecord borrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                borrowedAt,
                oldDueDate,
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        BorrowRecord updatedBorrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                borrowedAt,
                newDueDate,
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        BorrowRecordResponseDto responseDto = createResponseDto(
                50L,
                1L,
                "testuser",
                10L,
                "INV-001",
                "Война и мир",
                borrowedAt,
                newDueDate,
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        Mockito.when(borrowRecordDao.findById(50L))
                .thenReturn(Optional.of(borrowRecord));

        Mockito.when(borrowRecordDao.update(borrowRecord))
                .thenReturn(updatedBorrowRecord);

        Mockito.when(borrowRecordMapper.toResponseDto(updatedBorrowRecord))
                .thenReturn(responseDto);

        BorrowRecordResponseDto result = borrowRecordService.extendBorrowPeriod(50L, request);

        assertEquals(50L, result.id());
        assertEquals(newDueDate, result.dueDate());

        assertEquals(newDueDate, borrowRecord.getDueDate());

        Mockito.verify(borrowRecordDao).findById(50L);
        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(borrowRecordDao).update(borrowRecord);
        Mockito.verify(borrowRecordMapper).toResponseDto(updatedBorrowRecord);
    }


    @Test
    @DisplayName("Должен выбросить IllegalBookStateException при продлении уже возвращённой книги")
    void extendBorrowPeriod_shouldThrowException_whenBookAlreadyReturned() {
        ExtendBorrowRequestDto request = new ExtendBorrowRequestDto(
                LocalDate.of(2026, 7, 25)
        );

        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.AVAILABLE.name());

        BorrowRecord borrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                LocalDate.of(2026, 7, 10),
                BorrowRecordStatus.RETURNED.name()
        );

        Mockito.when(borrowRecordDao.findById(50L))
                .thenReturn(Optional.of(borrowRecord));

        assertThrows(
                IllegalBookStateException.class,
                () -> borrowRecordService.extendBorrowPeriod(50L, request)
        );

        Mockito.verify(borrowRecordDao).findById(50L);
        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(borrowRecordDao, Mockito.never()).update(Mockito.any(BorrowRecord.class));
        verifyNoInteractions(borrowRecordMapper);
    }


    @Test
    @DisplayName("Должен выбросить IllegalBookStateException, если новая дата возврата раньше текущей")
    void extendBorrowPeriod_shouldThrowException_whenNewDueDateBeforeCurrentDueDate() {
        ExtendBorrowRequestDto request = new ExtendBorrowRequestDto(
                LocalDate.of(2026, 7, 10)
        );

        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.BORROWED.name());

        BorrowRecord borrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        Mockito.when(borrowRecordDao.findById(50L))
                .thenReturn(Optional.of(borrowRecord));

        assertThrows(
                IllegalBookStateException.class,
                () -> borrowRecordService.extendBorrowPeriod(50L, request)
        );

        Mockito.verify(borrowRecordDao).findById(50L);
        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(borrowRecordDao, Mockito.never()).update(Mockito.any(BorrowRecord.class));
        verifyNoInteractions(borrowRecordMapper);
    }


    @Test
    @DisplayName("Должен вернуть запись аренды по ID")
    void getById_shouldReturnBorrowRecord_whenBorrowRecordExists() {
        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.BORROWED.name());

        BorrowRecord borrowRecord = createBorrowRecord(
                50L,
                user,
                bookCopy,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        BorrowRecordResponseDto responseDto = createResponseDto(
                50L,
                1L,
                "testuser",
                10L,
                "INV-001",
                "Война и мир",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        Mockito.when(borrowRecordDao.findById(50L))
                .thenReturn(Optional.of(borrowRecord));

        Mockito.when(borrowRecordMapper.toResponseDto(borrowRecord))
                .thenReturn(responseDto);

        BorrowRecordResponseDto result = borrowRecordService.getById(50L);

        assertEquals(50L, result.id());
        assertEquals(1L, result.userId());
        assertEquals(10L, result.bookCopyId());
        assertEquals(BorrowRecordStatus.BORROWED.name(), result.status());

        Mockito.verify(borrowRecordDao).findById(50L);
        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(borrowRecordMapper).toResponseDto(borrowRecord);
    }


    @Test
    @DisplayName("Должен выбросить EntityNotFoundException, если запись аренды по ID не найдена")
    void getById_shouldThrowException_whenBorrowRecordNotFound() {
        Mockito.when(borrowRecordDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> borrowRecordService.getById(99L)
        );

        Mockito.verify(borrowRecordDao).findById(99L);
        verifyNoInteractions(accessControlService);
        verifyNoInteractions(borrowRecordMapper);
    }


    @Test
    @DisplayName("Должен вернуть список всех записей аренды")
    void getAll_shouldReturnAllBorrowRecords() {
        User user = createUser(1L, "testuser");
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.BORROWED.name());

        BorrowRecord borrowRecord1 = createBorrowRecord(
                1L,
                user,
                bookCopy,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        BorrowRecord borrowRecord2 = createBorrowRecord(
                2L,
                user,
                bookCopy,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 15),
                LocalDate.of(2026, 6, 10),
                BorrowRecordStatus.RETURNED.name()
        );

        List<BorrowRecord> borrowRecords = List.of(borrowRecord1, borrowRecord2);

        List<BorrowRecordResponseDto> responseDtos = List.of(
                createResponseDto(1L, 1L, "testuser", 10L, "INV-001", "Война и мир",
                        LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15), null, BorrowRecordStatus.BORROWED.name()),
                createResponseDto(2L, 1L, "testuser", 10L, "INV-001", "Война и мир",
                        LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 10), BorrowRecordStatus.RETURNED.name())
        );

        Mockito.when(borrowRecordDao.findAll())
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());

        Mockito.verify(borrowRecordDao).findAll();
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    @Test
    @DisplayName("Должен вернуть текущие аренды")
    void getCurrentBorrows_shouldReturnCurrentBorrows() {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);
        borrowRecord.setStatus(BorrowRecordStatus.BORROWED.name());

        List<BorrowRecord> borrowRecords = List.of(borrowRecord);

        BorrowRecordResponseDto responseDto = createResponseDto(
                1L,
                1L,
                "testuser",
                10L,
                "INV-001",
                "Война и мир",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        List<BorrowRecordResponseDto> responseDtos = List.of(responseDto);

        Mockito.when(borrowRecordDao.findByStatus(BorrowRecordStatus.BORROWED.name()))
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getCurrentBorrows();

        assertEquals(1, result.size());
        assertEquals(BorrowRecordStatus.BORROWED.name(), result.get(0).status());

        Mockito.verify(borrowRecordDao).findByStatus(BorrowRecordStatus.BORROWED.name());
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    @Test
    @DisplayName("Должен вернуть просроченные аренды")
    void getOverdueBorrows_shouldReturnOverdueBorrows() {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);

        List<BorrowRecord> borrowRecords = List.of(borrowRecord);

        BorrowRecordResponseDto responseDto = createResponseDto(
                1L,
                1L,
                "testuser",
                10L,
                "INV-001",
                "Война и мир",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 15),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        List<BorrowRecordResponseDto> responseDtos = List.of(responseDto);

        Mockito.when(borrowRecordDao.findOverdueRecords(Mockito.any(LocalDate.class)))
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getOverdueBorrows();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());

        Mockito.verify(borrowRecordDao).findOverdueRecords(Mockito.any(LocalDate.class));
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    @Test
    @DisplayName("Должен вернуть записи аренды пользователя")
    void getByUserId_shouldReturnBorrowRecords_whenUserExists() {
        User user = createUser(1L, "testuser");

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);
        borrowRecord.setUser(user);

        List<BorrowRecord> borrowRecords = List.of(borrowRecord);

        BorrowRecordResponseDto responseDto = createResponseDto(
                1L,
                1L,
                "testuser",
                10L,
                "INV-001",
                "Война и мир",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                null,
                BorrowRecordStatus.BORROWED.name()
        );

        List<BorrowRecordResponseDto> responseDtos = List.of(responseDto);

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(borrowRecordDao.findByUserId(1L))
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).userId());

        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(borrowRecordDao).findByUserId(1L);
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    @Test
    @DisplayName("Должен выбросить EntityNotFoundException при получении записей пользователя, если пользователь не найден")
    void getByUserId_shouldThrowException_whenUserNotFound() {
        Mockito.when(userDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> borrowRecordService.getByUserId(99L)
        );

        Mockito.verify(accessControlService).checkUserAccess(99L);
        Mockito.verify(userDao).findById(99L);
        Mockito.verify(borrowRecordDao, Mockito.never()).findByUserId(99L);
        verifyNoInteractions(borrowRecordMapper);
    }


    @Test
    @DisplayName("Должен вернуть текущие аренды пользователя")
    void getCurrentBorrowsByUserId_shouldReturnCurrentBorrows_whenUserExists() {
        User user = createUser(1L, "testuser");

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);
        borrowRecord.setUser(user);
        borrowRecord.setStatus(BorrowRecordStatus.BORROWED.name());

        List<BorrowRecord> borrowRecords = List.of(borrowRecord);

        List<BorrowRecordResponseDto> responseDtos = List.of(
                createResponseDto(1L, 1L, "testuser", 10L, "INV-001", "Война и мир",
                        LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15), null, BorrowRecordStatus.BORROWED.name())
        );

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(borrowRecordDao.findActiveByUserId(1L))
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getCurrentBorrowsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(BorrowRecordStatus.BORROWED.name(), result.get(0).status());

        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(borrowRecordDao).findActiveByUserId(1L);
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    @Test
    @DisplayName("Должен вернуть историю аренды пользователя")
    void getBorrowHistoryByUserId_shouldReturnBorrowHistory_whenUserExists() {
        User user = createUser(1L, "testuser");

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);
        borrowRecord.setUser(user);

        List<BorrowRecord> borrowRecords = List.of(borrowRecord);

        List<BorrowRecordResponseDto> responseDtos = List.of(
                createResponseDto(1L, 1L, "testuser", 10L, "INV-001", "Война и мир",
                        LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15), null, BorrowRecordStatus.BORROWED.name())
        );

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(borrowRecordDao.findByUserId(1L))
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getBorrowHistoryByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).userId());

        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(borrowRecordDao).findByUserId(1L);
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    @Test
    @DisplayName("Должен вернуть просроченные аренды пользователя")
    void getOverdueBorrowsByUserId_shouldReturnOverdueBorrows_whenUserExists() {
        User user1 = createUser(1L, "testuser");
        User user2 = createUser(2L, "otheruser");

        BorrowRecord borrowRecord1 = new BorrowRecord();
        borrowRecord1.setId(1L);
        borrowRecord1.setUser(user1);

        BorrowRecord borrowRecord2 = new BorrowRecord();
        borrowRecord2.setId(2L);
        borrowRecord2.setUser(user2);

        List<BorrowRecord> overdueRecords = List.of(borrowRecord1, borrowRecord2);
        List<BorrowRecord> filteredRecords = List.of(borrowRecord1);

        List<BorrowRecordResponseDto> responseDtos = List.of(
                createResponseDto(1L, 1L, "testuser", 10L, "INV-001", "Война и мир",
                        LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 15), null, BorrowRecordStatus.BORROWED.name())
        );

        Mockito.when(userDao.findById(1L))
                .thenReturn(Optional.of(user1));

        Mockito.when(borrowRecordDao.findOverdueRecords(Mockito.any(LocalDate.class)))
                .thenReturn(overdueRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(filteredRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getOverdueBorrowsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).userId());

        Mockito.verify(accessControlService).checkUserAccess(1L);
        Mockito.verify(userDao).findById(1L);
        Mockito.verify(borrowRecordDao).findOverdueRecords(Mockito.any(LocalDate.class));
        Mockito.verify(borrowRecordMapper).toResponseDtoList(filteredRecords);
    }


    @Test
    @DisplayName("Должен вернуть записи аренды по ID экземпляра книги")
    void getByBookCopyId_shouldReturnBorrowRecords_whenBookCopyExists() {
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.BORROWED.name());

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);
        borrowRecord.setBookCopy(bookCopy);

        List<BorrowRecord> borrowRecords = List.of(borrowRecord);

        List<BorrowRecordResponseDto> responseDtos = List.of(
                createResponseDto(1L, 1L, "testuser", 10L, "INV-001", "Война и мир",
                        LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15), null, BorrowRecordStatus.BORROWED.name())
        );

        Mockito.when(bookCopyDao.findById(10L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(borrowRecordDao.findByBookCopyId(10L))
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getByBookCopyId(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).bookCopyId());

        Mockito.verify(bookCopyDao).findById(10L);
        Mockito.verify(borrowRecordDao).findByBookCopyId(10L);
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    @Test
    @DisplayName("Должен выбросить EntityNotFoundException при получении записей аренды, если экземпляр книги не найден")
    void getByBookCopyId_shouldThrowException_whenBookCopyNotFound() {
        Mockito.when(bookCopyDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> borrowRecordService.getByBookCopyId(99L)
        );

        Mockito.verify(bookCopyDao).findById(99L);
        Mockito.verify(borrowRecordDao, Mockito.never()).findByBookCopyId(99L);
        verifyNoInteractions(borrowRecordMapper);
    }


    @Test
    @DisplayName("Должен вернуть историю аренды по ID экземпляра книги")
    void getBorrowHistoryByBookCopyId_shouldReturnBorrowHistory_whenBookCopyExists() {
        Book book = createBook(100L, "Война и мир");
        BookCopy bookCopy = createBookCopy(10L, book, "INV-001", BookCopyStatus.BORROWED.name());

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(1L);
        borrowRecord.setBookCopy(bookCopy);

        List<BorrowRecord> borrowRecords = List.of(borrowRecord);

        List<BorrowRecordResponseDto> responseDtos = List.of(
                createResponseDto(1L, 1L, "testuser", 10L, "INV-001", "Война и мир",
                        LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 15), null, BorrowRecordStatus.BORROWED.name())
        );

        Mockito.when(bookCopyDao.findById(10L))
                .thenReturn(Optional.of(bookCopy));

        Mockito.when(borrowRecordDao.findByBookCopyId(10L))
                .thenReturn(borrowRecords);

        Mockito.when(borrowRecordMapper.toResponseDtoList(borrowRecords))
                .thenReturn(responseDtos);

        List<BorrowRecordResponseDto> result = borrowRecordService.getBorrowHistoryByBookCopyId(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).bookCopyId());

        Mockito.verify(bookCopyDao).findById(10L);
        Mockito.verify(borrowRecordDao).findByBookCopyId(10L);
        Mockito.verify(borrowRecordMapper).toResponseDtoList(borrowRecords);
    }


    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Book createBook(Long id, String title) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        return book;
    }

    private BookCopy createBookCopy(Long id, Book book, String inventoryNumber, String status) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(id);
        bookCopy.setBook(book);
        bookCopy.setInventoryNumber(inventoryNumber);
        bookCopy.setStatus(status);
        return bookCopy;
    }

    private BorrowRecord createBorrowRecord(
            Long id,
            User user,
            BookCopy bookCopy,
            LocalDate borrowedAt,
            LocalDate dueDate,
            LocalDate returnedAt,
            String status
    ) {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(id);
        borrowRecord.setUser(user);
        borrowRecord.setBookCopy(bookCopy);
        borrowRecord.setBorrowedAt(borrowedAt);
        borrowRecord.setDueDate(dueDate);
        borrowRecord.setReturnedAt(returnedAt);
        borrowRecord.setStatus(status);
        return borrowRecord;
    }

    private BorrowRecordResponseDto createResponseDto(
            Long id,
            Long userId,
            String username,
            Long bookCopyId,
            String inventoryNumber,
            String bookTitle,
            LocalDate borrowedAt,
            LocalDate dueDate,
            LocalDate returnedAt,
            String status
    ) {
        return new BorrowRecordResponseDto(
                id,
                userId,
                username,
                bookCopyId,
                inventoryNumber,
                bookTitle,
                borrowedAt,
                dueDate,
                returnedAt,
                status
        );
    }
}