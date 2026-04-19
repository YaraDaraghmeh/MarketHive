package com.markethive.MarketHive.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CategoryResponse {
    private Integer id;
    private String name;
    private Integer parentId;
    private String parentName;
}