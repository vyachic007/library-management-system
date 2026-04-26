package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(

        @NotBlank(message = "Имя пользователя не должно быть пустым")
        @Size(max = 100, message = "Имя пользователя не должно превышать 100 символов")
        String username,

        @NotBlank(message = "Пароль не должен быть пустым")
        String password
) {
}