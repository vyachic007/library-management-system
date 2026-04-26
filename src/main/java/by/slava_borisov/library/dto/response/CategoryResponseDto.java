package by.slava_borisov.library.dto.response;

public record CategoryResponseDto(
        Long id,
        String name,
        Long parentId
) {
}