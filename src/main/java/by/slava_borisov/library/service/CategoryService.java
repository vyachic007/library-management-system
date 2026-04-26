package by.slava_borisov.library.service;

import by.slava_borisov.library.dto.request.CategoryCreateRequestDto;
import by.slava_borisov.library.dto.request.CategoryUpdateRequestDto;
import by.slava_borisov.library.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto create(CategoryCreateRequestDto requestDto);

    CategoryResponseDto update(Long categoryId, CategoryUpdateRequestDto requestDto);

    CategoryResponseDto getById(Long categoryId);

    List<CategoryResponseDto> getAll();

    List<CategoryResponseDto> getRootCategories();

    List<CategoryResponseDto> getByParentId(Long parentId);

    void delete(Long categoryId);
}