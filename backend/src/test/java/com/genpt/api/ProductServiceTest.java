package com.genpt.api;

import com.genpt.api.dto.ProductDTO;
import com.genpt.api.exception.EmptyResourceException;
import com.genpt.api.mapper.ProductMapper;
import com.genpt.api.model.Product;
import com.genpt.api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	
	@Mock
	private ResourceLoader resourceLoader;
	
	@Mock
	private ProductMapper productMapper;
	
	@InjectMocks
	private ProductService productService;
	
	private String xmlContent;
	
	@BeforeEach
	void setUp() throws Exception {
		xmlContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Products>
                    <Product id="1">
                        <Name>apple</Name>
                        <Category>fruit</Category>
                        <PartNumberNR>2303-E1A-G-M-W209B-VM</PartNumberNR>
                        <CompanyName>FruitsAll</CompanyName>
                        <Active>true</Active>
                    </Product>
                    <Product id="2">
                        <Name>orange</Name>
                        <Category>fruit</Category>
                        <PartNumberNR>5603-J1A-G-M-W982F-PO</PartNumberNR>
                        <CompanyName>FruitsAll</CompanyName>
                        <Active>true</Active>
                    </Product>
                    <Product id="3">
                        <Name>glass</Name>
                        <Category>dish</Category>
                        <PartNumberNR>9999-E7R-Q-M-K287B-YH</PartNumberNR>
                        <CompanyName>HomeHome</CompanyName>
                        <Active>true</Active>
                    </Product>
                </Products>
                """;
		testReadXmlFile();
	}
	
	@Test
	void testReadXmlFile() throws Exception {
		Resource resource = mock(Resource.class);
		InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
		
		when(resourceLoader.getResource(anyString())).thenReturn(resource);
		when(resource.getInputStream()).thenReturn(inputStream);
		
		int productCount = productService.readXmlFile();
		
		assertEquals(3, productCount);
		inputStream.close();
	}
	
	
	@Test
	void testGetAllProducts() {
		ProductDTO appleDTO = new ProductDTO(1L, "apple", "fruit",
				"2303-E1A-G-M-W209B-VM", "FruitsAll", true);
		ProductDTO orangeDTO = new ProductDTO(2L, "orange", "fruit",
				"5603-J1A-G-M-W982F-PO", "FruitsAll", true);
		ProductDTO glassDTO = new ProductDTO(3L, "glass", "dish",
				"9999-E7R-Q-M-K287B-YH", "HomeHome", true);
		
		when(productMapper.apply(any(Product.class))).thenReturn(appleDTO, orangeDTO, glassDTO);
		
		List<ProductDTO> productDTOs = productService.getAllProducts();
		
		assertEquals(3, productDTOs.size());
	}
	
	@Test
	void testGetProductByName() {
		ProductDTO appleDTO = new ProductDTO(1L, "apple", "fruit",
				"2303-E1A-G-M-W209B-VM", "FruitsAll", true);
		
		when(productMapper.apply(any(Product.class))).thenReturn(appleDTO);
		
		List<ProductDTO> productDTOs = productService.getProductByName("apple");
		
		assertEquals(1, productDTOs.size());
		assertEquals(appleDTO, productDTOs.get(0));
	}
	
	@Test
	void testUpdateFileFields() throws Exception {
		byte[] content = xmlContent.getBytes(StandardCharsets.UTF_8);
		MockMultipartFile file = new MockMultipartFile("file", "products.xml",
				MediaType.APPLICATION_XML_VALUE, content);
		
		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(File.createTempFile("test", ".xml"));
		
		productService.updateFile(file);
		
		verify(resourceLoader, times(2)).getResource(anyString());
		
		assertThrows(EmptyResourceException.class, () -> productService.getXmlFileContent());
		
		// Use reflection to access static fields for verification
		Field xmlFileContentField = ProductService.class.getDeclaredField("xmlFileContent");
		xmlFileContentField.setAccessible(true);
		assertNull(xmlFileContentField.get(productService));
		
		Field productsField = ProductService.class.getDeclaredField("products");
		productsField.setAccessible(true);
		assertNull(productsField.get(productService));
	}
	
	@Test
	void testUpdateFileContent() throws Exception {
		byte[] content = xmlContent.getBytes(StandardCharsets.UTF_8);
		MockMultipartFile file = new MockMultipartFile("file", "products.xml",
				MediaType.APPLICATION_XML_VALUE, content);
		
		// Create a temporary file to simulate the resource
		File tempFile = File.createTempFile("test", ".xml");
		tempFile.deleteOnExit();
		Path tempFilePath = tempFile.toPath();
		
		Resource resource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(tempFile);
		
		productService.updateFile(file);
		
		// Verify that the file content was updated correctly
		String updatedContent = Files.readString(tempFilePath);
		assertEquals(xmlContent, updatedContent);
	}
}
