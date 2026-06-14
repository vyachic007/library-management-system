package by.slava_borisov.library.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResponseDto> fieldErrors
) {
}
