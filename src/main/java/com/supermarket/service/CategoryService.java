package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.exception.EntityNotFoundException;
import com.supermarket.model.Category;
import com.supermarket.repository.CategoryRepository;

import java.util.List;

/**
 * Service responsible for Category CRUD operations.
 */
public class CategoryService {

    private final CategoryRepository categoryRepo;

    public CategoryService() {
        this.categoryRepo = DataStore.getInstance().getCategoryRepository();
    }

    public void addCategory(Category category) {
        categoryRepo.save(category);
    }

    public Category getCategory(String id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy danh mục với mã: " + id));
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public void updateCategory(Category category) {
        categoryRepo.update(category);
    }

    public boolean deleteCategory(String id) {
        return categoryRepo.deleteById(id);
    }
}
