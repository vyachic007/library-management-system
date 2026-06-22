package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Подробная информация о книге")
public record BookDetailsResponseDto(

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
                description = "Описание книги",
                example = "Практическое руководство по написанию чистого и поддерживаемого кода",
                nullable = true
        )
        String description,

        @Schema(
                description = "Год публикации книги",
                example = "2008",
                nullable = true
        )
        Integer publicationYear,

        @Schema(
                description = "Категория книги"
        )
        CategoryResponseDto category,

        @Schema(
                description = "Список авторов книги"
        )
        List<AuthorResponseDto> authors
) {
}