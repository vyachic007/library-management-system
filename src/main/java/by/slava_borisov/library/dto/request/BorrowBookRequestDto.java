package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Schema(description = "Данные для выдачи экземпляра книги пользователю")
public record BorrowBookRequestDto(

        @Schema(
                description = "Идентификатор пользователя, которому выдаётся книга",
                example = "1"
        )
        @NotNull(message = "Идентификатор пользователя обязателен")
        Long userId,

        @Schema(
                description = "Идентификатор экземпляра книги",
                example = "1"
        )
        @NotNull(message = "Идентификатор экземпляра книги обязателен")
        Long bookCopyId,

        @Schema(
                description = "Дата выдачи книги",
                example = "2026-06-22",
                type = "string",
                format = "date"
        )
        @NotNull(message = "Дата выдачи обязательна")
        @PastOrPresent(message = "Дата выдачи не может быть в будущем")
        LocalDate borrowedAt,

        @Schema(
                description = "Плановая дата возврата книги",
                example = "2026-07-06",
                type = "string",
                format = "date"
        )
        @NotNull(message = "Дата возврата обязательна")
        @Future(message = "Дата возврата должна быть в будущем")
        LocalDate dueDate
) {
}