package com.markethive.MarketHive.services;

import com.markethive.MarketHive.dto.request.CategoryRequest;
import com.markethive.MarketHive.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse getById(Integer id);
    List<CategoryResponse> getAll();
    List<CategoryResponse> getRootCategories();
    List<CategoryResponse> getChildren(Integer parentId);
    CategoryResponse update(Integer id, CategoryRequest request);
    void delete(Integer id);
}
