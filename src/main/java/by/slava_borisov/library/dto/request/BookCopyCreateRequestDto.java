package by.slava_borisov.library.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyCreateRequestDto {

    private Long bookId;
    private String inventoryNumber;
    private String status;
    private String conditionDescription;
}