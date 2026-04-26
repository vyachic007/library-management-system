package by.slava_borisov.library.dto.response;

public record BookCopyResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        String inventoryNumber,
        String status,
        String conditionDescription
) {
}