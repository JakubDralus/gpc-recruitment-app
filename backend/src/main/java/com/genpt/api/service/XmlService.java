package com.genpt.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.api.exception.XmlParsingException;
import com.genpt.api.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class XmlService {
    
    private final ResourceLoader resourceLoader;
    private static final String PRODUCTS_XML_PATH = "classpath:products.xml";
    
    public List<Product> parseXmlFile(String path) {
        try {
            File xmlFile = resourceLoader.getResource("classpath:products.xml").getFile();
            XmlMapper xmlMapper = new XmlMapper();
            TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
            return xmlMapper.readValue(xmlFile, productsTypeRef);
        }
        catch (IOException e) {
            String errorMessage = "Error while reading XML file: " + path;
            throw new XmlParsingException(errorMessage, e);
        }
    }
}
