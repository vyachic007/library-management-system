package by.slava_borisov.library.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о категории книг")
public record CategoryResponseDto(

        @Schema(
                description = "Уникальный идентификатор категории",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Название категории",
                example = "Программирование"
        )
        String name,

        @Schema(
                description = "Идентификатор родительской категории",
                example = "2",
                nullable = true
        )
        Long parentId
) {
}