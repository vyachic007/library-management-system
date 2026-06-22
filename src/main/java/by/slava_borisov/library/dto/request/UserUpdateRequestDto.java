package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для обновления профиля пользователя")
public record UserUpdateRequestDto(

        @Schema(
                description = "Новый адрес электронной почты",
                example = "updated@example.com",
                format = "email",
                maxLength = 150
        )
        @NotBlank(message = "Email не должен быть пустым")
        @Email(message = "Email должен быть корректным")
        @Size(max = 150, message = "Email не должен превышать 150 символов")
        String email,

        @Schema(
                description = "Имя пользователя",
                example = "Вячеслав",
                maxLength = 100
        )
        @NotBlank(message = "Имя не должно быть пустым")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String firstName,

        @Schema(
                description = "Фамилия пользователя",
                example = "Борисов",
                maxLength = 100
        )
        @NotBlank(message = "Фамилия не должна быть пустой")
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        String lastName,

        @Schema(
                description = "Номер телефона пользователя",
                example = "+79991234567",
                maxLength = 30,
                nullable = true
        )
        @Size(max = 30, message = "Телефон не должен превышать 30 символов")
        String phone
) {
}