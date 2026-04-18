package by.slava_borisov.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordResponseDto {

    private Long id;
    private Long userId;
    private String username;
    private Long bookCopyId;
    private String inventoryNumber;
    private String bookTitle;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;
    private String status;
}