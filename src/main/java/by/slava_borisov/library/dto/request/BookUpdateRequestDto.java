package by.slava_borisov.library.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequestDto {

    private String title;
    private String isbn;
    private String description;
    private Integer publicationYear;
    private Long categoryId;
    private Set<Long> authorIds;
}