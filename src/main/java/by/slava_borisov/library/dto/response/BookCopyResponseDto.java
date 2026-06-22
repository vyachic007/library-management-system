package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об экземпляре книги")
public record BookCopyResponseDto(

        @Schema(
                description = "Уникальный идентификатор экземпляра книги",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Идентификатор книги, к которой относится экземпляр",
                example = "1"
        )
        Long bookId,

        @Schema(
                description = "Название книги",
                example = "Clean Code"
        )
        String bookTitle,

        @Schema(
                description = "Уникальный инвентарный номер экземпляра",
                example = "INV-0001"
        )
        String inventoryNumber,

        @Schema(
                description = "Текущий статус экземпляра",
                example = "AVAILABLE",
                allowableValues = {
                        "AVAILABLE",
                        "BORROWED",
                        "LOST",
                        "DAMAGED"
                }
        )
        String status,

        @Schema(
                description = "Описание физического состояния экземпляра",
                example = "Хорошее состояние, незначительные следы использования",
                nullable = true
        )
        String conditionDescription
) {
}