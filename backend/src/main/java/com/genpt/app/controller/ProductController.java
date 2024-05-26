package com.genpt.app.controller;

import com.genpt.app.dto.ProductDTO;
import com.genpt.app.dto.ProductWrapperDTO;
import com.genpt.app.service.ProductService;
import com.genpt.app.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/read-file")
    public ApiResponse<?> readXmlFileAndGetProductsLength() {
        int numOfRecords = productService.readXmlFile();
        
        return ApiResponse.builder()
                .message("File successfully parsed, number of records in the file: " + numOfRecords)
                .build();
    }
    
    @GetMapping("/all")
    public ApiResponse<ProductWrapperDTO> getAllProductsJSON() {
        List<ProductDTO> allProducts = productService.getAllProducts();
        
        return ApiResponse.<ProductWrapperDTO>builder()
                .message("Fetched all records from the file.")
                .data(new ProductWrapperDTO(allProducts))
                .build();
    }

    // assuming the product name is not unique
    @GetMapping("/{name}")
    public ApiResponse<List<ProductDTO>> getProductsByName(@PathVariable String name) {
        List<ProductDTO> productByName = productService.getProductByName(name);
        
        return ApiResponse.<List<ProductDTO>>builder()
                .message(String.format("Fetched all records matching name: '%s'", name))
                .data(productByName)
                .build();
    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<Integer> uploadXml(@RequestBody String xmlContent) {
//        int recordCount = productService.processXml(xmlContent);
//        return ResponseEntity.ok(recordCount);
//    }
}