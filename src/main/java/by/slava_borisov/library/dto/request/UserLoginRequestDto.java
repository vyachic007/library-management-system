package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для входа пользователя в систему")
public record UserLoginRequestDto(

        @Schema(
                description = "Имя пользователя",
                example = "testuser",
                maxLength = 100
        )
        @NotBlank(message = "Имя пользователя не должно быть пустым")
        @Size(
                max = 100,
                message = "Имя пользователя не должно превышать 100 символов"
        )
        String username,

        @Schema(
                description = "Пароль пользователя",
                example = "test12345",
                format = "password",
                accessMode = Schema.AccessMode.WRITE_ONLY
        )
        @NotBlank(message = "Пароль не должен быть пустым")
        String password
) {
}