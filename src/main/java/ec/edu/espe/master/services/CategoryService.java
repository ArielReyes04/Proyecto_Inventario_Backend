package ec.edu.espe.master.services;

import ec.edu.espe.master.dto.request.CategoryRequest;
import ec.edu.espe.master.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(UUID id, CategoryRequest request);
    void deleteCategory(UUID id);
    CategoryResponse toggleActive(UUID id);
}
