package com.genpt.api.mapper;

import com.genpt.api.dto.ProductDTO;

import com.genpt.api.model.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Function for mapping {@link Product} to {@link ProductDTO}.
 * @see com.genpt.api.service.ProductService
 */
@Component
public class ProductMapper implements Function<Product, ProductDTO> {

    @Override
    public ProductDTO apply(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .partNumberNR(product.getPartNumberNR())
                .companyName(product.getCompanyName())
                .active(product.isActive())
                .build();
    }
}

