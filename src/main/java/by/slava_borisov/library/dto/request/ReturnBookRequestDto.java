package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record ReturnBookRequestDto(

        @NotNull(message = "Идентификатор записи аренды обязателен")
        Long borrowRecordId,

        @NotNull(message = "Дата возврата обязательна")
        @PastOrPresent(message = "Дата возврата не может быть в будущем")
        LocalDate returnedAt
) {
}