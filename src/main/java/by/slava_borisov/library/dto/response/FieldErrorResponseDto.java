package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибка валидации отдельного поля")
public record FieldErrorResponseDto(

        @Schema(
                description = "Название поля, не прошедшего валидацию",
                example = "email"
        )
        String field,

        @Schema(
                description = "Сообщение об ошибке валидации",
                example = "Email должен быть корректным"
        )
        String message
) {
}