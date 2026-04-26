package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BookCreateRequestDto(

        @NotBlank(message = "Название книги не должно быть пустым")
        @Size(max = 255, message = "Название книги не должно превышать 255 символов")
        String title,

        @NotBlank(message = "ISBN не должен быть пустым")
        @Size(max = 50, message = "ISBN не должен превышать 50 символов")
        String isbn,

        String description,

        @Min(value = 0, message = "Год публикации не может быть отрицательным")
        @Max(value = 2100, message = "Год публикации указан некорректно")
        Integer publicationYear,

        @NotNull(message = "Категория книги обязательна")
        Long categoryId,

        @NotEmpty(message = "У книги должен быть хотя бы один автор")
        Set<Long> authorIds
) {
}