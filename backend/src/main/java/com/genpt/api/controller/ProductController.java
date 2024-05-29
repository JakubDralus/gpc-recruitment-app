package com.genpt.api.controller;

import com.genpt.api.dto.ProductDTO;
import com.genpt.api.service.ProductService;
import com.genpt.api.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Controller class for managing product-related HTTP requests.
 * @see ProductService
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Endpoint for reading XML file and getting the number of records.
     * @return JSON with a message indicating successful parsing and the number of records.
     * @see ApiResponse
     */
    @GetMapping("/read-file")
    public ApiResponse<?> readXmlFileAndGetProductsLength() {
        int numOfRecords = productService.readXmlFile();
        
        return ApiResponse.builder()
                .message("File successfully parsed, number of records in the file: " + numOfRecords)
                .build();
    }
    
    /**
     * Endpoint for fetching all products in JSON format.
     * @return JSON with a message indicating successful retrieval and the list of products.
     * @see ApiResponse
     */
    @GetMapping("/all")
    public ApiResponse<?> getAllProductsJSON() {
        List<ProductDTO> allProducts = productService.getAllProducts();
        
        return ApiResponse.builder()
                .message("Fetched all records from the file.")
                .data(Map.of("products", allProducts))
                .build();
    }
    
    /**
     * Endpoint for fetching products by name.
     * @param name The name of the product to search for.
     * @return JSON with a message indicating successful retrieval and the list of products matching the name.
     * @see ApiResponse
     */
    @GetMapping("/{name}")
    public ApiResponse<List<ProductDTO>> getProductsByName(@PathVariable String name) {
        List<ProductDTO> productByName = productService.getProductByName(name);
        
        return ApiResponse.<List<ProductDTO>>builder()
                .message(String.format("Fetched all records matching name: '%s'", name))
                .data(productByName)
                .build();
    }
    
    /**
     * Endpoint for getting the XML file content as application/xml.
     */
    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String getXmlFileContent() {
        return productService.getXmlFileContent();
    }
    
    /**
     * Endpoint for updating the XML file.
     * @param file The updated XML file.
     * @return ApiResponse indicating successful file update.
     * @see ApiResponse
     */
    @PutMapping("/update-file")
    public ApiResponse<?> updateXmlFile(@RequestParam("file") MultipartFile file) {
        productService.updateFile(file, productService.getXML_FILE_PATH());
        return ApiResponse.builder()
                .message("File successfully updated.")
                .build();
    }
}
