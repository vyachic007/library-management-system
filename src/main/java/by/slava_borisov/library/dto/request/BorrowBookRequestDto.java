package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record BorrowBookRequestDto(

        @NotNull(message = "Идентификатор пользователя обязателен")
        Long userId,

        @NotNull(message = "Идентификатор экземпляра книги обязателен")
        Long bookCopyId,

        @NotNull(message = "Дата выдачи обязательна")
        @PastOrPresent(message = "Дата выдачи не может быть в будущем")
        LocalDate borrowedAt,

        @NotNull(message = "Дата возврата обязательна")
        @Future(message = "Дата возврата должна быть в будущем")
        LocalDate dueDate
) {
}