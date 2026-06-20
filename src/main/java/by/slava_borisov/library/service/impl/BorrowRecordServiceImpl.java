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
import by.slava_borisov.library.model.BookCopy;
import by.slava_borisov.library.model.BorrowRecord;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.model.enums.BookCopyStatus;
import by.slava_borisov.library.model.enums.BorrowRecordStatus;
import by.slava_borisov.library.service.AccessControlService;
import by.slava_borisov.library.service.BorrowRecordService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRecordDao borrowRecordDao;
    private final UserDao userDao;
    private final BookCopyDao bookCopyDao;
    private final BorrowRecordMapper borrowRecordMapper;
    private final AccessControlService accessControlService;

    @Override
    @Transactional
    public BorrowRecordResponseDto borrowBook(BorrowBookRequestDto requestDto) {
        log.info("Оформление аренды книги: userId={}, bookCopyId={}, borrowedAt={}, dueDate={}",
                requestDto.userId(), requestDto.bookCopyId(), requestDto.borrowedAt(), requestDto.dueDate());

        accessControlService.checkUserAccess(requestDto.userId());

        User user = getUserEntityById(requestDto.userId());
        BookCopy bookCopy = getBookCopyEntityById(requestDto.bookCopyId());

        if (!BookCopyStatus.AVAILABLE.name().equals(bookCopy.getStatus())) {
            log.warn("Попытка арендовать недоступный экземпляр книги: bookCopyId={}, текущий статус={}",
                    bookCopy.getId(), bookCopy.getStatus());
            throw new IllegalBookStateException(Messages.BOOK_COPY_IS_NOT_AVAILABLE);
        }

        BorrowRecord borrowRecord = borrowRecordMapper.toEntity(requestDto);
        borrowRecord.setUser(user);
        borrowRecord.setBookCopy(bookCopy);
        borrowRecord.setStatus(BorrowRecordStatus.BORROWED.name());

        bookCopy.setStatus(BookCopyStatus.BORROWED.name());
        bookCopyDao.update(bookCopy);

        BorrowRecord savedBorrowRecord = borrowRecordDao.save(borrowRecord);

        log.info("Аренда книги успешно оформлена: borrowRecordId={}, userId={}, bookCopyId={}, dueDate={}",
                savedBorrowRecord.getId(),
                savedBorrowRecord.getUser().getId(),
                savedBorrowRecord.getBookCopy().getId(),
                savedBorrowRecord.getDueDate());

        return borrowRecordMapper.toResponseDto(savedBorrowRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponseDto returnBook(ReturnBookRequestDto requestDto) {
        log.info("Оформление возврата книги: borrowRecordId={}, returnedAt={}",
                requestDto.borrowRecordId(), requestDto.returnedAt());

        BorrowRecord borrowRecord = getBorrowRecordEntityById(requestDto.borrowRecordId());

        accessControlService.checkUserAccess(borrowRecord.getUser().getId());

        if (borrowRecord.getReturnedAt() != null
                || BorrowRecordStatus.RETURNED.name().equals(borrowRecord.getStatus())) {
            log.warn("Попытка повторного возврата книги: borrowRecordId={}, status={}, returnedAt={}",
                    borrowRecord.getId(), borrowRecord.getStatus(), borrowRecord.getReturnedAt());
            throw new IllegalBookStateException(Messages.BORROW_RECORD_ALREADY_RETURNED);
        }

        if (requestDto.returnedAt().isBefore(borrowRecord.getBorrowedAt())) {
            log.warn("Некорректная дата возврата: borrowRecordId={}, borrowedAt={}, returnedAt={}",
                    borrowRecord.getId(), borrowRecord.getBorrowedAt(), requestDto.returnedAt());
            throw new IllegalBookStateException(Messages.RETURN_DATE_BEFORE_BORROW_DATE);
        }

        borrowRecord.setReturnedAt(requestDto.returnedAt());
        borrowRecord.setStatus(BorrowRecordStatus.RETURNED.name());

        BookCopy bookCopy = borrowRecord.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE.name());
        bookCopyDao.update(bookCopy);

        BorrowRecord updatedBorrowRecord = borrowRecordDao.update(borrowRecord);

        log.info("Возврат книги успешно оформлен: borrowRecordId={}, bookCopyId={}, returnedAt={}",
                updatedBorrowRecord.getId(),
                updatedBorrowRecord.getBookCopy().getId(),
                updatedBorrowRecord.getReturnedAt());

        return borrowRecordMapper.toResponseDto(updatedBorrowRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponseDto extendBorrowPeriod(Long borrowRecordId, ExtendBorrowRequestDto requestDto) {
        log.info("Продление срока аренды: borrowRecordId={}, newDueDate={}",
                borrowRecordId, requestDto.newDueDate());

        BorrowRecord borrowRecord = getBorrowRecordEntityById(borrowRecordId);

        accessControlService.checkUserAccess(borrowRecord.getUser().getId());

        if (borrowRecord.getReturnedAt() != null
                || BorrowRecordStatus.RETURNED.name().equals(borrowRecord.getStatus())) {
            log.warn("Попытка продлить уже возвращённую книгу: borrowRecordId={}, status={}, returnedAt={}",
                    borrowRecord.getId(), borrowRecord.getStatus(), borrowRecord.getReturnedAt());
            throw new IllegalBookStateException(Messages.BORROW_RECORD_ALREADY_RETURNED);
        }

        if (requestDto.newDueDate().isBefore(borrowRecord.getDueDate())) {
            log.warn("Некорректное продление аренды: borrowRecordId={}, oldDueDate={}, newDueDate={}",
                    borrowRecord.getId(), borrowRecord.getDueDate(), requestDto.newDueDate());
            throw new IllegalBookStateException(Messages.NEW_DUE_DATE_BEFORE_CURRENT_DUE_DATE);
        }

        borrowRecord.setDueDate(requestDto.newDueDate());

        BorrowRecord updatedBorrowRecord = borrowRecordDao.update(borrowRecord);

        log.info("Срок аренды успешно продлён: borrowRecordId={}, newDueDate={}",
                updatedBorrowRecord.getId(), updatedBorrowRecord.getDueDate());

        return borrowRecordMapper.toResponseDto(updatedBorrowRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowRecordResponseDto getById(Long borrowRecordId) {
        log.info("Получение записи аренды по id={}", borrowRecordId);

        BorrowRecord borrowRecord = getBorrowRecordEntityById(borrowRecordId);

        accessControlService.checkUserAccess(borrowRecord.getUser().getId());

        log.info("Запись аренды найдена: id={}, userId={}, bookCopyId={}, status={}",
                borrowRecord.getId(),
                borrowRecord.getUser().getId(),
                borrowRecord.getBookCopy().getId(),
                borrowRecord.getStatus());

        return borrowRecordMapper.toResponseDto(borrowRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getAll() {
        log.info("Получение списка всех записей аренды");

        List<BorrowRecord> borrowRecords = borrowRecordDao.findAll();

        log.info("Получен список всех записей аренды, количество={}", borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getCurrentBorrows() {
        log.info("Получение списка текущих аренд");

        List<BorrowRecord> borrowRecords = borrowRecordDao.findByStatus(BorrowRecordStatus.BORROWED.name());

        log.info("Получен список текущих аренд, количество={}", borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getOverdueBorrows() {
        log.info("Получение списка просроченных аренд");

        List<BorrowRecord> borrowRecords = borrowRecordDao.findOverdueRecords(LocalDate.now());

        log.info("Получен список просроченных аренд, количество={}", borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getByUserId(Long userId) {
        log.info("Получение записей аренды пользователя: userId={}", userId);

        accessControlService.checkUserAccess(userId);

        getUserEntityById(userId);
        List<BorrowRecord> borrowRecords = borrowRecordDao.findByUserId(userId);

        log.info("Получены записи аренды пользователя: userId={}, количество={}", userId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getCurrentBorrowsByUserId(Long userId) {
        log.info("Получение текущих аренд пользователя: userId={}", userId);

        accessControlService.checkUserAccess(userId);

        getUserEntityById(userId);
        List<BorrowRecord> borrowRecords = borrowRecordDao.findActiveByUserId(userId);

        log.info("Получены текущие аренды пользователя: userId={}, количество={}", userId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getBorrowHistoryByUserId(Long userId) {
        log.info("Получение истории аренды пользователя: userId={}", userId);

        accessControlService.checkUserAccess(userId);

        getUserEntityById(userId);
        List<BorrowRecord> borrowRecords = borrowRecordDao.findByUserId(userId);

        log.info("Получена история аренды пользователя: userId={}, количество={}", userId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getOverdueBorrowsByUserId(Long userId) {
        log.info("Получение просроченных аренд пользователя: userId={}", userId);

        accessControlService.checkUserAccess(userId);

        getUserEntityById(userId);

        List<BorrowRecord> borrowRecords = borrowRecordDao.findOverdueRecords(LocalDate.now()).stream()
                .filter(borrowRecord -> borrowRecord.getUser().getId().equals(userId))
                .toList();

        log.info("Получены просроченные аренды пользователя: userId={}, количество={}", userId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getByBookCopyId(Long bookCopyId) {
        log.info("Получение записей аренды экземпляра книги: bookCopyId={}", bookCopyId);

        getBookCopyEntityById(bookCopyId);
        List<BorrowRecord> borrowRecords = borrowRecordDao.findByBookCopyId(bookCopyId);

        log.info("Получены записи аренды экземпляра книги: bookCopyId={}, количество={}",
                bookCopyId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getBorrowHistoryByBookCopyId(Long bookCopyId) {
        log.info("Получение истории аренды экземпляра книги: bookCopyId={}", bookCopyId);

        getBookCopyEntityById(bookCopyId);
        List<BorrowRecord> borrowRecords = borrowRecordDao.findByBookCopyId(bookCopyId);

        log.info("Получена история аренды экземпляра книги: bookCopyId={}, количество={}",
                bookCopyId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    private BorrowRecord getBorrowRecordEntityById(Long borrowRecordId) {
        return borrowRecordDao.findById(borrowRecordId)
                .orElseThrow(() -> {
                    log.warn("Запись аренды не найдена: id={}", borrowRecordId);
                    return new EntityNotFoundException(
                            Messages.BORROW_RECORD_NOT_FOUND_BY_ID.formatted(borrowRecordId)
                    );
                });
    }

    private User getUserEntityById(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: id={}", userId);
                    return new EntityNotFoundException(
                            Messages.USER_NOT_FOUND_BY_ID.formatted(userId)
                    );
                });
    }

    private BookCopy getBookCopyEntityById(Long bookCopyId) {
        return bookCopyDao.findById(bookCopyId)
                .orElseThrow(() -> {
                    log.warn("Экземпляр книги не найден: id={}", bookCopyId);
                    return new EntityNotFoundException(
                            Messages.BOOK_COPY_NOT_FOUND_BY_ID.formatted(bookCopyId)
                    );
                });
    }
}