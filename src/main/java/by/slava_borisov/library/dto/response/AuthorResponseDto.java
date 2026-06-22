package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об авторе книги")
public record AuthorResponseDto(

        @Schema(
                description = "Уникальный идентификатор автора",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Имя автора",
                example = "Robert"
        )
        String firstName,

        @Schema(
                description = "Фамилия автора",
                example = "Martin"
        )
        String lastName
) {
}