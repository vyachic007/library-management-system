package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для обновления автора")
public record AuthorUpdateRequestDto(

        @Schema(
                description = "Новое имя автора",
                example = "Robert",
                maxLength = 100
        )
        @NotBlank(message = "Имя автора не должно быть пустым")
        @Size(max = 100, message = "Имя автора не должно превышать 100 символов")
        String firstName,

        @Schema(
                description = "Новая фамилия автора",
                example = "Martin",
                maxLength = 100
        )
        @NotBlank(message = "Фамилия автора не должна быть пустой")
        @Size(max = 100, message = "Фамилия автора не должна превышать 100 символов")
        String lastName
) {
}