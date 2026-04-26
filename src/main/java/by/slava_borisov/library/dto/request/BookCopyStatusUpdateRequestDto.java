package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookCopyStatusUpdateRequestDto(

        @NotBlank(message = "Статус экземпляра книги обязателен")
        @Size(max = 30, message = "Статус не должен превышать 30 символов")
        String status
) {
}