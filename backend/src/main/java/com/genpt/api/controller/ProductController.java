package com.genpt.api.controller;

import com.genpt.api.dto.ProductDTO;
import com.genpt.api.service.ProductService;
import com.genpt.api.util.ApiResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${files.xml.products}")
    private String xmlFileName;
    
    @PostConstruct
    private void test() {
        System.out.println("xml file name: " + xmlFileName);
    }
    
    /**
     * Endpoint for reading XML file and getting the number of records.
     * @return JSON with a message indicating successful parsing and the number of records.
     * @see ApiResponse
     */
    @GetMapping("/read-file")
    public ApiResponse<?> readXmlFileAndGetProductsLength() {
        int numOfRecords = productService.readXmlFile(xmlFileName);
        return ApiResponse.builder()
                .message("Number of records in the file: " + numOfRecords)
                .build();
    }
    
    /**
     * Endpoint for fetching all products in JSON format.
     * @return JSON with a message indicating successful retrieval and the list of products.
     * @see ApiResponse
     */
    @GetMapping("/all")
    public ApiResponse<?> getAllProductsJSON() {
        List<ProductDTO> allProducts = productService.getAllProducts(xmlFileName);
        return ApiResponse.builder()
                .message("Fetched all records from the file.")
                .data(Map.of("products", allProducts))
                .build();
    }
    
    /**
     * Endpoint for fetching products by name.
     * @param productName The name of the product to search for.
     * @return JSON with a message indicating successful retrieval and the list of products matching the name.
     * @see ApiResponse
     */
    @GetMapping("/{productName}")
    public ApiResponse<List<ProductDTO>> getProductsByName(@PathVariable String productName) {
        List<ProductDTO> productByName = productService.getProductByName(xmlFileName, productName);
        return ApiResponse.<List<ProductDTO>>builder()
                .message(String.format("Fetched all records matching name: '%s'", productName))
                .data(productByName)
                .build();
    }
    
    //  ---- extra ----
    
    /**
     * Endpoint for getting the XML file content as application/xml.
     * @return the xml file.
     */
    @GetMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String getXmlFileContent() {
        return productService.getXmlFileContent(xmlFileName);
    }
    
    /**
     * Endpoint for updating the XML file.
     * @param uploadedFile The updated XML file.
     * @return ApiResponse indicating successful file update.
     * @see ApiResponse
     */
    @PutMapping("/update-file")
    public ApiResponse<?> updateXmlFile(@RequestParam("file") MultipartFile uploadedFile) {
        productService.updateFile(uploadedFile, xmlFileName);
        return ApiResponse.builder()
                .message("File successfully updated.")
                .build();
    }
}
