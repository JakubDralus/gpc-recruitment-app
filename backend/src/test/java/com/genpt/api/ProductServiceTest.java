package com.genpt.api;

import com.genpt.api.dto.ProductDTO;
import com.genpt.api.exception.EmptyResourceException;
import com.genpt.api.mapper.ProductMapper;
import com.genpt.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ProductServiceTest {
	
	@Mock
	private ResourceLoader resourceLoader;
    private ProductService productService;
	private String xmlContent;
	
	@BeforeEach
	void setUp() throws Exception {
        ProductMapper productMapper = new ProductMapper(); //this is actual mapper but the resourceLoader is mocked
        productService = new ProductService(productMapper, resourceLoader);
		
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
                        <Active>false</Active>
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
		
		// we have to parse the file each test
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
		List<ProductDTO> expected = List.of(
				new ProductDTO(1L, "apple", "fruit",
						"2303-E1A-G-M-W209B-VM", "FruitsAll", true),
				new ProductDTO(2L, "orange", "fruit",
						"5603-J1A-G-M-W982F-PO", "FruitsAll", false),
				new ProductDTO(3L, "glass", "dish",
						"9999-E7R-Q-M-K287B-YH", "HomeHome", true)
		);
		
		List<ProductDTO> productDTOs = productService.getAllProducts();
		
		assertEquals(3, productDTOs.size());
		assertEquals(expected, productDTOs);
	}
	
	@Test
	void testGetProductByName() {
		ProductDTO appleDTO = new ProductDTO(1L, "apple", "fruit",
				"2303-E1A-G-M-W209B-VM", "FruitsAll", true);
		
		List<ProductDTO> productDTOs = productService.getProductByName("apple");
		
		assertEquals(1, productDTOs.size());
		assertEquals(appleDTO, productDTOs.get(0));
	}
	
	@Test
	void testGetXmlFileContent() {
		String fileContent = productService.getXmlFileContent();
		String expectedContent = xmlContent;
		
		assertEquals(fileContent, expectedContent);
	}
	
	@Test
	void testGetXmlFileContentThrows() throws NoSuchFieldException, IllegalAccessException {
		Field xmlFileContentField = ProductService.class.getDeclaredField("xmlFileContent");
		xmlFileContentField.setAccessible(true);
		xmlFileContentField.set(productService, null);
		
		assertThrows(EmptyResourceException.class, () -> productService.getXmlFileContent());
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
	
	@Test
	void testUpdateFileEmpty() {
		MockMultipartFile file = new MockMultipartFile("file", "test.xml",
				"application/xml", new byte[0]);
		
		Exception exception = assertThrows(EmptyResourceException.class, () -> productService.updateFile(file));
		
		String expectedMessage = "File is empty";
		String actualMessage = exception.getMessage();
		
		// Assert the exception message
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testUpdateFileWrongContentType() {
		MockMultipartFile file = new MockMultipartFile("file", "test.txt",
				"text/plain", "Test content".getBytes());
		
		Exception exception = assertThrows(InvalidParameterException.class, () -> productService.updateFile(file));
		
		String expectedMessage = "Wrong file content type: 'text/plain', it should be application/xml or text/xml.";
		String actualMessage = exception.getMessage();
		
		// Assert the exception message
		assertEquals(expectedMessage, actualMessage);
	}
}
