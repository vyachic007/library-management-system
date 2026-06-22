package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для обновления экземпляра книги")
public record BookCopyUpdateRequestDto(

        @Schema(
                description = "Идентификатор книги",
                example = "1"
        )
        @NotNull(message = "Идентификатор книги обязателен")
        Long bookId,

        @Schema(
                description = "Инвентарный номер экземпляра",
                example = "INV-0001",
                maxLength = 100
        )
        @NotBlank(message = "Инвентарный номер не должен быть пустым")
        @Size(max = 100, message = "Инвентарный номер не должен превышать 100 символов")
        String inventoryNumber,

        @Schema(
                description = "Статус экземпляра книги",
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
        String status,

        @Schema(
                description = "Описание физического состояния экземпляра",
                example = "Хорошее состояние, небольшие следы использования",
                nullable = true
        )
        String conditionDescription
) {
}