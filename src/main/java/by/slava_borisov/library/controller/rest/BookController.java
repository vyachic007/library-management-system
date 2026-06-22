package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.BookCreateRequestDto;
import by.slava_borisov.library.dto.request.BookUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.dto.response.BookDetailsResponseDto;
import by.slava_borisov.library.dto.response.BookResponseDto;
import by.slava_borisov.library.service.BookService;
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
        name = "Книги",
        description = "Поиск, просмотр и управление книгами библиотеки"
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Найти книги",
            description = "Выполняет поиск книг по названию, автору, категории и ISBN. Все параметры необязательные"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Результаты поиска получены"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра книг"
            )
    })
    @GetMapping("/search")
    public List<BookResponseDto> searchBooks(
            @Parameter(
                    description = "Название книги или его часть",
                    example = "Clean Code"
            )
            @RequestParam(required = false) String title,

            @Parameter(
                    description = "Имя или фамилия автора",
                    example = "Robert Martin"
            )
            @RequestParam(required = false) String author,

            @Parameter(
                    description = "Идентификатор категории",
                    example = "1"
            )
            @RequestParam(required = false) Long categoryId,

            @Parameter(
                    description = "ISBN книги",
                    example = "9780132350884"
            )
            @RequestParam(required = false) String isbn
    ) {
        return bookService.search(title, author, categoryId, isbn);
    }

    @Operation(
            summary = "Получить книгу по ISBN",
            description = "Возвращает информацию о книге по её ISBN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга найдена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра книги"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга с указанным ISBN не найдена"
            )
    })
    @GetMapping("/isbn/{isbn}")
    public BookResponseDto getBookByIsbn(
            @Parameter(
                    description = "ISBN книги",
                    example = "9780132350884"
            )
            @PathVariable("isbn") String isbn
    ) {
        return bookService.getByIsbn(isbn);
    }

    @Operation(
            summary = "Получить книги категории",
            description = "Возвращает список книг, относящихся к указанной категории"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список книг категории получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра книг"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Категория не найдена"
            )
    })
    @GetMapping("/category/{categoryId}")
    public List<BookResponseDto> getBooksByCategoryId(
            @Parameter(
                    description = "Идентификатор категории",
                    example = "1"
            )
            @PathVariable("categoryId") Long categoryId
    ) {
        return bookService.getByCategoryId(categoryId);
    }

    @Operation(
            summary = "Создать книгу",
            description = "Добавляет новую книгу в каталог. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Книга успешно создана"
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
                    description = "Указанная категория или автор не найдены"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Книга с таким ISBN уже существует"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(
            @Valid @RequestBody BookCreateRequestDto request
    ) {
        return bookService.create(request);
    }

    @Operation(
            summary = "Получить все книги",
            description = "Возвращает список всех книг библиотеки"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список книг получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра книг"
            )
    })
    @GetMapping
    public List<BookResponseDto> getAllBooks() {
        return bookService.getAll();
    }

    @Operation(
            summary = "Получить книги автора",
            description = "Возвращает список книг указанного автора"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список книг автора получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра книг"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Автор не найден"
            )
    })
    @GetMapping("/author/{authorId}")
    public List<BookResponseDto> getBooksByAuthorId(
            @Parameter(
                    description = "Идентификатор автора",
                    example = "1"
            )
            @PathVariable("authorId") Long authorId
    ) {
        return bookService.getByAuthorId(authorId);
    }

    @Operation(
            summary = "Получить подробную информацию о книге",
            description = "Возвращает сведения о книге вместе с авторами, категорией и экземплярами"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Подробная информация о книге получена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра книги"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга не найдена"
            )
    })
    @GetMapping("/{bookId}/details")
    public BookDetailsResponseDto getBookDetailsById(
            @Parameter(
                    description = "Идентификатор книги",
                    example = "1"
            )
            @PathVariable("bookId") Long bookId
    ) {
        return bookService.getDetailsById(bookId);
    }

    @Operation(
            summary = "Получить доступные экземпляры книги",
            description = "Возвращает экземпляры книги, которые в настоящее время доступны для выдачи"
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
    @GetMapping("/{bookId}/available-copies")
    public List<BookCopyResponseDto> getAvailableCopies(
            @Parameter(
                    description = "Идентификатор книги",
                    example = "1"
            )
            @PathVariable("bookId") Long bookId
    ) {
        return bookService.getAvailableCopies(bookId);
    }

    @Operation(
            summary = "Получить книгу по идентификатору",
            description = "Возвращает основную информацию о книге"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга найдена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра книги"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Книга не найдена"
            )
    })
    @GetMapping("/{bookId}")
    public BookResponseDto getBookById(
            @Parameter(
                    description = "Идентификатор книги",
                    example = "1"
            )
            @PathVariable("bookId") Long bookId
    ) {
        return bookService.getById(bookId);
    }

    @Operation(
            summary = "Обновить книгу",
            description = "Изменяет информацию о книге. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Книга успешно обновлена"
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
                    description = "Книга, категория или автор не найдены"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Указанный ISBN уже используется другой книгой"
            )
    })
    @PutMapping("/{bookId}")
    public BookResponseDto updateBook(
            @Parameter(
                    description = "Идентификатор книги",
                    example = "1"
            )
            @PathVariable("bookId") Long bookId,

            @Valid @RequestBody BookUpdateRequestDto request
    ) {
        return bookService.update(bookId, request);
    }

    @Operation(
            summary = "Удалить книгу",
            description = "Удаляет книгу по идентификатору. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Книга успешно удалена"
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
            )
    })
    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookById(
            @Parameter(
                    description = "Идентификатор книги",
                    example = "1"
            )
            @PathVariable("bookId") Long bookId
    ) {
        bookService.delete(bookId);
    }
}