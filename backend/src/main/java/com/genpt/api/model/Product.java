package com.genpt.api.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.genpt.api.service.ProductService;
import lombok.Data;

/**
 * Object representing a product form XML file.
 *
 * @see ProductService
 */
@Data
public class Product {
    
    @JacksonXmlProperty(isAttribute = true)
    private Long id;
    
    @JacksonXmlProperty(localName = "Name")
    private String name;
    
    @JacksonXmlProperty(localName = "Category")
    private String category;
    
    @JacksonXmlProperty(localName = "PartNumberNR")
    private String partNumberNR;
    
    @JacksonXmlProperty(localName = "CompanyName")
    private String companyName;
    
    @JacksonXmlProperty(localName = "Active")
    private boolean active;
}
