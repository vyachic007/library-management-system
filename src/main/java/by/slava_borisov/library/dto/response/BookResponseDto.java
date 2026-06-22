package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Краткая информация о книге")
public record BookResponseDto(

        @Schema(
                description = "Уникальный идентификатор книги",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Название книги",
                example = "Clean Code"
        )
        String title,

        @Schema(
                description = "Международный стандартный номер книги",
                example = "9780132350884"
        )
        String isbn,

        @Schema(
                description = "Год публикации книги",
                example = "2008",
                nullable = true
        )
        Integer publicationYear
) {
}