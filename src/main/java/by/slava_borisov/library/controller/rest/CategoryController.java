package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.CategoryCreateRequestDto;
import by.slava_borisov.library.dto.request.CategoryUpdateRequestDto;
import by.slava_borisov.library.dto.response.CategoryResponseDto;
import by.slava_borisov.library.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(
            @Valid @RequestBody CategoryCreateRequestDto request
    ) {
        return categoryService.create(request);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponseDto updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CategoryUpdateRequestDto request
    ) {
        return categoryService.update(categoryId, request);
    }

    @GetMapping
    public List<CategoryResponseDto> getAllCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/root")
    public List<CategoryResponseDto> getRootCategories() {
        return categoryService.getRootCategories();
    }

    @GetMapping("/{parentId}/subcategories")
    public List<CategoryResponseDto> getSubcategoriesByParentId(
            @PathVariable("parentId") Long parentId
    ) {
        return categoryService.getByParentId(parentId);
    }

    @GetMapping("/{categoryId}")
    public CategoryResponseDto getCategoryById(
            @PathVariable("categoryId") Long categoryId
    ) {
        return categoryService.getById(categoryId);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoryById(
            @PathVariable("categoryId") Long categoryId
    ) {
        categoryService.delete(categoryId);
    }
}
