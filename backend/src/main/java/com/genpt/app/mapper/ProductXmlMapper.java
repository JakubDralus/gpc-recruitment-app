package com.genpt.app.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.genpt.app.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductXmlMapper {
    private static final XmlMapper xmlMapper = new XmlMapper();
    
    public Product mapToModel(String xmlContent) {
        Product product;
        try {
            product = xmlMapper.readValue(xmlContent, Product.class);
        }
        catch (JsonProcessingException e) {
            // todo add custom exception
            throw new RuntimeException("error processing XML");
        }
        return product;
    }
}
