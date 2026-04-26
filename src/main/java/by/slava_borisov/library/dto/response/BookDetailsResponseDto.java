package by.slava_borisov.library.dto.response;

import java.util.List;

public record BookDetailsResponseDto(
        Long id,
        String title,
        String isbn,
        String description,
        Integer publicationYear,
        CategoryResponseDto category,
        List<AuthorResponseDto> authors
) {
}