package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.CategoryCreateRequestDto;
import by.slava_borisov.library.dto.request.CategoryUpdateRequestDto;
import by.slava_borisov.library.dto.response.CategoryResponseDto;
import by.slava_borisov.library.service.CategoryService;
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
        name = "Категории",
        description = "Просмотр и управление категориями книг"
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Создать категорию",
            description = "Добавляет новую категорию книг. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Категория успешно создана"
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
                    description = "Родительская категория не найдена"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Категория с таким названием уже существует"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(
            @Valid @RequestBody CategoryCreateRequestDto request
    ) {
        return categoryService.create(request);
    }

    @Operation(
            summary = "Обновить категорию",
            description = "Изменяет данные категории. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Категория успешно обновлена"
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
                    description = "Категория или родительская категория не найдена"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Категория с таким названием уже существует"
            )
    })
    @PutMapping("/{categoryId}")
    public CategoryResponseDto updateCategory(
            @Parameter(
                    description = "Идентификатор категории",
                    example = "1"
            )
            @PathVariable("categoryId") Long categoryId,

            @Valid @RequestBody CategoryUpdateRequestDto request
    ) {
        return categoryService.update(categoryId, request);
    }

    @Operation(
            summary = "Получить все категории",
            description = "Возвращает список всех категорий книг"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список категорий получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра категорий"
            )
    })
    @GetMapping
    public List<CategoryResponseDto> getAllCategories() {
        return categoryService.getAll();
    }

    @Operation(
            summary = "Получить корневые категории",
            description = "Возвращает категории верхнего уровня, не имеющие родительской категории"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список корневых категорий получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра категорий"
            )
    })
    @GetMapping("/root")
    public List<CategoryResponseDto> getRootCategories() {
        return categoryService.getRootCategories();
    }

    @Operation(
            summary = "Получить подкатегории",
            description = "Возвращает список подкатегорий указанной родительской категории"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список подкатегорий получен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра категорий"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Родительская категория не найдена"
            )
    })
    @GetMapping("/{parentId}/subcategories")
    public List<CategoryResponseDto> getSubcategoriesByParentId(
            @Parameter(
                    description = "Идентификатор родительской категории",
                    example = "1"
            )
            @PathVariable("parentId") Long parentId
    ) {
        return categoryService.getByParentId(parentId);
    }

    @Operation(
            summary = "Получить категорию по идентификатору",
            description = "Возвращает информацию о категории по её идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Категория найдена"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для просмотра категории"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Категория не найдена"
            )
    })
    @GetMapping("/{categoryId}")
    public CategoryResponseDto getCategoryById(
            @Parameter(
                    description = "Идентификатор категории",
                    example = "1"
            )
            @PathVariable("categoryId") Long categoryId
    ) {
        return categoryService.getById(categoryId);
    }

    @Operation(
            summary = "Удалить категорию",
            description = "Удаляет категорию по идентификатору. Операция доступна только администратору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Категория успешно удалена"
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
                    description = "Категория не найдена"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Невозможно удалить категорию, связанную с книгами или подкатегориями"
            )
    })
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoryById(
            @Parameter(
                    description = "Идентификатор категории",
                    example = "1"
            )
            @PathVariable("categoryId") Long categoryId
    ) {
        categoryService.delete(categoryId);
    }
}