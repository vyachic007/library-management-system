package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.BorrowBookRequestDto;
import by.slava_borisov.library.dto.request.ExtendBorrowRequestDto;
import by.slava_borisov.library.dto.request.ReturnBookRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @PostMapping("/rent")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRecordResponseDto borrowBook(
            @Valid @RequestBody BorrowBookRequestDto request
    ) {
        return borrowRecordService.borrowBook(request);
    }

    @PostMapping("/return")
    public BorrowRecordResponseDto returnBook(
            @Valid @RequestBody ReturnBookRequestDto request
    ) {
        return borrowRecordService.returnBook(request);
    }

    @PostMapping("/{recordId}/extend")
    public BorrowRecordResponseDto extendBorrowPeriod(
            @PathVariable("recordId") Long recordId,
            @Valid @RequestBody ExtendBorrowRequestDto request
    ) {
        return borrowRecordService.extendBorrowPeriod(recordId, request);
    }

    @GetMapping
    public List<BorrowRecordResponseDto> getAllBorrowRecords() {
        return borrowRecordService.getAll();
    }

    @GetMapping("/active")
    public List<BorrowRecordResponseDto> getCurrentBorrows() {
        return borrowRecordService.getCurrentBorrows();
    }

    @GetMapping("/overdue")
    public List<BorrowRecordResponseDto> getOverdueBorrows() {
        return borrowRecordService.getOverdueBorrows();
    }

    @GetMapping("/user/{userId}")
    public List<BorrowRecordResponseDto> getBorrowRecordsByUserId(
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getByUserId(userId);
    }

    @GetMapping("/user/{userId}/current")
    public List<BorrowRecordResponseDto> getCurrentBorrowsByUserId(
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getCurrentBorrowsByUserId(userId);
    }

    @GetMapping("/user/{userId}/history")
    public List<BorrowRecordResponseDto> getBorrowHistoryByUserId(
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getBorrowHistoryByUserId(userId);
    }

    @GetMapping("/user/{userId}/overdue")
    public List<BorrowRecordResponseDto> getOverdueBorrowsByUserId(
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getOverdueBorrowsByUserId(userId);
    }

    @GetMapping("/book-copy/{copyId}")
    public List<BorrowRecordResponseDto> getBorrowRecordsByBookCopyId(
            @PathVariable("copyId") Long copyId
    ) {
        return borrowRecordService.getByBookCopyId(copyId);
    }

    @GetMapping("/book-copy/{copyId}/history")
    public List<BorrowRecordResponseDto> getBorrowHistoryByBookCopyId(
            @PathVariable("copyId") Long copyId
    ) {
        return borrowRecordService.getBorrowHistoryByBookCopyId(copyId);
    }

    @GetMapping("/{recordId}")
    public BorrowRecordResponseDto getBorrowRecordById(
            @PathVariable("recordId") Long recordId
    ) {
        return borrowRecordService.getById(recordId);
    }
}