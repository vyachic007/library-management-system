package by.slava_borisov.library.service;

import by.slava_borisov.library.dto.request.BookCreateRequestDto;
import by.slava_borisov.library.dto.request.BookUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.dto.response.BookDetailsResponseDto;
import by.slava_borisov.library.dto.response.BookResponseDto;

import java.util.List;

public interface BookService {

    BookResponseDto create(BookCreateRequestDto requestDto);

    BookResponseDto update(Long bookId, BookUpdateRequestDto requestDto);

    BookResponseDto getById(Long bookId);

    BookDetailsResponseDto getDetailsById(Long bookId);

    List<BookResponseDto> getAll();

    List<BookResponseDto> getByCategoryId(Long categoryId);

    List<BookResponseDto> getByAuthorId(Long authorId);

    BookResponseDto getByIsbn(String isbn);

    List<BookResponseDto> searchByTitle(String title);

    List<BookResponseDto> searchByAuthor(String authorLastName);

    List<BookResponseDto> searchByCategory(Long categoryId);

    List<BookCopyResponseDto> getAvailableCopies(Long bookId);

    void delete(Long bookId);
}