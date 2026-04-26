package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExtendBorrowRequestDto(

        @NotNull(message = "Новая дата возврата обязательна")
        @Future(message = "Новая дата возврата должна быть в будущем")
        LocalDate newDueDate
) {
}