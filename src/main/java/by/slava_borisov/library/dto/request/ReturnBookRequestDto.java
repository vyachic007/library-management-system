package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@Schema(description = "Данные для возврата ранее выданной книги")
public record ReturnBookRequestDto(

        @Schema(
                description = "Идентификатор записи выдачи книги",
                example = "1"
        )
        @NotNull(message = "Идентификатор записи аренды обязателен")
        Long borrowRecordId,

        @Schema(
                description = "Фактическая дата возврата книги",
                example = "2026-06-22",
                type = "string",
                format = "date"
        )
        @NotNull(message = "Дата возврата обязательна")
        @PastOrPresent(message = "Дата возврата не может быть в будущем")
        LocalDate returnedAt
) {
}