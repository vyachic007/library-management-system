package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Информация о пользователе")
public record UserResponseDto(

        @Schema(
                description = "Уникальный идентификатор пользователя",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Имя пользователя",
                example = "testuser"
        )
        String username,

        @Schema(
                description = "Адрес электронной почты",
                example = "testuser@example.com",
                format = "email"
        )
        String email,

        @Schema(
                description = "Имя пользователя",
                example = "Вячеслав"
        )
        String firstName,

        @Schema(
                description = "Фамилия пользователя",
                example = "Борисов"
        )
        String lastName,

        @Schema(
                description = "Номер телефона",
                example = "+79991234567",
                nullable = true
        )
        String phone,

        @Schema(
                description = "Признак активности учётной записи",
                example = "true"
        )
        Boolean isActive,

        @Schema(
                description = "Роли пользователя",
                example = "[\"ROLE_USER\"]"
        )
        Set<String> roles
) {
}