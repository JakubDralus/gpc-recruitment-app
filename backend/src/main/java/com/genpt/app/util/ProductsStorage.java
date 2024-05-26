package com.genpt.app.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.app.model.ProductWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ProductsStorage {
    
    private static final String PATH = "src/main/resources/products.xml";
    
    public ProductWrapper parseProductsXml() {
        try {
            String xmlFileAsString = new String(Files.readAllBytes(Paths.get(PATH)));
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xmlFileAsString, ProductWrapper.class);
        } catch (IOException e) {
            String errorMessage = "Error while reading XML file: " + PATH;
            throw new RuntimeException(errorMessage, e);
        }
    }
}