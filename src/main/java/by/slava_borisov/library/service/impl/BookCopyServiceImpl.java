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
import by.slava_borisov.library.service.BookCopyService;
import by.slava_borisov.library.util.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyDao bookCopyDao;
    private final BookDao bookDao;
    private final BookCopyMapper bookCopyMapper;

    @Override
    @Transactional
    public BookCopyResponseDto create(BookCopyCreateRequestDto requestDto) {
        log.info("Создание экземпляра книги: bookId={}, inventoryNumber={}, status={}",
                requestDto.bookId(), requestDto.inventoryNumber(), requestDto.status());

        if (bookCopyDao.existsByInventoryNumber(requestDto.inventoryNumber())) {
            log.warn("Попытка создать экземпляр книги с уже существующим инвентарным номером: inventoryNumber={}",
                    requestDto.inventoryNumber());
            throw new DuplicateException(Messages.BOOK_COPY_ALREADY_EXISTS_BY_INVENTORY_NUMBER);
        }

        Book book = getBookEntityById(requestDto.bookId());

        BookCopy bookCopy = bookCopyMapper.toEntity(requestDto);
        bookCopy.setBook(book);

        BookCopy savedBookCopy = bookCopyDao.save(bookCopy);

        log.info("Экземпляр книги успешно создан: id={}, bookId={}, inventoryNumber={}, status={}",
                savedBookCopy.getId(),
                savedBookCopy.getBook().getId(),
                savedBookCopy.getInventoryNumber(),
                savedBookCopy.getStatus());

        return bookCopyMapper.toResponseDto(savedBookCopy);
    }

    @Override
    @Transactional
    public BookCopyResponseDto update(Long bookCopyId, BookCopyUpdateRequestDto requestDto) {
        log.info("Обновление экземпляра книги: id={}, bookId={}, inventoryNumber={}, status={}",
                bookCopyId, requestDto.bookId(), requestDto.inventoryNumber(), requestDto.status());

        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);

        bookCopyDao.findByInventoryNumber(requestDto.inventoryNumber())
                .filter(existingBookCopy -> !existingBookCopy.getId().equals(bookCopyId))
                .ifPresent(existingBookCopy -> {
                    log.warn("Попытка обновить экземпляр книги на уже существующий инвентарный номер: id={}, inventoryNumber={}",
                            bookCopyId, requestDto.inventoryNumber());
                    throw new DuplicateException(Messages.BOOK_COPY_ALREADY_EXISTS_BY_INVENTORY_NUMBER);
                });

        Book book = getBookEntityById(requestDto.bookId());

        bookCopyMapper.updateEntityFromDto(requestDto, bookCopy);
        bookCopy.setBook(book);

        BookCopy updatedBookCopy = bookCopyDao.update(bookCopy);

        log.info("Экземпляр книги успешно обновлён: id={}, bookId={}, inventoryNumber={}, status={}",
                updatedBookCopy.getId(),
                updatedBookCopy.getBook().getId(),
                updatedBookCopy.getInventoryNumber(),
                updatedBookCopy.getStatus());

        return bookCopyMapper.toResponseDto(updatedBookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public BookCopyResponseDto getById(Long bookCopyId) {
        log.info("Получение экземпляра книги по id={}", bookCopyId);

        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);

        log.info("Экземпляр книги найден: id={}, bookId={}, inventoryNumber={}, status={}",
                bookCopy.getId(),
                bookCopy.getBook().getId(),
                bookCopy.getInventoryNumber(),
                bookCopy.getStatus());

        return bookCopyMapper.toResponseDto(bookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getAll() {
        log.info("Получение списка всех экземпляров книг");

        List<BookCopy> bookCopies = bookCopyDao.findAll();

        log.info("Получен список экземпляров книг, количество={}", bookCopies.size());

        return bookCopyMapper.toResponseDtoList(bookCopies);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getByBookId(Long bookId) {
        log.info("Получение экземпляров книги по bookId={}", bookId);

        getBookEntityById(bookId);
        List<BookCopy> bookCopies = bookCopyDao.findByBookId(bookId);

        log.info("Получены экземпляры книги: bookId={}, количество={}", bookId, bookCopies.size());

        return bookCopyMapper.toResponseDtoList(bookCopies);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getByStatus(String status) {
        log.info("Получение экземпляров книг по статусу={}", status);

        List<BookCopy> bookCopies = bookCopyDao.findByStatus(status);

        log.info("Получены экземпляры книг по статусу: status={}, количество={}", status, bookCopies.size());

        return bookCopyMapper.toResponseDtoList(bookCopies);
    }

    @Override
    @Transactional(readOnly = true)
    public BookCopyResponseDto getByInventoryNumber(String inventoryNumber) {
        log.info("Получение экземпляра книги по инвентарному номеру={}", inventoryNumber);

        BookCopy bookCopy = bookCopyDao.findByInventoryNumber(inventoryNumber)
                .orElseThrow(() -> {
                    log.warn("Экземпляр книги не найден по инвентарному номеру={}", inventoryNumber);
                    return new NotFoundException(
                            Messages.BOOK_COPY_NOT_FOUND_BY_INVENTORY_NUMBER.formatted(inventoryNumber)
                    );
                });

        log.info("Экземпляр книги найден по инвентарному номеру: id={}, inventoryNumber={}, status={}",
                bookCopy.getId(), bookCopy.getInventoryNumber(), bookCopy.getStatus());

        return bookCopyMapper.toResponseDto(bookCopy);
    }

    @Override
    @Transactional
    public BookCopyResponseDto changeStatus(Long bookCopyId, BookCopyStatusUpdateRequestDto requestDto) {
        log.info("Изменение статуса экземпляра книги: id={}, новый статус={}",
                bookCopyId, requestDto.status());

        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);
        String oldStatus = bookCopy.getStatus();

        bookCopy.setStatus(requestDto.status());

        BookCopy updatedBookCopy = bookCopyDao.update(bookCopy);

        log.info("Статус экземпляра книги успешно изменён: id={}, старый статус={}, новый статус={}",
                updatedBookCopy.getId(), oldStatus, updatedBookCopy.getStatus());

        return bookCopyMapper.toResponseDto(updatedBookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getAvailableByBookId(Long bookId) {
        log.info("Получение доступных экземпляров книги: bookId={}", bookId);

        getBookEntityById(bookId);
        List<BookCopy> bookCopies = bookCopyDao.findAvailableByBookId(bookId);

        log.info("Получены доступные экземпляры книги: bookId={}, количество={}", bookId, bookCopies.size());

        return bookCopyMapper.toResponseDtoList(bookCopies);
    }

    @Override
    @Transactional
    public void delete(Long bookCopyId) {
        log.info("Удаление экземпляра книги: id={}", bookCopyId);

        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);
        bookCopyDao.delete(bookCopy);

        log.info("Экземпляр книги успешно удалён: id={}, inventoryNumber={}",
                bookCopy.getId(), bookCopy.getInventoryNumber());
    }

    private BookCopy getBookCopyEntityById(Long bookCopyId) {
        return bookCopyDao.findById(bookCopyId)
                .orElseThrow(() -> {
                    log.warn("Экземпляр книги не найден: id={}", bookCopyId);
                    return new NotFoundException(
                            Messages.BOOK_COPY_NOT_FOUND_BY_ID.formatted(bookCopyId)
                    );
                });
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
}