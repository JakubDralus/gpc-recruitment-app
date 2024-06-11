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

import java.io.*;
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
    
    /** Mapper function for conversion between Product and ProductDTO. */
    private final ProductMapper productMapper;
    
    /**
     * ResourceLoader used for loading resources in the application.
     * In this class it is responsible for loading products.xml file form resources folder.
     */
    private final ResourceLoader resourceLoader;
    
    
    /**
     * Reads the XML file, parses it and returns the number of products.
     *
     * @param fileName the name of the xml file.
     * @return the number of products in the XML file.
     * @throws XmlParsingException if an error occurs while parsing the XML file.
     * @see #parseXmlFile(String fileName)
     * @see #extractFileBytes(String)
     */
    @Cacheable(value = "products", key = "'readXmlFile'")
    public int readXmlFile(String fileName) {
        return parseXmlFile(fileName).size();
    }
    
    /**
     * Reads the XML file, parses it and returns a list of all products mapped to their DTO object.
     *
     * @param fileName the name of the xml file.
     * @return a list of all products.
     * @see ProductDTO
     * @see ProductMapper
     */
    @Cacheable(value = "products", key = "'getAllProducts'")
    public List<ProductDTO> getAllProducts(String fileName) {
        List<Product> products = parseXmlFile(fileName);
        return products.stream().map(productMapper).toList();
    }
    
    /**
     * Reads the XML file, parses it and returns a list of products that match the given name
     * (assuming the name is not unique).
     *
     * @param fileName the name of the xml file.
     * @param productName the name of the product to search for.
     * @return a list of products that match the given name.
     * @throws ResourceNotFoundException if no products are found with the given name.
     */
    @Cacheable(value = "products", key = "#productName")
    public List<ProductDTO> getProductByName(String fileName, String productName) {
        List<Product> products = parseXmlFile(fileName);
        List<Product> foundProducts = products.stream()
                .filter(product -> product.getName().equals(productName))
                .toList();
        
        if (foundProducts.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with name: " + productName);
        }
        
        return foundProducts.stream().map(productMapper).toList();
    }
    
    /**
     * @param fileName the name of the xml file.
     * @return list of products in the file.
     */
    private List<Product> parseXmlFile(String fileName) {
        try {
            File xmlFile = resourceLoader.getResource("classpath:"+ fileName).getFile();
            XmlMapper xmlMapper = new XmlMapper();
            TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
            return xmlMapper.readValue(xmlFile, productsTypeRef);
        }
        catch (IOException e) {
            String errorMessage = "Error while reading XML file: " + fileName;
            System.err.println(e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    
    // ----- extra -----
    
    /**
     * Returns the content of the XML file as a string.
     *
     * @return the content of the XML file.
     * @throws EmptyResourceException if the XML file was not yet parsed or is empty.
     */
    public String getXmlFileContent(String fileName) {
        return new String(extractFileBytes(fileName));
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
     * Replaces content of the XML uloadedFile being with the new uloadedFile passed as an argument.
     * Additionally, clears the product cache.
     *
     * @param uploadedFile the new XML uloadedFile to replace the existing one.
     * @throws EmptyResourceException      if the given uloadedFile is empty.
     * @throws InvalidParameterException   if the given uloadedFile is not of type XML.
     * @throws XmlParsingException         if an error occurs while updating the XML uloadedFile.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void updateFile(MultipartFile uploadedFile, String originalFileName) {
        if (uploadedFile.isEmpty()) {
            throw new EmptyResourceException("File is empty");
        }
        
        if (!Objects.equals(uploadedFile.getContentType(), MediaType.APPLICATION_XML_VALUE)
                && !Objects.equals(uploadedFile.getContentType(), MediaType.TEXT_XML_VALUE)) {
            throw new InvalidParameterException(
                    String.format("Wrong file content type: '%s', it should be %s or %s.",
                            uploadedFile.getContentType(), MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE)
            );
        }
        
        try {
            Path path = resourceLoader.getResource("classpath:" + originalFileName).getFile().toPath();
//            System.out.println(path);
            Files.write(path, uploadedFile.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            String errorMessage = "Error while updating XML uloadedFile";
            throw new XmlParsingException(errorMessage, e);
        }
    }
}
