package com.genpt.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.api.dto.ProductDTO;
import com.genpt.api.exception.EntityNotFoundException;
import com.genpt.api.exception.ResourceNotInitializedException;
import com.genpt.api.exception.XmlParsingException;
import com.genpt.api.mapper.ProductMapper;
import com.genpt.api.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private static final String DEFAULT_XML_FILENAME = "products.xml";
    private final ResourceLoader resourceLoader;
    private final ProductMapper productMapper;
    private static List<Product> products = null;
    private static String xmlFileContent = null;
    
    
    public int readXmlFile() {
        parseDefaultXmlFile(DEFAULT_XML_FILENAME);
        return products.size();
    }
    
    public List<ProductDTO> getAllProducts() {
        if (products == null) {
            throw new ResourceNotInitializedException("The XML file was not yet parsed.");
        }
        return products.stream().map(productMapper).toList();
    }
    
//    @Cacheable
    public List<ProductDTO> getProductByName(String productName) {
        if (products == null) {
            throw new ResourceNotInitializedException("The XML file was not yet parsed.");
        }
        
        List<Product> foundProducts = products.stream()
                .filter(product -> product.getName().equals(productName))
                .toList();
        
        if (foundProducts.isEmpty()) {
            throw new EntityNotFoundException("Product not found with name: " + productName);
        }

        return foundProducts.stream().map(productMapper).toList();
    }
    
    private void parseDefaultXmlFile(String filename) {
        try {
            File xmlFile = resourceLoader.getResource("classpath:" + filename).getFile();
            Path xmlFilePath = xmlFile.toPath();
            XmlMapper xmlMapper = new XmlMapper();
            xmlFileContent = Files.readString(xmlFilePath);
            TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
            products = xmlMapper.readValue(xmlFileContent, productsTypeRef);
        }
        catch (IOException e) {
            String errorMessage = "Error while reading XML file: " + filename;
            throw new XmlParsingException(errorMessage, e);
        }
    }
    
    // ------------------ extra stuff ------------------
    
    public String getXmlFileContent() {
        return xmlFileContent;
    }
    
    public int readXmlFile(String xmlFile) {
        parseXmlFile(xmlFile);
        return products.size();
    }
    
    private void parseXmlFile(String xmlFile) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlFileContent = xmlFile;
            TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
            products = xmlMapper.readValue(xmlFile, productsTypeRef);
        }
        catch (IOException e) {
            String errorMessage = "Error while reading your XML.";
            throw new XmlParsingException(errorMessage, e);
        }
    }
}
