package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Данные для добавления новой книги")
public record BookCreateRequestDto(

        @Schema(
                description = "Название книги",
                example = "Clean Code",
                maxLength = 255
        )
        @NotBlank(message = "Название книги не должно быть пустым")
        @Size(max = 255, message = "Название книги не должно превышать 255 символов")
        String title,

        @Schema(
                description = "Международный стандартный номер книги",
                example = "9780132350884",
                maxLength = 50
        )
        @NotBlank(message = "ISBN не должен быть пустым")
        @Size(max = 50, message = "ISBN не должен превышать 50 символов")
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
                minimum = "0",
                maximum = "2100",
                nullable = true
        )
        @Min(value = 0, message = "Год публикации не может быть отрицательным")
        @Max(value = 2100, message = "Год публикации указан некорректно")
        Integer publicationYear,

        @Schema(
                description = "Идентификатор категории книги",
                example = "1"
        )
        @NotNull(message = "Категория книги обязательна")
        Long categoryId,

        @Schema(
                description = "Идентификаторы авторов книги",
                example = "[1, 2]"
        )
        @NotEmpty(message = "У книги должен быть хотя бы один автор")
        Set<Long> authorIds
) {
}