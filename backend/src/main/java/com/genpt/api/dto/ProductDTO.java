package com.genpt.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) that is returned by the api.
 *
 * @see com.genpt.api.controller.ProductController
 * @see com.genpt.api.model.Product
 * @see com.genpt.api.mapper.ProductMapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String category;
    private String partNumberNR;
    private String companyName;
    private boolean active;
}
