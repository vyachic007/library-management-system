package by.slava_borisov.library.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для обновления категории")
public record CategoryUpdateRequestDto(

        @Schema(
                description = "Новое название категории",
                example = "Веб-разработка",
                maxLength = 150
        )
        @NotBlank(message = "Название категории не должно быть пустым")
        @Size(max = 150, message = "Название категории не должно превышать 150 символов")
        String name,

        @Schema(
                description = "Идентификатор новой родительской категории. Не указывается для корневой категории",
                example = "1",
                nullable = true
        )
        Long parentId
) {
}