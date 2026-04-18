package by.slava_borisov.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyResponseDto {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String inventoryNumber;
    private String status;
    private String conditionDescription;
}