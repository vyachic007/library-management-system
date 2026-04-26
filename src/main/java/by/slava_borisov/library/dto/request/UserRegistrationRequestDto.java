package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDto(

        @NotBlank(message = "Имя пользователя не должно быть пустым")
        @Size(max = 100, message = "Имя пользователя не должно превышать 100 символов")
        String username,

        @NotBlank(message = "Пароль не должен быть пустым")
        @Size(min = 6, max = 255, message = "Пароль должен содержать от 6 до 255 символов")
        String password,

        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Email должен быть корректным")
        @Size(max = 150, message = "Email не должен превышать 150 символов")
        String email,

        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String firstName,

        @NotBlank(message = "Фамилия не должна быть пустой")
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        String lastName,

        @Size(max = 30, message = "Телефон не должен превышать 30 символов")
        String phone
) {
}