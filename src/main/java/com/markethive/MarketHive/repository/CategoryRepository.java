package com.markethive.MarketHive.repository;

import com.markethive.MarketHive.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Integer parentId);
    boolean existsByName(String name);
}
