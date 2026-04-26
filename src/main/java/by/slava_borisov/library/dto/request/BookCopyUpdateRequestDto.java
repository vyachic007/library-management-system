package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookCopyUpdateRequestDto(

        @NotNull(message = "Идентификатор книги обязателен")
        Long bookId,

        @NotBlank(message = "Инвентарный номер не должен быть пустым")
        @Size(max = 100, message = "Инвентарный номер не должен превышать 100 символов")
        String inventoryNumber,

        @NotBlank(message = "Статус экземпляра книги обязателен")
        @Size(max = 30, message = "Статус не должен превышать 30 символов")
        String status,

        String conditionDescription
) {
}