package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Информация о записи выдачи книги")
public record BorrowRecordResponseDto(

        @Schema(
                description = "Уникальный идентификатор записи выдачи",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Идентификатор пользователя",
                example = "1"
        )
        Long userId,

        @Schema(
                description = "Имя пользователя",
                example = "testuser"
        )
        String username,

        @Schema(
                description = "Идентификатор экземпляра книги",
                example = "1"
        )
        Long bookCopyId,

        @Schema(
                description = "Инвентарный номер экземпляра книги",
                example = "INV-0001"
        )
        String inventoryNumber,

        @Schema(
                description = "Название выданной книги",
                example = "Clean Code"
        )
        String bookTitle,

        @Schema(
                description = "Дата выдачи книги",
                example = "2026-06-22",
                type = "string",
                format = "date"
        )
        LocalDate borrowedAt,

        @Schema(
                description = "Плановая дата возврата книги",
                example = "2026-07-06",
                type = "string",
                format = "date"
        )
        LocalDate dueDate,

        @Schema(
                description = "Фактическая дата возврата книги",
                example = "2026-07-04",
                type = "string",
                format = "date",
                nullable = true
        )
        LocalDate returnedAt,

        @Schema(
                description = "Статус записи выдачи",
                example = "ACTIVE",
                allowableValues = {
                        "ACTIVE",
                        "RETURNED",
                        "OVERDUE"
                }
        )
        String status
) {
}