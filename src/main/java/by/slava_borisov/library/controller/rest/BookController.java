package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.BookCreateRequestDto;
import by.slava_borisov.library.dto.request.BookUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.dto.response.BookDetailsResponseDto;
import by.slava_borisov.library.dto.response.BookResponseDto;
import by.slava_borisov.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/search")
    public List<BookResponseDto> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String isbn
    ) {
        return bookService.search(title, author, categoryId, isbn);
    }

    @GetMapping("/isbn/{isbn}")
    public BookResponseDto getBookByIsbn(
            @PathVariable String isbn
    ) {
        return bookService.getByIsbn(isbn);
    }

    @GetMapping("/category/{categoryId}")
    public List<BookResponseDto> getBooksByCategoryId(
            @PathVariable Long categoryId
    ) {
        return bookService.getByCategoryId(categoryId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(
            @Valid @RequestBody BookCreateRequestDto request
    ) {
        return bookService.create(request);
    }

    @GetMapping
    public List<BookResponseDto> getAllBooks() {
        return bookService.getAll();
    }

    @GetMapping("/author/{authorId}")
    public List<BookResponseDto> getBooksByAuthorId(
            @PathVariable Long authorId
    ) {
        return bookService.getByAuthorId(authorId);
    }

    @GetMapping("/{bookId}/details")
    public BookDetailsResponseDto getBookDetailsById(
            @PathVariable Long bookId
    ) {
        return bookService.getDetailsById(bookId);
    }

    @GetMapping("/{bookId}/available-copies")
    public List<BookCopyResponseDto> getAvailableCopies(
            @PathVariable Long bookId
    ) {
        return bookService.getAvailableCopies(bookId);
    }

    @GetMapping("/{bookId}")
    public BookResponseDto getBookById(
            @PathVariable Long bookId
    ) {
        return bookService.getById(bookId);
    }

    @PutMapping("/{bookId}")
    public BookResponseDto updateBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookUpdateRequestDto request
    ) {
        return bookService.update(bookId, request);
    }

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookById(
            @PathVariable Long bookId
    ) {
        bookService.delete(bookId);
    }
}
