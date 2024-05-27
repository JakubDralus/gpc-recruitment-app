package com.genpt.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.api.dto.ProductDTO;
import com.genpt.api.exception.EmptyResourceException;
import com.genpt.api.exception.ResourceNotFoundException;
import com.genpt.api.exception.XmlParsingException;
import com.genpt.api.mapper.ProductMapper;
import com.genpt.api.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductMapper productMapper;
    private static final String XML_FILE_NAME = "products.xml";
    private static final Path XML_FILE_PATH = Path.of("./resources/products.xml");
    private static String xmlFileContent = null;
    private static List<Product> products = null;
    
    
    public int readXmlFile() {
        parseXmlFile();
        return products.size();
    }
    
    public List<ProductDTO> getAllProducts() {
        if (products == null) {
            throw new EmptyResourceException("The XML file was not yet parsed.");
        }
        return products.stream().map(productMapper).toList();
    }
    

    public List<ProductDTO> getProductByName(String productName) {
        if (products == null) {
            throw new EmptyResourceException("The XML file was not yet parsed.");
        }
        
        List<Product> foundProducts = products.stream()
                .filter(product -> product.getName().equals(productName))
                .toList();
        
        if (foundProducts.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with name: " + productName);
        }

        return foundProducts.stream().map(productMapper).toList();
    }
    
    private void parseXmlFile() {
        try {
            byte[] fileContent = extractFileBytes();
            xmlFileContent = new String(fileContent);
            
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent);
            XmlMapper xmlMapper = new XmlMapper();
            TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
            products = xmlMapper.readValue(byteArrayInputStream, productsTypeRef);
        }
        catch (IOException e) {
            String errorMessage = "Error while parsing XML file: " + XML_FILE_NAME;
            throw new XmlParsingException(errorMessage, e);
        }
    }
    
    private byte[] extractFileBytes() {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(XML_FILE_PATH.toString()));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException e) {
            String errorMessage = "Error while reading XML file bytes: " + XML_FILE_NAME;
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    // ------------------ extra stuff ------------------
    
    public String getXmlFileContent() {
        if (xmlFileContent == null || xmlFileContent.isBlank()) {
            throw new EmptyResourceException("The XML file was not yet parsed.");
        }
        return xmlFileContent;
    }
    
    public void updateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyResourceException("File is empty");
        }
        if (!Objects.equals(file.getContentType(), MediaType.APPLICATION_XML_VALUE)) {
            throw new InvalidParameterException(
                    String.format("Wrong file content type: '%s', it should be %s.",
                            file.getContentType(), MediaType.APPLICATION_XML_VALUE)
            );
        }
        
        try {
            Files.copy(file.getInputStream(), XML_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            String errorMessage = "Error while reading XML file: " + XML_FILE_NAME;
            throw new XmlParsingException(errorMessage, e);
        }
    }
}
