package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.BookCopyDao;
import by.slava_borisov.library.dao.BorrowRecordDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.request.BorrowBookRequestDto;
import by.slava_borisov.library.dto.request.ExtendBorrowRequestDto;
import by.slava_borisov.library.dto.request.ReturnBookRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.mapper.BorrowRecordMapper;
import by.slava_borisov.library.model.BookCopy;
import by.slava_borisov.library.model.BorrowRecord;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.model.enums.BookCopyStatus;
import by.slava_borisov.library.model.enums.BorrowRecordStatus;
import by.slava_borisov.library.service.BorrowRecordService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRecordDao borrowRecordDao;
    private final UserDao userDao;
    private final BookCopyDao bookCopyDao;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    @Transactional
    public BorrowRecordResponseDto borrowBook(BorrowBookRequestDto requestDto) {
        User user = getUserEntityById(requestDto.userId());
        BookCopy bookCopy = getBookCopyEntityById(requestDto.bookCopyId());

        if (!BookCopyStatus.AVAILABLE.name().equals(bookCopy.getStatus())) {
            throw new IllegalArgumentException(Messages.BOOK_COPY_IS_NOT_AVAILABLE);
        }

        BorrowRecord borrowRecord = borrowRecordMapper.toEntity(requestDto);
        borrowRecord.setUser(user);
        borrowRecord.setBookCopy(bookCopy);
        borrowRecord.setStatus(BorrowRecordStatus.BORROWED.name());

        bookCopy.setStatus(BookCopyStatus.BORROWED.name());
        bookCopyDao.update(bookCopy);

        BorrowRecord savedBorrowRecord = borrowRecordDao.save(borrowRecord);
        return borrowRecordMapper.toResponseDto(savedBorrowRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponseDto returnBook(ReturnBookRequestDto requestDto) {
        BorrowRecord borrowRecord = getBorrowRecordEntityById(requestDto.borrowRecordId());

        if (borrowRecord.getReturnedAt() != null
                || BorrowRecordStatus.RETURNED.name().equals(borrowRecord.getStatus())) {
            throw new IllegalArgumentException(Messages.BORROW_RECORD_ALREADY_RETURNED);
        }

        if (requestDto.returnedAt().isBefore(borrowRecord.getBorrowedAt())) {
            throw new IllegalArgumentException(Messages.RETURN_DATE_BEFORE_BORROW_DATE);
        }

        borrowRecord.setReturnedAt(requestDto.returnedAt());
        borrowRecord.setStatus(BorrowRecordStatus.RETURNED.name());

        BookCopy bookCopy = borrowRecord.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE.name());
        bookCopyDao.update(bookCopy);

        BorrowRecord updatedBorrowRecord = borrowRecordDao.update(borrowRecord);
        return borrowRecordMapper.toResponseDto(updatedBorrowRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponseDto extendBorrowPeriod(Long borrowRecordId, ExtendBorrowRequestDto requestDto) {
        BorrowRecord borrowRecord = getBorrowRecordEntityById(borrowRecordId);

        if (borrowRecord.getReturnedAt() != null
                || BorrowRecordStatus.RETURNED.name().equals(borrowRecord.getStatus())) {
            throw new IllegalArgumentException(Messages.BORROW_RECORD_ALREADY_RETURNED);
        }

        if (requestDto.newDueDate().isBefore(borrowRecord.getDueDate())) {
            throw new IllegalArgumentException(Messages.NEW_DUE_DATE_BEFORE_CURRENT_DUE_DATE);
        }

        borrowRecord.setDueDate(requestDto.newDueDate());

        BorrowRecord updatedBorrowRecord = borrowRecordDao.update(borrowRecord);
        return borrowRecordMapper.toResponseDto(updatedBorrowRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowRecordResponseDto getById(Long borrowRecordId) {
        BorrowRecord borrowRecord = getBorrowRecordEntityById(borrowRecordId);
        return borrowRecordMapper.toResponseDto(borrowRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getAll() {
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getCurrentBorrows() {
        return borrowRecordMapper.toResponseDtoList(
                borrowRecordDao.findByStatus(BorrowRecordStatus.BORROWED.name())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getOverdueBorrows() {
        return borrowRecordMapper.toResponseDtoList(
                borrowRecordDao.findOverdueRecords(LocalDate.now())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getByUserId(Long userId) {
        getUserEntityById(userId);
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getCurrentBorrowsByUserId(Long userId) {
        getUserEntityById(userId);
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findActiveByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getBorrowHistoryByUserId(Long userId) {
        getUserEntityById(userId);
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getOverdueBorrowsByUserId(Long userId) {
        getUserEntityById(userId);

        return borrowRecordMapper.toResponseDtoList(
                borrowRecordDao.findOverdueRecords(LocalDate.now()).stream()
                        .filter(borrowRecord -> borrowRecord.getUser().getId().equals(userId))
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getByBookCopyId(Long bookCopyId) {
        getBookCopyEntityById(bookCopyId);
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findByBookCopyId(bookCopyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getBorrowHistoryByBookCopyId(Long bookCopyId) {
        getBookCopyEntityById(bookCopyId);
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findByBookCopyId(bookCopyId));
    }

    private BorrowRecord getBorrowRecordEntityById(Long borrowRecordId) {
        return borrowRecordDao.findById(borrowRecordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.BORROW_RECORD_NOT_FOUND_BY_ID.formatted(borrowRecordId)
                ));
    }

    private User getUserEntityById(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.USER_NOT_FOUND_BY_ID.formatted(userId)
                ));
    }

    private BookCopy getBookCopyEntityById(Long bookCopyId) {
        return bookCopyDao.findById(bookCopyId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.BOOK_COPY_NOT_FOUND_BY_ID.formatted(bookCopyId)
                ));
    }
}