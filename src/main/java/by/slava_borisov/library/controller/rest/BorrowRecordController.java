package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.BorrowBookRequestDto;
import by.slava_borisov.library.dto.request.ExtendBorrowRequestDto;
import by.slava_borisov.library.dto.request.ReturnBookRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.dto.response.ErrorResponseDto;
import by.slava_borisov.library.service.BorrowRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Выдача книг",
        description = "Выдача, возврат и продление книг, а также просмотр истории выдач"
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @Operation(
            summary = "Выдать книгу пользователю",
            description = "Создаёт запись о выдаче доступного экземпляра книги"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Книга успешно выдана"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Переданы некорректные данные",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для выдачи книги указанному пользователю",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь или экземпляр книги не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Экземпляр книги недоступен для выдачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/rent")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowRecordResponseDto borrowBook(
            @Valid @RequestBody BorrowBookRequestDto request
    ) {
        return borrowRecordService.borrowBook(request);
    }

    @Operation(
            summary = "Вернуть книгу",
            description = "Закрывает активную запись выдачи и возвращает экземпляру статус доступного"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга успешно возвращена"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Переданы некорректные данные",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для возврата указанной книги",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запись выдачи не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Книга уже была возвращена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/return")
    public BorrowRecordResponseDto returnBook(
            @Valid @RequestBody ReturnBookRequestDto request
    ) {
        return borrowRecordService.returnBook(request);
    }

    @Operation(
            summary = "Продлить срок выдачи",
            description = "Изменяет дату планового возврата активной записи выдачи"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Срок выдачи успешно продлён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Передана некорректная дата возврата",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для продления этой выдачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запись выдачи не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Невозможно продлить завершённую или просроченную выдачу",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/{recordId}/extend")
    public BorrowRecordResponseDto extendBorrowPeriod(
            @Parameter(
                    description = "Идентификатор записи выдачи",
                    example = "1"
            )
            @PathVariable("recordId") Long recordId,

            @Valid @RequestBody ExtendBorrowRequestDto request
    ) {
        return borrowRecordService.extendBorrowPeriod(recordId, request);
    }

    @Operation(
            summary = "Получить все записи выдачи",
            description = "Возвращает все записи выдачи книг. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список записей выдачи получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping
    public List<BorrowRecordResponseDto> getAllBorrowRecords() {
        return borrowRecordService.getAll();
    }

    @Operation(
            summary = "Получить все активные выдачи",
            description = "Возвращает активные записи выдачи. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список активных выдач получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/active")
    public List<BorrowRecordResponseDto> getCurrentBorrows() {
        return borrowRecordService.getCurrentBorrows();
    }

    @Operation(
            summary = "Получить все просроченные выдачи",
            description = "Возвращает просроченные записи выдачи. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список просроченных выдач получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/overdue")
    public List<BorrowRecordResponseDto> getOverdueBorrows() {
        return borrowRecordService.getOverdueBorrows();
    }

    @Operation(
            summary = "Получить выдачи пользователя",
            description = "Возвращает все записи выдачи указанного пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список выдач пользователя получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра выдач этого пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/user/{userId}")
    public List<BorrowRecordResponseDto> getBorrowRecordsByUserId(
            @Parameter(
                    description = "Идентификатор пользователя",
                    example = "1"
            )
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getByUserId(userId);
    }

    @Operation(
            summary = "Получить активные выдачи пользователя",
            description = "Возвращает текущие активные выдачи указанного пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список активных выдач пользователя получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра выдач этого пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/user/{userId}/current")
    public List<BorrowRecordResponseDto> getCurrentBorrowsByUserId(
            @Parameter(
                    description = "Идентификатор пользователя",
                    example = "1"
            )
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getCurrentBorrowsByUserId(userId);
    }

    @Operation(
            summary = "Получить историю выдач пользователя",
            description = "Возвращает завершённые записи выдачи указанного пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "История выдач пользователя получена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра истории этого пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/user/{userId}/history")
    public List<BorrowRecordResponseDto> getBorrowHistoryByUserId(
            @Parameter(
                    description = "Идентификатор пользователя",
                    example = "1"
            )
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getBorrowHistoryByUserId(userId);
    }

    @Operation(
            summary = "Получить просроченные выдачи пользователя",
            description = "Возвращает просроченные выдачи указанного пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список просроченных выдач пользователя получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра выдач этого пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/user/{userId}/overdue")
    public List<BorrowRecordResponseDto> getOverdueBorrowsByUserId(
            @Parameter(
                    description = "Идентификатор пользователя",
                    example = "1"
            )
            @PathVariable("userId") Long userId
    ) {
        return borrowRecordService.getOverdueBorrowsByUserId(userId);
    }

    @Operation(
            summary = "Получить выдачи экземпляра книги",
            description = "Возвращает все записи выдачи экземпляра. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список выдач экземпляра получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Экземпляр книги не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/book-copy/{copyId}")
    public List<BorrowRecordResponseDto> getBorrowRecordsByBookCopyId(
            @Parameter(
                    description = "Идентификатор экземпляра книги",
                    example = "1"
            )
            @PathVariable("copyId") Long copyId
    ) {
        return borrowRecordService.getByBookCopyId(copyId);
    }

    @Operation(
            summary = "Получить историю экземпляра книги",
            description = "Возвращает завершённые выдачи экземпляра. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "История выдач экземпляра получена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Экземпляр книги не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/book-copy/{copyId}/history")
    public List<BorrowRecordResponseDto> getBorrowHistoryByBookCopyId(
            @Parameter(
                    description = "Идентификатор экземпляра книги",
                    example = "1"
            )
            @PathVariable("copyId") Long copyId
    ) {
        return borrowRecordService.getBorrowHistoryByBookCopyId(copyId);
    }

    @Operation(
            summary = "Получить запись выдачи по идентификатору",
            description = "Возвращает подробную информацию о записи выдачи"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Запись выдачи найдена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра этой записи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запись выдачи не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/{recordId}")
    public BorrowRecordResponseDto getBorrowRecordById(
            @Parameter(
                    description = "Идентификатор записи выдачи",
                    example = "1"
            )
            @PathVariable("recordId") Long recordId
    ) {
        return borrowRecordService.getById(recordId);
    }
}