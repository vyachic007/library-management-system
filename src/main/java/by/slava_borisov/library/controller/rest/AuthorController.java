package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.AuthorCreateRequestDto;
import by.slava_borisov.library.dto.request.AuthorUpdateRequestDto;
import by.slava_borisov.library.dto.response.AuthorResponseDto;
import by.slava_borisov.library.dto.response.ErrorResponseDto;
import by.slava_borisov.library.service.AuthorService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Авторы",
        description = "Просмотр, поиск и управление авторами книг"
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @Operation(
            summary = "Создать автора",
            description = "Добавляет нового автора. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Автор успешно создан"
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
                    description = "Операция доступна только администратору",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseDto createAuthor(
            @Valid @RequestBody AuthorCreateRequestDto request
    ) {
        return authorService.create(request);
    }


    @Operation(
            summary = "Получить всех авторов",
            description = "Возвращает список всех авторов"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список авторов получен"
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
                    description = "Недостаточно прав для просмотра авторов",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public List<AuthorResponseDto> getAllAuthors() {
        return authorService.getAll();
    }


    @Operation(
            summary = "Найти авторов по фамилии",
            description = "Возвращает список авторов с указанной фамилией"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Результаты поиска получены"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Фамилия автора не указана",
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
                    description = "Недостаточно прав для просмотра авторов",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/search")
    public List<AuthorResponseDto> getAuthorsByLastName(
            @Parameter(
                    description = "Фамилия автора",
                    example = "Martin",
                    required = true
            )
            @RequestParam String lastName
    ) {
        return authorService.getByLastName(lastName);
    }


    @Operation(
            summary = "Получить автора по идентификатору",
            description = "Возвращает информацию об авторе по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Автор найден"
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
                    description = "Недостаточно прав для просмотра автора",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Автор не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{authorId}")
    public AuthorResponseDto getAuthorById(
            @Parameter(
                    description = "Идентификатор автора",
                    example = "1"
            )
            @PathVariable("authorId") Long authorId
    ) {
        return authorService.getById(authorId);
    }


    @Operation(
            summary = "Обновить автора",
            description = "Изменяет данные автора. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные автора успешно обновлены"
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
                    description = "Операция доступна только администратору",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Автор не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{authorId}")
    public AuthorResponseDto updateAuthor(
            @Parameter(
                    description = "Идентификатор автора",
                    example = "1"
            )
            @PathVariable("authorId") Long authorId,

            @Valid @RequestBody AuthorUpdateRequestDto request
    ) {
        return authorService.update(authorId, request);
    }


    @Operation(
            summary = "Удалить автора",
            description = "Удаляет автора по идентификатору. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Автор успешно удалён"
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
                    description = "Автор не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Невозможно удалить автора, связанного с книгами",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAuthorById(
            @Parameter(
                    description = "Идентификатор автора",
                    example = "1"
            )
            @PathVariable("authorId") Long authorId
    ) {
        authorService.delete(authorId);
    }
}