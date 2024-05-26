package com.genpt.app.mapper;

import com.genpt.app.dto.ProductDTO;

import com.genpt.app.model.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

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

