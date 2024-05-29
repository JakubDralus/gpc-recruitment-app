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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductService {
    
    /** Mapper function for converting between Product and ProductDTO. */
    private final ProductMapper productMapper;
    
    /**
     * ResourceLoader used for loading resources in the application.
     * In this class it is responsible for loading products.xml file form resources folder.
     */
    private final ResourceLoader resourceLoader;
    
    /** Name of the XML file containing product data. */
    private final String XML_FILE_NAME = "products.xml";
    
    /** Content of the XML file as a string. */
    private String xmlFileContent = null;
    
    /** List of products parsed from the XML file. */
    private List<Product> products = null;
    
    
    /**
     * Reads the XML file, parses it and returns the number of products.
     *
     * @return the number of products in the XML file.
     * @throws XmlParsingException if an error occurs while parsing the XML file.
     * @see #parseXmlFile(byte[])
     * @see #extractFileBytes(String)
     * @see #xmlFileContent
     * @see #products
     */
    @Cacheable(value = "products", key = "'readXmlFile'")
    public int readXmlFile() {
        byte[] fileContent = extractFileBytes(XML_FILE_NAME);
        parseXmlFile(fileContent);
        return products.size();
    }
    
    /**
     * Parses the XML file content into a list of {@link Product} objects
     * and sets the {@link #products} variable.
     *
     * @param fileContent the content of the XML file as byte array.
     * @throws XmlParsingException if an error occurs while parsing the XML file.
     */
    private void parseXmlFile(byte[] fileContent) {
        try {
            xmlFileContent = new String(fileContent);
            XmlMapper xmlMapper = new XmlMapper();
            TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
            ByteArrayInputStream src = new ByteArrayInputStream(fileContent);
            products = xmlMapper.readValue(src, productsTypeRef);
            
            src.close();
        }
        catch (IOException e) {
            String errorMessage = "Error while parsing XML file";
            throw new XmlParsingException(errorMessage, e);
        }
    }
    
    /**
     * Extracts the bytes from the XML file using {@link ResourceLoader}.
     *
     * @return XML content as a {@code byte[]}.
     * @throws RuntimeException if an error occurs while reading the XML file bytes.
     */
    private byte[] extractFileBytes(String filename) {
        try (InputStream inputStream = resourceLoader.getResource("classpath:" + filename).getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException e) {
            String errorMessage = "Error while reading XML file bytes";
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * Returns a list of all products mapped to their DTO object.
     *
     * @return a list of all products.
     * @throws EmptyResourceException if the XML file was not yet parsed.
     * @see ProductDTO
     * @see ProductMapper
     */
    @Cacheable(value = "products", key = "'getAllProducts'")
    public List<ProductDTO> getAllProducts() {
        if (products == null) {
            throw new EmptyResourceException("The XML file was not yet parsed.");
        }
        return products.stream().map(productMapper).toList();
    }
    
    /**
     * Returns a list of products that match the given name
     * (assuming the name is not unique).
     *
     * @param productName the name of the product to search for.
     * @return a list of products that match the given name.
     * @throws EmptyResourceException   if the XML file was not yet parsed.
     * @throws ResourceNotFoundException if no products are found with the given name.
     */
    @Cacheable(value = "products", key = "#productName")
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
    
    /**
     * Returns the content of the previously parsed XML file as a string.
     *
     * @return the content of the XML file.
     * @throws EmptyResourceException if the XML file was not yet parsed or is empty.
     */
    public String getXmlFileContent() {
        if (xmlFileContent == null || xmlFileContent.isBlank()) {
            throw new EmptyResourceException("The XML file was not yet parsed.");
        }
        return xmlFileContent;
    }
    
    /**
     * Replaces content of the XML file being with the new file passed as an argument.
     * Additionally, clears the product cache.
     *
     * @param file the new XML file to replace the existing one.
     * @throws EmptyResourceException      if the given file is empty.
     * @throws InvalidParameterException   if the given file is not of type XML.
     * @throws XmlParsingException         if an error occurs while updating the XML file.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void updateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyResourceException("File is empty");
        }
        
        if (!Objects.equals(file.getContentType(), MediaType.APPLICATION_XML_VALUE)
                && !Objects.equals(file.getContentType(), MediaType.TEXT_XML_VALUE)) {
            throw new InvalidParameterException(
                    String.format("Wrong file content type: '%s', it should be %s or %s.",
                            file.getContentType(), MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE)
            );
        }
        
        try {
            String path = resourceLoader.getResource("classpath:" + XML_FILE_NAME).getFile().getPath();
//            System.out.println(path);
            Files.write(Path.of(path), file.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            
            // Invalidate local data since the file has been updated
            xmlFileContent = null;
            products = null;
        }
        catch (IOException e) {
            String errorMessage = "Error while updating XML file";
            throw new XmlParsingException(errorMessage, e);
        }
    }
}
