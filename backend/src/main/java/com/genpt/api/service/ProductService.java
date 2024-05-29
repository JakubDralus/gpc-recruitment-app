package com.genpt.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.api.dto.ProductDTO;
import com.genpt.api.exception.EmptyResourceException;
import com.genpt.api.exception.ResourceNotFoundException;
import com.genpt.api.exception.XmlParsingException;
import com.genpt.api.mapper.ProductMapper;
import com.genpt.api.model.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    
    /** Mapper function for converting between Product and ProductDTO. */
    private final ProductMapper productMapper;
    
    /** Name of the XML file containing product data. */
    private static final String XML_FILE_NAME = "products.xml";
    
    /** Path to the XML file containing product data. <br>
     *
     *  Filepath is relative because .jar file in docker can't see classpath resources.
     *  So my solution is to make a volume with same path name here locally and on docker container.
     *  (see Dockerfile) <br>
     *
     *  Other solution is to copy whole project to docker container and run the app there.
     *  (I chose the first solution as I have very little space left on my laptop and docker is taking a lot of it.
     *  and I don't have enough time to refactor it)
     * */
    @Getter
    private final Path XML_FILE_PATH = Path.of("./resources/products.xml");
    
    /** Content of the XML file as a string. */
    private static String xmlFileContent = null;
    
    /** List of products parsed from the XML file. */
    private static List<Product> products = null;
    
    
    /**
     * Reads the XML file, parses it and returns the number of products.
     *
     * @return the number of products in the XML file.
     * @throws XmlParsingException if an error occurs while parsing the XML file.
     * @see #parseXmlFile(Path) 
     * @see #extractFileBytes(Path) 
     * @see #xmlFileContent
     * @see #products
     */
    @Cacheable(value = "products", key = "'readXmlFile'")
    public int readXmlFile() {
        parseXmlFile(XML_FILE_PATH);
        return products.size();
    }
    
    /**
     * Parses the XML file by mapping its content into a list of {@link Product} objects
     * and setting {@link #products} variable.
     * File content is obtained by {@link #extractFileBytes(Path)} method.
     *
     * @throws XmlParsingException if an error occurs while parsing the XML file.
     * @see #extractFileBytes(Path) 
     */
    private void parseXmlFile(Path filename) {
        try {
            byte[] fileContent = extractFileBytes(filename);
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
    
    /**
     * Extracts the bytes from the XML file using {@link BufferedInputStream} for efficient data extraction.
     *
     * @return XML content as a {@code byte[]};
     * @throws RuntimeException if an error occurs while reading the XML file bytes.
     */
    private byte[] extractFileBytes(Path path) {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(String.valueOf(path)));
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
            e.printStackTrace();
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
     * @param xmlFilePath path of the file that will be updated.
     * @throws EmptyResourceException      if the given file is empty.
     * @throws InvalidParameterException   if the given file is not of type XML.
     * @throws XmlParsingException         if an error occurs while updating the XML file.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void updateFile(MultipartFile file, Path xmlFilePath) {
        if (file == null || file.isEmpty()) {
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
            Files.copy(file.getInputStream(), xmlFilePath, StandardCopyOption.REPLACE_EXISTING);
            // Invalidate local data since the file has been updated
            xmlFileContent = null;
            products = null;
        }
        catch (Exception e) {
            String errorMessage = "Error while updating XML file: " + XML_FILE_NAME;
            throw new XmlParsingException(errorMessage, e);
        }
    }
}
