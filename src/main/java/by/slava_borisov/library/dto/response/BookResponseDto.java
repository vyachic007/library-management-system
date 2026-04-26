package by.slava_borisov.library.dto.response;

public record BookResponseDto(
        Long id,
        String title,
        String isbn,
        Integer publicationYear
) {
}