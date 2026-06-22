package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат успешной аутентификации пользователя")
public record JwtAuthResponseDto(

        @Schema(
                description = "Тип токена авторизации",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "JWT-токен для доступа к защищённым endpoint’ам",
                example = "eyJhbGciOiJIUzI1NiJ9...",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String accessToken,

        @Schema(
                description = "Информация об аутентифицированном пользователе"
        )
        UserResponseDto user
) {
}