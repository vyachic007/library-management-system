package by.slava_borisov.library.dao.impl;

import by.slava_borisov.library.dao.AbstractDao;
import by.slava_borisov.library.dao.CategoryDao;
import by.slava_borisov.library.model.Category;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDaoImpl extends AbstractDao<Category, Long> implements CategoryDao {

    public CategoryDaoImpl() {
        super(Category.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        try {
            Category category = entityManager.createQuery(
                            "select c from Category c where c.name = :name", Category.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(category);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        Long count = entityManager.createQuery(
                        "select count(c) from Category c where c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findRootCategories() {
        return entityManager.createQuery(
                        "select c from Category c where c.parent is null", Category.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findByParentId(Long parentId) {
        return entityManager.createQuery(
                        "select c from Category c where c.parent.id = :parentId", Category.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }
}