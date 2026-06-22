package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Данные для продления срока выдачи книги")
public record ExtendBorrowRequestDto(

        @Schema(
                description = "Новая плановая дата возврата книги",
                example = "2026-07-20",
                type = "string",
                format = "date"
        )
        @NotNull(message = "Новая дата возврата обязательна")
        @Future(message = "Новая дата возврата должна быть в будущем")
        LocalDate newDueDate
) {
}