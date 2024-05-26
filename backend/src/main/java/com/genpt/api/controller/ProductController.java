package com.genpt.api.controller;

import com.genpt.api.dto.ProductDTO;
import com.genpt.api.service.ProductService;
import com.genpt.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ApiResponse<?> getAllProductsJSON() {
        List<ProductDTO> allProducts = productService.getAllProducts();
        
        return ApiResponse.builder()
                .message("Fetched all records from the file.")
                .data(Map.of("products", allProducts))
                .build();
    }

    // assuming the product name might not be unique
    @GetMapping("/{name}")
    public ApiResponse<List<ProductDTO>> getProductsByName(@PathVariable String name) {
        List<ProductDTO> productByName = productService.getProductByName(name);
        
        return ApiResponse.<List<ProductDTO>>builder()
                .message(String.format("Fetched all records matching name: '%s'", name))
                .data(productByName)
                .build();
    }
    
    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String getXmlFileContent() {
        return productService.getXmlFileContent();
    }
    
    @PostMapping("/upload")
    public ApiResponse<?> uploadXmlFile(@RequestBody String file) {
        int numOfRecords = productService.readXmlFile(file);
        return ApiResponse.builder()
                .message("File successfully parsed, number of records in the file: " + numOfRecords)
                .build();
    }
}