package by.slava_borisov.library.dao;

import by.slava_borisov.library.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {

    Optional<Category> findById(Long id);

    List<Category> findAll();

    Category save(Category category);

    Category update(Category category);

    void delete(Category category);

    void deleteById(Long id);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findRootCategories();

    List<Category> findByParentId(Long parentId);
}