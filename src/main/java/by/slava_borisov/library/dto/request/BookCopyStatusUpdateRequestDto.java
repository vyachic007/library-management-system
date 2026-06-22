package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для изменения статуса экземпляра книги")
public record BookCopyStatusUpdateRequestDto(

        @Schema(
                description = "Новый статус экземпляра книги",
                example = "AVAILABLE",
                maxLength = 30,
                allowableValues = {
                        "AVAILABLE",
                        "BORROWED",
                        "LOST",
                        "DAMAGED"
                }
        )
        @NotBlank(message = "Статус экземпляра книги обязателен")
        @Size(max = 30, message = "Статус не должен превышать 30 символов")
        String status
) {
}