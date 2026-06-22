package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для добавления нового автора")
public record AuthorCreateRequestDto(

        @Schema(
                description = "Имя автора",
                example = "Robert",
                maxLength = 100
        )
        @NotBlank(message = "Имя автора не должно быть пустым")
        @Size(max = 100, message = "Имя автора не должно превышать 100 символов")
        String firstName,

        @Schema(
                description = "Фамилия автора",
                example = "Martin",
                maxLength = 100
        )
        @NotBlank(message = "Фамилия автора не должна быть пустой")
        @Size(max = 100, message = "Фамилия автора не должна превышать 100 символов")
        String lastName
) {
}