package com.genpt.app.dto;

import lombok.Data;

import java.util.List;

/**
 * For making a response JSON that will look like this: <br>
 * <pre>
 * {
 *    "products": [
 *      {
 *        product1
 *      },
 *      ...
 *    ]
 * }
 * </pre>
 */
@Data
public class ProductWrapperDTO {
    private final List<ProductDTO> products;
}
//todo: consider just using a Map.of()