package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.CategoryDao;
import by.slava_borisov.library.dto.request.CategoryCreateRequestDto;
import by.slava_borisov.library.dto.request.CategoryUpdateRequestDto;
import by.slava_borisov.library.dto.response.CategoryResponseDto;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.CategoryMapper;
import by.slava_borisov.library.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    @DisplayName("Должен вернуть CategoryResponseDto с данными, если категория существует")
    void getById_shouldReturnCategory_whenCategoryExists() {
        Category category = new Category();
        category.setId(15L);
        category.setName("Программирование");

        CategoryResponseDto responseDto = new CategoryResponseDto(
                15L,
                "Программирование",
                null
        );

        Mockito.when(categoryDao.findById(15L))
                .thenReturn(Optional.of(category));

        Mockito.when(categoryMapper.toResponseDto(category))
                .thenReturn(responseDto);

        CategoryResponseDto result = categoryService.getById(15L);

        assertEquals(15L, result.id());
        assertEquals("Программирование", result.name());
        assertNull(result.parentId());

        Mockito.verify(categoryDao).findById(15L);
        Mockito.verify(categoryMapper).toResponseDto(category);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException, если категория с таким ID не найдена")
    void getById_shouldThrowException_whenCategoryNotFound() {
        Mockito.when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.getById(99L)
        );

        Mockito.verify(categoryDao).findById(99L);
        verifyNoInteractions(categoryMapper);
    }


    @Test
    @DisplayName("Должен вернуть список всех категорий")
    void getAll_shouldReturnAllCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Программирование");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Базы данных");

        List<Category> categories = List.of(category1, category2);

        CategoryResponseDto responseDto1 = new CategoryResponseDto(
                1L,
                "Программирование",
                null
        );

        CategoryResponseDto responseDto2 = new CategoryResponseDto(
                2L,
                "Базы данных",
                null
        );

        List<CategoryResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(categoryDao.findAll())
                .thenReturn(categories);

        Mockito.when(categoryMapper.toResponseDtoList(categories))
                .thenReturn(responseDtos);

        List<CategoryResponseDto> result = categoryService.getAll();

        assertEquals(2, result.size());
        assertEquals("Программирование", result.get(0).name());
        assertEquals("Базы данных", result.get(1).name());

        Mockito.verify(categoryDao).findAll();
        Mockito.verify(categoryMapper).toResponseDtoList(categories);
    }


    @Test
    @DisplayName("Должен создать категорию без родителя, если название уникальное")
    void create_shouldCreateCategory_whenNameIsUniqueAndParentIsNull() {
        CategoryCreateRequestDto request = new CategoryCreateRequestDto(
                "Программирование",
                null
        );

        Category categoryToSave = new Category();
        categoryToSave.setName("Программирование");
        categoryToSave.setParent(null);

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Программирование");
        savedCategory.setParent(null);

        CategoryResponseDto responseDto = new CategoryResponseDto(
                1L,
                "Программирование",
                null
        );

        Mockito.when(categoryDao.existsByName("Программирование"))
                .thenReturn(false);

        Mockito.when(categoryMapper.toEntity(request))
                .thenReturn(categoryToSave);

        Mockito.when(categoryDao.save(categoryToSave))
                .thenReturn(savedCategory);

        Mockito.when(categoryMapper.toResponseDto(savedCategory))
                .thenReturn(responseDto);

        CategoryResponseDto result = categoryService.create(request);

        assertEquals(1L, result.id());
        assertEquals("Программирование", result.name());
        assertNull(result.parentId());

        Mockito.verify(categoryDao).existsByName("Программирование");
        Mockito.verify(categoryMapper).toEntity(request);
        Mockito.verify(categoryDao).save(categoryToSave);
        Mockito.verify(categoryMapper).toResponseDto(savedCategory);
    }


    @Test
    @DisplayName("Должен создать категорию с родителем, если название уникальное и родитель существует")
    void create_shouldCreateCategory_whenNameIsUniqueAndParentExists() {
        CategoryCreateRequestDto request = new CategoryCreateRequestDto(
                "Java",
                10L
        );

        Category parentCategory = new Category();
        parentCategory.setId(10L);
        parentCategory.setName("Программирование");

        Category categoryToSave = new Category();
        categoryToSave.setName("Java");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Java");
        savedCategory.setParent(parentCategory);

        CategoryResponseDto responseDto = new CategoryResponseDto(
                1L,
                "Java",
                10L
        );

        Mockito.when(categoryDao.existsByName("Java"))
                .thenReturn(false);

        Mockito.when(categoryMapper.toEntity(request))
                .thenReturn(categoryToSave);

        Mockito.when(categoryDao.findById(10L))
                .thenReturn(Optional.of(parentCategory));

        Mockito.when(categoryDao.save(categoryToSave))
                .thenReturn(savedCategory);

        Mockito.when(categoryMapper.toResponseDto(savedCategory))
                .thenReturn(responseDto);

        CategoryResponseDto result = categoryService.create(request);

        assertEquals(1L, result.id());
        assertEquals("Java", result.name());
        assertEquals(10L, result.parentId());

        Mockito.verify(categoryDao).existsByName("Java");
        Mockito.verify(categoryMapper).toEntity(request);
        Mockito.verify(categoryDao).findById(10L);
        Mockito.verify(categoryDao).save(categoryToSave);
        Mockito.verify(categoryMapper).toResponseDto(savedCategory);
    }


    @Test
    @DisplayName("Должен выбросить исключение при создании, если категория с таким названием уже существует")
    void create_shouldThrowException_whenNameAlreadyExists() {
        CategoryCreateRequestDto request = new CategoryCreateRequestDto(
                "Программирование",
                null
        );

        Mockito.when(categoryDao.existsByName("Программирование"))
                .thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> categoryService.create(request)
        );

        Mockito.verify(categoryDao).existsByName("Программирование");
        Mockito.verify(categoryDao, Mockito.never()).save(Mockito.any(Category.class));
        Mockito.verifyNoInteractions(categoryMapper);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при создании, если родительская категория не найдена")
    void create_shouldThrowException_whenParentCategoryNotFound() {
        CategoryCreateRequestDto request = new CategoryCreateRequestDto(
                "Java",
                99L
        );

        Category categoryToSave = new Category();
        categoryToSave.setName("Java");

        Mockito.when(categoryDao.existsByName("Java"))
                .thenReturn(false);

        Mockito.when(categoryMapper.toEntity(request))
                .thenReturn(categoryToSave);

        Mockito.when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.create(request)
        );

        Mockito.verify(categoryDao).existsByName("Java");
        Mockito.verify(categoryMapper).toEntity(request);
        Mockito.verify(categoryDao).findById(99L);
        Mockito.verify(categoryDao, Mockito.never()).save(Mockito.any(Category.class));
    }


    @Test
    @DisplayName("Должен вернуть список корневых категорий")
    void getRootCategories_shouldReturnRootCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Художественная литература");
        category1.setParent(null);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Научная литература");
        category2.setParent(null);

        List<Category> categories = List.of(category1, category2);

        CategoryResponseDto responseDto1 = new CategoryResponseDto(
                1L,
                "Художественная литература",
                null
        );

        CategoryResponseDto responseDto2 = new CategoryResponseDto(
                2L,
                "Научная литература",
                null
        );

        List<CategoryResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(categoryDao.findRootCategories())
                .thenReturn(categories);

        Mockito.when(categoryMapper.toResponseDtoList(categories))
                .thenReturn(responseDtos);

        List<CategoryResponseDto> result = categoryService.getRootCategories();

        assertEquals(2, result.size());
        assertEquals("Художественная литература", result.get(0).name());
        assertEquals("Научная литература", result.get(1).name());

        Mockito.verify(categoryDao).findRootCategories();
        Mockito.verify(categoryMapper).toResponseDtoList(categories);
    }


    @Test
    @DisplayName("Должен вернуть подкатегории, если родительская категория существует")
    void getByParentId_shouldReturnSubcategories_whenParentExists() {
        Category parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Программирование");

        Category subcategory1 = new Category();
        subcategory1.setId(2L);
        subcategory1.setName("Java");
        subcategory1.setParent(parentCategory);

        Category subcategory2 = new Category();
        subcategory2.setId(3L);
        subcategory2.setName("Spring");
        subcategory2.setParent(parentCategory);

        List<Category> subcategories = List.of(subcategory1, subcategory2);

        CategoryResponseDto responseDto1 = new CategoryResponseDto(
                2L,
                "Java",
                1L
        );

        CategoryResponseDto responseDto2 = new CategoryResponseDto(
                3L,
                "Spring",
                1L
        );

        List<CategoryResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(categoryDao.findById(1L))
                .thenReturn(Optional.of(parentCategory));

        Mockito.when(categoryDao.findByParentId(1L))
                .thenReturn(subcategories);

        Mockito.when(categoryMapper.toResponseDtoList(subcategories))
                .thenReturn(responseDtos);

        List<CategoryResponseDto> result = categoryService.getByParentId(1L);

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).name());
        assertEquals("Spring", result.get(1).name());

        Mockito.verify(categoryDao).findById(1L);
        Mockito.verify(categoryDao).findByParentId(1L);
        Mockito.verify(categoryMapper).toResponseDtoList(subcategories);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException, если родительская категория не найдена")
    void getByParentId_shouldThrowException_whenParentCategoryNotFound() {
        Mockito.when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.getByParentId(99L)
        );

        Mockito.verify(categoryDao).findById(99L);
        Mockito.verify(categoryDao, Mockito.never()).findByParentId(99L);
        Mockito.verifyNoInteractions(categoryMapper);
    }


    @Test
    @DisplayName("Должен обновить категорию без родителя, если категория существует и название уникальное")
    void update_shouldUpdateCategory_whenCategoryExistsAndNameIsUniqueAndParentIsNull() {
        CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(
                "Новое название",
                null
        );

        Category category = new Category();
        category.setId(1L);
        category.setName("Старое название");
        category.setParent(null);

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Новое название");
        updatedCategory.setParent(null);

        CategoryResponseDto responseDto = new CategoryResponseDto(
                1L,
                "Новое название",
                null
        );

        Mockito.when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        Mockito.when(categoryDao.findByName("Новое название"))
                .thenReturn(Optional.empty());

        Mockito.when(categoryDao.update(category))
                .thenReturn(updatedCategory);

        Mockito.when(categoryMapper.toResponseDto(updatedCategory))
                .thenReturn(responseDto);

        CategoryResponseDto result = categoryService.update(1L, request);

        assertEquals(1L, result.id());
        assertEquals("Новое название", result.name());
        assertNull(result.parentId());

        Mockito.verify(categoryDao).findById(1L);
        Mockito.verify(categoryDao).findByName("Новое название");
        Mockito.verify(categoryDao).update(category);
        Mockito.verify(categoryMapper).toResponseDto(updatedCategory);
    }


    @Test
    @DisplayName("Должен обновить категорию с родителем, если категория и родитель существуют")
    void update_shouldUpdateCategory_whenCategoryExistsAndParentExists() {
        CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(
                "Java",
                10L
        );

        Category category = new Category();
        category.setId(1L);
        category.setName("Старое название");

        Category parentCategory = new Category();
        parentCategory.setId(10L);
        parentCategory.setName("Программирование");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Java");
        updatedCategory.setParent(parentCategory);

        CategoryResponseDto responseDto = new CategoryResponseDto(
                1L,
                "Java",
                10L
        );

        Mockito.when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        Mockito.when(categoryDao.findByName("Java"))
                .thenReturn(Optional.empty());

        Mockito.when(categoryDao.findById(10L))
                .thenReturn(Optional.of(parentCategory));

        Mockito.when(categoryDao.update(category))
                .thenReturn(updatedCategory);

        Mockito.when(categoryMapper.toResponseDto(updatedCategory))
                .thenReturn(responseDto);

        CategoryResponseDto result = categoryService.update(1L, request);

        assertEquals(1L, result.id());
        assertEquals("Java", result.name());
        assertEquals(10L, result.parentId());

        Mockito.verify(categoryDao).findById(1L);
        Mockito.verify(categoryDao).findByName("Java");
        Mockito.verify(categoryDao).findById(10L);
        Mockito.verify(categoryDao).update(category);
        Mockito.verify(categoryMapper).toResponseDto(updatedCategory);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при обновлении, если категория не найдена")
    void update_shouldThrowException_whenCategoryNotFound() {
        CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(
                "Новое название",
                null
        );

        Mockito.when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.update(99L, request)
        );

        Mockito.verify(categoryDao).findById(99L);
        Mockito.verify(categoryDao, Mockito.never()).update(Mockito.any(Category.class));
        Mockito.verifyNoInteractions(categoryMapper);
    }


    @Test
    @DisplayName("Должен выбросить исключение при обновлении, если новое название уже существует")
    void update_shouldThrowException_whenNameAlreadyExists() {
        CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(
                "Базы данных",
                null
        );

        Category category = new Category();
        category.setId(1L);
        category.setName("Программирование");

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("Базы данных");

        Mockito.when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        Mockito.when(categoryDao.findByName("Базы данных"))
                .thenReturn(Optional.of(existingCategory));

        assertThrows(
                RuntimeException.class,
                () -> categoryService.update(1L, request)
        );

        Mockito.verify(categoryDao).findById(1L);
        Mockito.verify(categoryDao).findByName("Базы данных");
        Mockito.verify(categoryDao, Mockito.never()).update(Mockito.any(Category.class));
        Mockito.verifyNoInteractions(categoryMapper);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при обновлении, если родительская категория не найдена")
    void update_shouldThrowException_whenParentCategoryNotFound() {
        CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(
                "Java",
                99L
        );

        Category category = new Category();
        category.setId(1L);
        category.setName("Старое название");

        Mockito.when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        Mockito.when(categoryDao.findByName("Java"))
                .thenReturn(Optional.empty());

        Mockito.when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.update(1L, request)
        );

        Mockito.verify(categoryDao).findById(1L);
        Mockito.verify(categoryDao).findByName("Java");
        Mockito.verify(categoryDao).findById(99L);
        Mockito.verify(categoryDao, Mockito.never()).update(Mockito.any(Category.class));
    }

    @Test
    @DisplayName("Должен удалить категорию, если она существует")
    void delete_shouldDeleteCategory_whenCategoryExists() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Программирование");

        Mockito.when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.delete(1L);

        Mockito.verify(categoryDao).findById(1L);
        Mockito.verify(categoryDao).delete(category);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при удалении, если категория не найдена")
    void delete_shouldThrowException_whenCategoryNotFound() {
        Mockito.when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.delete(99L)
        );

        Mockito.verify(categoryDao).findById(99L);
        Mockito.verify(categoryDao, Mockito.never()).delete(Mockito.any(Category.class));
    }
}