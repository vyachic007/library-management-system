package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.BookCopyDao;
import by.slava_borisov.library.dao.BookDao;
import by.slava_borisov.library.dto.request.BookCopyCreateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyStatusUpdateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.mapper.BookCopyMapper;
import by.slava_borisov.library.model.Book;
import by.slava_borisov.library.model.BookCopy;
import by.slava_borisov.library.service.BookCopyService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl implements BookCopyService {

    private final BookCopyDao bookCopyDao;
    private final BookDao bookDao;
    private final BookCopyMapper bookCopyMapper;

    @Override
    @Transactional
    public BookCopyResponseDto create(BookCopyCreateRequestDto requestDto) {
        if (bookCopyDao.existsByInventoryNumber(requestDto.inventoryNumber())) {
            throw new IllegalArgumentException(Messages.BOOK_COPY_ALREADY_EXISTS_BY_INVENTORY_NUMBER);
        }

        Book book = getBookEntityById(requestDto.bookId());

        BookCopy bookCopy = bookCopyMapper.toEntity(requestDto);
        bookCopy.setBook(book);

        BookCopy savedBookCopy = bookCopyDao.save(bookCopy);
        return bookCopyMapper.toResponseDto(savedBookCopy);
    }

    @Override
    @Transactional
    public BookCopyResponseDto update(Long bookCopyId, BookCopyUpdateRequestDto requestDto) {
        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);

        bookCopyDao.findByInventoryNumber(requestDto.inventoryNumber())
                .filter(existingBookCopy -> !existingBookCopy.getId().equals(bookCopyId))
                .ifPresent(existingBookCopy -> {
                    throw new IllegalArgumentException(Messages.BOOK_COPY_ALREADY_EXISTS_BY_INVENTORY_NUMBER);
                });

        Book book = getBookEntityById(requestDto.bookId());

        bookCopyMapper.updateEntityFromDto(requestDto, bookCopy);
        bookCopy.setBook(book);

        BookCopy updatedBookCopy = bookCopyDao.update(bookCopy);
        return bookCopyMapper.toResponseDto(updatedBookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public BookCopyResponseDto getById(Long bookCopyId) {
        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);
        return bookCopyMapper.toResponseDto(bookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getAll() {
        return bookCopyMapper.toResponseDtoList(bookCopyDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getByBookId(Long bookId) {
        getBookEntityById(bookId);
        return bookCopyMapper.toResponseDtoList(bookCopyDao.findByBookId(bookId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getByStatus(String status) {
        return bookCopyMapper.toResponseDtoList(bookCopyDao.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public BookCopyResponseDto getByInventoryNumber(String inventoryNumber) {
        BookCopy bookCopy = bookCopyDao.findByInventoryNumber(inventoryNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.BOOK_COPY_NOT_FOUND_BY_INVENTORY_NUMBER.formatted(inventoryNumber)
                ));

        return bookCopyMapper.toResponseDto(bookCopy);
    }

    @Override
    @Transactional
    public BookCopyResponseDto changeStatus(Long bookCopyId, BookCopyStatusUpdateRequestDto requestDto) {
        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);
        bookCopy.setStatus(requestDto.status());

        BookCopy updatedBookCopy = bookCopyDao.update(bookCopy);
        return bookCopyMapper.toResponseDto(updatedBookCopy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyResponseDto> getAvailableByBookId(Long bookId) {
        getBookEntityById(bookId);
        return bookCopyMapper.toResponseDtoList(bookCopyDao.findAvailableByBookId(bookId));
    }

    @Override
    @Transactional
    public void delete(Long bookCopyId) {
        BookCopy bookCopy = getBookCopyEntityById(bookCopyId);
        bookCopyDao.delete(bookCopy);
    }

    private BookCopy getBookCopyEntityById(Long bookCopyId) {
        return bookCopyDao.findById(bookCopyId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.BOOK_COPY_NOT_FOUND_BY_ID.formatted(bookCopyId)
                ));
    }

    private Book getBookEntityById(Long bookId) {
        return bookDao.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.BOOK_NOT_FOUND_BY_ID.formatted(bookId)
                ));
    }
}