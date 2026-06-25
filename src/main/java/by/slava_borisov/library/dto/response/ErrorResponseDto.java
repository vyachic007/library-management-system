package by.slava_borisov.library.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Стандартное тело ответа при ошибке")
public record ErrorResponseDto(

        @Schema(
                description = "Дата и время возникновения ошибки",
                example = "2026-06-22T21:30:00.000",
                type = "string",
                format = "date-time"
        )
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "HTTP-код ответа",
                example = "404"
        )
        int status,

        @Schema(
                description = "Краткое обозначение ошибки",
                example = "NOT_FOUND"
        )
        String error,

        @Schema(
                description = "Поясняющее сообщение об ошибке",
                example = "Книга с идентификатором 99 не найдена"
        )
        String message,

        @Schema(
                description = "Адрес запроса, при обработке которого возникла ошибка",
                example = "/api/books/99"
        )
        String path,

        @Schema(
                description = "Ошибки валидации отдельных полей. Пустой список, если ошибки полей отсутствуют"
        )
        List<FieldErrorResponseDto> fieldErrors
) {
}