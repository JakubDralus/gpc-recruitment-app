package com.genpt.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.app.dto.ProductDTO;
import com.genpt.app.exception.ResourceNotFoundException;
import com.genpt.app.mapper.ProductMapper;
import com.genpt.app.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ResourceLoader resourceLoader;
    private static final String PRODUCTS_XML_PATH = "src/main/resources/products.xml";
    private final ProductMapper productMapper;
    private static List<Product> products = null;
    
    public int readXmlFile() {
        parseXmlFile(PRODUCTS_XML_PATH);
        return products.size();
    }
    
    public List<ProductDTO> getAllProducts() {
        if (products == null) {
            throw new RuntimeException("The XML file was not yet parsed.");
        }
        return products.stream()
                .map(productMapper)
                .toList();
    }
    
//    @Cacheable
    public List<ProductDTO> getProductByName(String productName) {
        if (products == null) {
            throw new RuntimeException("The XML file was not yet parsed.");
        }
        
        List<Product> foundProducts = products.stream()
                .filter(product -> product.getName().equals(productName))
                .toList();
        
        if (foundProducts.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with name: " + productName);
        }

        return foundProducts.stream()
                .map(productMapper)
                .toList();
    }
    
    private void parseXmlFile(String path) {
        try {
            File xmlFile = resourceLoader.getResource("classpath:products.xml").getFile();
            XmlMapper xmlMapper = new XmlMapper();
            TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
            products = xmlMapper.readValue(xmlFile, productsTypeRef);
        }
        catch (IOException e) {
            String errorMessage = "Error while reading XML file: " + path;
            throw new RuntimeException(errorMessage, e);
        }
    }
}
