package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.BookCopyCreateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyStatusUpdateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.service.BookCopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
@RequiredArgsConstructor
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookCopyResponseDto createBookCopy(
            @Valid @RequestBody BookCopyCreateRequestDto request
    ) {
        return bookCopyService.create(request);
    }

    @GetMapping
    public List<BookCopyResponseDto> getAllBookCopies() {
        return bookCopyService.getAll();
    }

    @GetMapping("/status/{status}")
    public List<BookCopyResponseDto> getBookCopiesByStatus(
            @PathVariable String status
    ) {
        return bookCopyService.getByStatus(status);
    }

    @GetMapping("/inventory/{inventoryNumber}")
    public BookCopyResponseDto getBookCopyByInventoryNumber(
            @PathVariable String inventoryNumber
    ) {
        return bookCopyService.getByInventoryNumber(inventoryNumber);
    }

    @GetMapping("/book/{bookId}")
    public List<BookCopyResponseDto> getBookCopiesByBookId(
            @PathVariable Long bookId
    ) {
        return bookCopyService.getByBookId(bookId);
    }

    @GetMapping("/book/{bookId}/available")
    public List<BookCopyResponseDto> getAvailableBookCopiesByBookId(
            @PathVariable Long bookId
    ) {
        return bookCopyService.getAvailableByBookId(bookId);
    }

    @GetMapping("/{copyId}")
    public BookCopyResponseDto getBookCopyById(
            @PathVariable Long copyId
    ) {
        return bookCopyService.getById(copyId);
    }

    @PutMapping("/{copyId}")
    public BookCopyResponseDto updateBookCopy(
            @PathVariable Long copyId,
            @Valid @RequestBody BookCopyUpdateRequestDto request
    ) {
        return bookCopyService.update(copyId, request);
    }

    @PatchMapping("/{copyId}/status")
    public BookCopyResponseDto changeBookCopyStatus(
            @PathVariable Long copyId,
            @Valid @RequestBody BookCopyStatusUpdateRequestDto request
    ) {
        return bookCopyService.changeStatus(copyId, request);
    }

    @DeleteMapping("/{copyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCopyById(
            @PathVariable Long copyId
    ) {
        bookCopyService.delete(copyId);
    }
}