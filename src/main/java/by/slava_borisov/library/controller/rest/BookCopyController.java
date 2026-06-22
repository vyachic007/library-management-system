package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.BookCopyCreateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyStatusUpdateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.service.BookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
        name = "Экземпляры книг",
        description = "Просмотр и управление физическими экземплярами книг"
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/book-copies")
@RequiredArgsConstructor
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @Operation(
            summary = "Создать экземпляр книги",
            description = "Добавляет новый физический экземпляр книги. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Экземпляр книги успешно создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Переданы некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга не найдена"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Экземпляр с таким инвентарным номером уже существует"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookCopyResponseDto createBookCopy(
            @Valid @RequestBody BookCopyCreateRequestDto request
    ) {
        return bookCopyService.create(request);
    }

    @Operation(
            summary = "Получить все экземпляры книг",
            description = "Возвращает список всех экземпляров книг"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список экземпляров получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра экземпляров"
            )
    })
    @GetMapping
    public List<BookCopyResponseDto> getAllBookCopies() {
        return bookCopyService.getAll();
    }

    @Operation(
            summary = "Получить экземпляры по статусу",
            description = "Возвращает список экземпляров книг с указанным статусом"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список экземпляров получен"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Указан некорректный статус"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра экземпляров"
            )
    })
    @GetMapping("/status/{status}")
    public List<BookCopyResponseDto> getBookCopiesByStatus(
            @Parameter(
                    description = "Статус экземпляра книги",
                    example = "AVAILABLE"
            )
            @PathVariable("status") String status
    ) {
        return bookCopyService.getByStatus(status);
    }

    @Operation(
            summary = "Получить экземпляр по инвентарному номеру",
            description = "Возвращает экземпляр книги по его уникальному инвентарному номеру"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Экземпляр книги найден"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра экземпляра"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Экземпляр книги не найден"
            )
    })
    @GetMapping("/inventory/{inventoryNumber}")
    public BookCopyResponseDto getBookCopyByInventoryNumber(
            @Parameter(
                    description = "Инвентарный номер экземпляра",
                    example = "INV-0001"
            )
            @PathVariable("inventoryNumber") String inventoryNumber
    ) {
        return bookCopyService.getByInventoryNumber(inventoryNumber);
    }

    @Operation(
            summary = "Получить экземпляры указанной книги",
            description = "Возвращает все физические экземпляры выбранной книги"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список экземпляров книги получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра экземпляров"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга не найдена"
            )
    })
    @GetMapping("/book/{bookId}")
    public List<BookCopyResponseDto> getBookCopiesByBookId(
            @Parameter(
                    description = "Идентификатор книги",
                    example = "1"
            )
            @PathVariable("bookId") Long bookId
    ) {
        return bookCopyService.getByBookId(bookId);
    }

    @Operation(
            summary = "Получить доступные экземпляры книги",
            description = "Возвращает экземпляры указанной книги, доступные для выдачи"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список доступных экземпляров получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра экземпляров"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга не найдена"
            )
    })
    @GetMapping("/book/{bookId}/available")
    public List<BookCopyResponseDto> getAvailableBookCopiesByBookId(
            @Parameter(
                    description = "Идентификатор книги",
                    example = "1"
            )
            @PathVariable("bookId") Long bookId
    ) {
        return bookCopyService.getAvailableByBookId(bookId);
    }

    @Operation(
            summary = "Получить экземпляр по идентификатору",
            description = "Возвращает информацию об экземпляре книги по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Экземпляр книги найден"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра экземпляра"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Экземпляр книги не найден"
            )
    })
    @GetMapping("/{copyId}")
    public BookCopyResponseDto getBookCopyById(
            @Parameter(
                    description = "Идентификатор экземпляра книги",
                    example = "1"
            )
            @PathVariable("copyId") Long copyId
    ) {
        return bookCopyService.getById(copyId);
    }

    @Operation(
            summary = "Обновить экземпляр книги",
            description = "Изменяет данные экземпляра книги. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Экземпляр книги успешно обновлён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Переданы некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Экземпляр книги или книга не найдены"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Инвентарный номер уже используется другим экземпляром"
            )
    })
    @PutMapping("/{copyId}")
    public BookCopyResponseDto updateBookCopy(
            @Parameter(
                    description = "Идентификатор экземпляра книги",
                    example = "1"
            )
            @PathVariable("copyId") Long copyId,

            @Valid @RequestBody BookCopyUpdateRequestDto request
    ) {
        return bookCopyService.update(copyId, request);
    }

    @Operation(
            summary = "Изменить статус экземпляра",
            description = "Изменяет текущий статус экземпляра книги. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус экземпляра успешно изменён"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Указан некорректный статус"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Экземпляр книги не найден"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Изменение статуса невозможно в текущем состоянии экземпляра"
            )
    })
    @PatchMapping("/{copyId}/status")
    public BookCopyResponseDto changeBookCopyStatus(
            @Parameter(
                    description = "Идентификатор экземпляра книги",
                    example = "1"
            )
            @PathVariable("copyId") Long copyId,

            @Valid @RequestBody BookCopyStatusUpdateRequestDto request
    ) {
        return bookCopyService.changeStatus(copyId, request);
    }

    @Operation(
            summary = "Удалить экземпляр книги",
            description = "Удаляет экземпляр книги. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Экземпляр книги успешно удалён"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Операция доступна только администратору"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Экземпляр книги не найден"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Невозможно удалить экземпляр, участвующий в активной выдаче"
            )
    })
    @DeleteMapping("/{copyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCopyById(
            @Parameter(
                    description = "Идентификатор экземпляра книги",
                    example = "1"
            )
            @PathVariable("copyId") Long copyId
    ) {
        bookCopyService.delete(copyId);
    }
}