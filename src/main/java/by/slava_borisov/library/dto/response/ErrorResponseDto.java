package by.slava_borisov.library.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ErrorResponseDto(
        LocalDate timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResponseDto> fieldErrors
) {
}
