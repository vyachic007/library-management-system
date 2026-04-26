package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.CategoryDao;
import by.slava_borisov.library.dto.request.CategoryCreateRequestDto;
import by.slava_borisov.library.dto.request.CategoryUpdateRequestDto;
import by.slava_borisov.library.dto.response.CategoryResponseDto;
import by.slava_borisov.library.mapper.CategoryMapper;
import by.slava_borisov.library.model.Category;
import by.slava_borisov.library.service.CategoryService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDto create(CategoryCreateRequestDto requestDto) {
        if (categoryDao.existsByName(requestDto.name())) {
            throw new IllegalArgumentException(Messages.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.toEntity(requestDto);

        if (requestDto.parentId() != null) {
            Category parent = getCategoryEntityById(requestDto.parentId());
            category.setParent(parent);
        }

        Category savedCategory = categoryDao.save(category);
        return categoryMapper.toResponseDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto update(Long categoryId, CategoryUpdateRequestDto requestDto) {
        Category category = getCategoryEntityById(categoryId);

        categoryDao.findByName(requestDto.name())
                .filter(existingCategory -> !existingCategory.getId().equals(categoryId))
                .ifPresent(existingCategory -> {
                    throw new IllegalArgumentException(Messages.CATEGORY_ALREADY_EXISTS);
                });

        categoryMapper.updateEntityFromDto(requestDto, category);

        if (requestDto.parentId() == null) {
            category.setParent(null);
        } else {
            if (requestDto.parentId().equals(categoryId)) {
                throw new IllegalArgumentException(Messages.CATEGORY_CANNOT_BE_PARENT_OF_ITSELF);
            }

            Category parent = getCategoryEntityById(requestDto.parentId());
            category.setParent(parent);
        }

        Category updatedCategory = categoryDao.update(category);
        return categoryMapper.toResponseDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getById(Long categoryId) {
        Category category = getCategoryEntityById(categoryId);
        return categoryMapper.toResponseDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        return categoryMapper.toResponseDtoList(categoryDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getRootCategories() {
        return categoryMapper.toResponseDtoList(categoryDao.findRootCategories());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getByParentId(Long parentId) {
        getCategoryEntityById(parentId);
        return categoryMapper.toResponseDtoList(categoryDao.findByParentId(parentId));
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        Category category = getCategoryEntityById(categoryId);
        categoryDao.delete(category);
    }

    private Category getCategoryEntityById(Long categoryId) {
        return categoryDao.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.CATEGORY_NOT_FOUND_BY_ID.formatted(categoryId)
                ));
    }
}