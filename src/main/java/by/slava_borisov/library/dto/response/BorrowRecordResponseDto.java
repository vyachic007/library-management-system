package by.slava_borisov.library.dto.response;

import java.time.LocalDate;

public record BorrowRecordResponseDto(
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
}