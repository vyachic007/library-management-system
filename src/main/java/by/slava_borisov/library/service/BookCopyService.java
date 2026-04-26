package by.slava_borisov.library.service;

import by.slava_borisov.library.dto.request.BookCopyCreateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyStatusUpdateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;

import java.util.List;

public interface BookCopyService {

    BookCopyResponseDto create(BookCopyCreateRequestDto requestDto);

    BookCopyResponseDto update(Long bookCopyId, BookCopyUpdateRequestDto requestDto);

    BookCopyResponseDto getById(Long bookCopyId);

    List<BookCopyResponseDto> getAll();

    List<BookCopyResponseDto> getByBookId(Long bookId);

    List<BookCopyResponseDto> getByStatus(String status);

    BookCopyResponseDto getByInventoryNumber(String inventoryNumber);

    BookCopyResponseDto changeStatus(Long bookCopyId, BookCopyStatusUpdateRequestDto requestDto);

    List<BookCopyResponseDto> getAvailableByBookId(Long bookId);

    void delete(Long bookCopyId);
}