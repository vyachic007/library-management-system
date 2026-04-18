package by.slava_borisov.library.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookRequestDto {

    private Long userId;
    private Long bookCopyId;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
}