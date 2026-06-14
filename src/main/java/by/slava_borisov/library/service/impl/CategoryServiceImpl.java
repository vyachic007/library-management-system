package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.CategoryDao;
import by.slava_borisov.library.dto.request.CategoryCreateRequestDto;
import by.slava_borisov.library.dto.request.CategoryUpdateRequestDto;
import by.slava_borisov.library.dto.response.CategoryResponseDto;
import by.slava_borisov.library.exception.DuplicateException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.exception.ValidationException;
import by.slava_borisov.library.mapper.CategoryMapper;
import by.slava_borisov.library.model.Category;
import by.slava_borisov.library.service.CategoryService;
import by.slava_borisov.library.util.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDto create(CategoryCreateRequestDto requestDto) {
        log.info("Создание категории: name={}, parentId={}", requestDto.name(), requestDto.parentId());

        if (categoryDao.existsByName(requestDto.name())) {
            log.warn("Попытка создать категорию с уже существующим названием: name={}", requestDto.name());
            throw new DuplicateException(Messages.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.toEntity(requestDto);

        if (requestDto.parentId() != null) {
            Category parent = getCategoryEntityById(requestDto.parentId());
            category.setParent(parent);
        }

        Category savedCategory = categoryDao.save(category);

        log.info("Категория успешно создана: id={}, name={}, parentId={}",
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getParent() == null ? null : savedCategory.getParent().getId());

        return categoryMapper.toResponseDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto update(Long categoryId, CategoryUpdateRequestDto requestDto) {
        log.info("Обновление категории: id={}, name={}, parentId={}",
                categoryId, requestDto.name(), requestDto.parentId());

        Category category = getCategoryEntityById(categoryId);

        categoryDao.findByName(requestDto.name())
                .filter(existingCategory -> !existingCategory.getId().equals(categoryId))
                .ifPresent(existingCategory -> {
                    log.warn("Попытка обновить категорию на уже существующее название: id={}, name={}",
                            categoryId, requestDto.name());
                    throw new DuplicateException(Messages.CATEGORY_ALREADY_EXISTS);
                });

        categoryMapper.updateEntityFromDto(requestDto, category);

        if (requestDto.parentId() == null) {
            category.setParent(null);
        } else {
            if (requestDto.parentId().equals(categoryId)) {
                log.warn("Попытка сделать категорию родителем самой себя: id={}", categoryId);
                throw new ValidationException(Messages.CATEGORY_CANNOT_BE_PARENT_OF_ITSELF);
            }

            Category parent = getCategoryEntityById(requestDto.parentId());
            category.setParent(parent);
        }

        Category updatedCategory = categoryDao.update(category);

        log.info("Категория успешно обновлена: id={}, name={}, parentId={}",
                updatedCategory.getId(),
                updatedCategory.getName(),
                updatedCategory.getParent() == null ? null : updatedCategory.getParent().getId());

        return categoryMapper.toResponseDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getById(Long categoryId) {
        log.info("Получение категории по id={}", categoryId);

        Category category = getCategoryEntityById(categoryId);

        log.info("Категория найдена: id={}, name={}, parentId={}",
                category.getId(),
                category.getName(),
                category.getParent() == null ? null : category.getParent().getId());

        return categoryMapper.toResponseDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        log.info("Получение списка всех категорий");

        List<Category> categories = categoryDao.findAll();

        log.info("Получен список всех категорий, количество={}", categories.size());

        return categoryMapper.toResponseDtoList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getRootCategories() {
        log.info("Получение списка корневых категорий");

        List<Category> categories = categoryDao.findRootCategories();

        log.info("Получен список корневых категорий, количество={}", categories.size());

        return categoryMapper.toResponseDtoList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getByParentId(Long parentId) {
        log.info("Получение подкатегорий по parentId={}", parentId);

        getCategoryEntityById(parentId);
        List<Category> categories = categoryDao.findByParentId(parentId);

        log.info("Получены подкатегории: parentId={}, количество={}", parentId, categories.size());

        return categoryMapper.toResponseDtoList(categories);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        log.info("Удаление категории: id={}", categoryId);

        Category category = getCategoryEntityById(categoryId);
        categoryDao.delete(category);

        log.info("Категория успешно удалена: id={}, name={}", category.getId(), category.getName());
    }

    private Category getCategoryEntityById(Long categoryId) {
        return categoryDao.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Категория не найдена: id={}", categoryId);
                    return new NotFoundException(
                            Messages.CATEGORY_NOT_FOUND_BY_ID.formatted(categoryId)
                    );
                });
    }
}