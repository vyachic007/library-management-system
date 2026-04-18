package by.slava_borisov.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsResponseDto {

    private Long id;
    private String title;
    private String isbn;
    private String description;
    private Integer publicationYear;
    private CategoryResponseDto category;
    private List<AuthorResponseDto> authors;
}