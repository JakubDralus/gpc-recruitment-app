package com.genpt.app.util;

import com.genpt.app.model.Product;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class XMLParser {
    
    public List<Product> parse(String xmlContent) {
        List<Product> products = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
            NodeList productNodes = document.getElementsByTagName("Product");
            for (int i = 0; i < productNodes.getLength(); i++) {
                Element productElement = (Element) productNodes.item(i);
                Product product = new Product();
                product.setName(getTagValue("Name", productElement));
                product.setCategory(getTagValue("Category", productElement));
                product.setPartNumberNR(getTagValue("PartNumberNR", productElement));
                product.setCompanyName(getTagValue("CompanyName", productElement));
                product.setActive(Boolean.parseBoolean(getTagValue("Active", productElement)));
                products.add(product);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
    
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        return nodeList.item(0).getNodeValue();
    }
}
