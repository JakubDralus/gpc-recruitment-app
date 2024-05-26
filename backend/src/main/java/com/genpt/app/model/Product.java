package com.genpt.app.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

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