package com.genpt.api;

import com.genpt.api.dto.ProductDTO;
import com.genpt.api.exception.EmptyResourceException;
import com.genpt.api.exception.ResourceNotFoundException;
import com.genpt.api.mapper.ProductMapper;
import com.genpt.api.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	
	@Mock
	private ProductMapper productMapper;
	
	@InjectMocks
	private ProductService productService;
	
	private static final Path TEST_XML_PATH = Path.of("src/test/resources/products.xml");
	private static final Path BACKUP_XML_PATH = Path.of("src/test/resources/products_backup.xml");
	
	@BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException  {
		// Use reflection to set the xml file path to test path
		Field xmlFilePathField = ProductService.class.getDeclaredField("XML_FILE_PATH");
		xmlFilePathField.setAccessible(true);
		xmlFilePathField.set(productService, TEST_XML_PATH);
		testReadXmlFile();
	}
	
	@Test
	void testReadXmlFile()  {
		int numOfRecords = productService.readXmlFile();
		assertEquals(3, numOfRecords);
	}
	
	@Test
	void testGetAllProducts() {
		when(productMapper.apply(any())).thenReturn(new ProductDTO()); // Mock mapper
		
		List<ProductDTO> productDTOs = productService.getAllProducts();
		
		assertEquals(3, productDTOs.size());
		verify(productMapper, times(3)).apply(any());
	}
	
	@Test
	void testGetProductByName() {
		String productName = "apple";
		List<ProductDTO> productDTOs = productService.getProductByName(productName);
		assertEquals(1, productDTOs.size());
	}
	
	@Test
	void testGetProductByName_NotFound() {
		String productName = "nonexistent";
		assertThrows(ResourceNotFoundException.class, () -> productService.getProductByName(productName));
	}
	
	@Test
	void testGetXmlFileContent() {
		String xmlFileContent = productService.getXmlFileContent();
		assertNotNull(xmlFileContent);
	}
	@Test
	void testUpdateFile_EmptyFile() {
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(true);
		
		assertThrows(EmptyResourceException.class, () -> productService.updateFile(file, TEST_XML_PATH));
	}
	
	@Test
	void testUpdateFile_InvalidContentType() {
		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(false);
		when(file.getContentType()).thenReturn("text/plain");
		
		assertThrows(InvalidParameterException.class, () -> productService.updateFile(file, TEST_XML_PATH));
	}
	
	@Test
	void testUpdateFile() throws IOException {
		// Create a backup of the original file
		Files.copy(TEST_XML_PATH, BACKUP_XML_PATH, StandardCopyOption.REPLACE_EXISTING);
		
		// Initialize the service with mocks
		ProductMapper productMapper = mock(ProductMapper.class);
		productService = new ProductService(productMapper);
		
		// Prepare the mock MultipartFile
		MultipartFile file = mock(MultipartFile.class);
		InputStream inputStream = new ByteArrayInputStream(
				"<Products><Product id=\"4\"><Name>banana</Name></Product></Products>".getBytes());
		when(file.isEmpty()).thenReturn(false);
		when(file.getContentType()).thenReturn("application/xml");
		when(file.getInputStream()).thenReturn(inputStream);
		
		// Perform the file update
		productService.updateFile(file, TEST_XML_PATH);
		
		// Verify interactions with the mock
		verify(file, times(1)).isEmpty();
		verify(file, times(1)).getContentType();
		verify(file, times(1)).getInputStream();
		
		// Verify that the file content has been updated
		String updatedContent = Files.readString(TEST_XML_PATH);
		Assertions.assertTrue(updatedContent.contains("banana"), "The file content should contain the new product.");
	
		// Restore the original file from the backup
		Files.copy(BACKUP_XML_PATH, TEST_XML_PATH, StandardCopyOption.REPLACE_EXISTING);
		Files.deleteIfExists(BACKUP_XML_PATH);
		inputStream.close();
	}
}
