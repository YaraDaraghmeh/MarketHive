package com.markethive.MarketHive.services.impl;


import com.markethive.MarketHive.dto.request.CategoryRequest;
import com.markethive.MarketHive.dto.response.CategoryResponse;
import com.markethive.MarketHive.entity.Category;
import com.markethive.MarketHive.exception.BadRequestException;
import com.markethive.MarketHive.exception.ResourceNotFoundException;
import com.markethive.MarketHive.repository.CategoryRepository;
import com.markethive.MarketHive.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category already exists: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", String.valueOf(request.getParentId())));
            category.setParent(parent);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse getById(Integer id) {
        return mapToResponse(findById(id));
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getChildren(Integer parentId) {
        return categoryRepository.findByParentId(parentId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = findById(id);
        category.setName(request.getName());

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", String.valueOf(request.getParentId())));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Integer id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }

    private Category findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", String.valueOf(id)));
    }

    private CategoryResponse mapToResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .parentName(c.getParent() != null ? c.getParent().getName() : null)
                .build();
    }
}
