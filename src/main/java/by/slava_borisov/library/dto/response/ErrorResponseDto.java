package by.slava_borisov.library.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDto(

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        )
        LocalDateTime timestamp,

        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResponseDto> fieldErrors
) {
}