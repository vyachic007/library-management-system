package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorUpdateRequestDto(

        @NotBlank(message = "Имя автора не должно быть пустым")
        @Size(max = 100, message = "Имя автора не должно превышать 100 символов")
        String firstName,

        @NotBlank(message = "Фамилия автора не должна быть пустой")
        @Size(max = 100, message = "Фамилия автора не должна превышать 100 символов")
        String lastName
) {
}