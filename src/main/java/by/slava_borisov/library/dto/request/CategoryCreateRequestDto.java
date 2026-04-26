package by.slava_borisov.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequestDto(

        @NotBlank(message = "Название категории не должно быть пустым")
        @Size(max = 150, message = "Название категории не должно превышать 150 символов")
        String name,

        Long parentId
) {
}