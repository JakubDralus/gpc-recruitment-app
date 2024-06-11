package com.genpt.api;

import com.genpt.api.dto.ProductDTO;
import com.genpt.api.exception.EmptyResourceException;
import com.genpt.api.mapper.ProductMapper;
import com.genpt.api.service.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	@Mock
	private static ResourceLoader resourceLoader;
	private static ProductService productService;
	
	@Value("${files.xml.products}")
	private static String XML_FILE_NAME;
	private static File tempFile;
	private static final String xmlContent;
	static {
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
                        <Name>test</Name>
                        <Category>dish</Category>
                        <PartNumberNR>9999-E7R-Q-M-K287B-YH</PartNumberNR>
                        <CompanyName>HomeHome</CompanyName>
                        <Active>true</Active>
                    </Product>
                </Products>
                """;
	}
	
	@BeforeAll
	static void beforeAll() throws Exception {
		// Initialize mocks
		MockitoAnnotations.openMocks(ProductServiceTest.class);
		
		// Manually initialize the mock for the static resourceLoader
		resourceLoader = mock(ResourceLoader.class);
		ProductMapper productMapper = new ProductMapper(); // this is actual mapper but the resourceLoader is mocked
		productService = new ProductService(productMapper, resourceLoader);
		
		// mock resource loader behaviour
		Resource resource = mock(Resource.class);
		tempFile = File.createTempFile("test", ".xml");
		tempFile.deleteOnExit();
		Files.writeString(tempFile.toPath(), xmlContent);
		InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
		
		when(resourceLoader.getResource(anyString())).thenReturn(resource);
		when(resource.getFile()).thenReturn(tempFile);
		when(resource.getInputStream()).thenReturn(inputStream);
	}
	
	@Test
	void testReadXmlFile() {
		int productCount = productService.readXmlFile(XML_FILE_NAME);
		assertEquals(3, productCount);
	}
	
	@Test
	void testGetAllProducts() {
		List<ProductDTO> expected = List.of(
				new ProductDTO(1L, "apple", "fruit",
						"2303-E1A-G-M-W209B-VM", "FruitsAll", true),
				new ProductDTO(2L, "orange", "fruit",
						"5603-J1A-G-M-W982F-PO", "FruitsAll", false),
				new ProductDTO(3L, "test", "dish",
						"9999-E7R-Q-M-K287B-YH", "HomeHome", true)
		);
		
		List<ProductDTO> productDTOs = productService.getAllProducts(XML_FILE_NAME);
		
		assertEquals(3, productDTOs.size());
		assertEquals(expected, productDTOs);
	}
	
	@Test
	void testGetProductByName() {
		ProductDTO appleDTO = new ProductDTO(1L, "apple", "fruit",
				"2303-E1A-G-M-W209B-VM", "FruitsAll", true);
		
		List<ProductDTO> productDTOs = productService.getProductByName(XML_FILE_NAME, "apple");
		
		assertEquals(1, productDTOs.size());
		assertEquals(appleDTO, productDTOs.get(0));
	}
	
	
	// --- extra ---
	
	@Test
	void testGetXmlFileContent() {
		String fileContent = productService.getXmlFileContent(XML_FILE_NAME);
        assertEquals(fileContent, xmlContent);
	}
	
	@Test
	void testUpdateFileContent() throws Exception {
		byte[] content = xmlContent.getBytes(StandardCharsets.UTF_8);
		MockMultipartFile file = new MockMultipartFile("file", "products.xml",
				MediaType.APPLICATION_XML_VALUE, content);
		
		productService.updateFile(file, XML_FILE_NAME);
		
		// Verify that the file content was updated correctly
		String updatedContent = Files.readString(tempFile.toPath());
		assertEquals(xmlContent, updatedContent);
	}
	
	@Test
	void testUpdateFileEmpty() {
		MockMultipartFile file = new MockMultipartFile("file", "test.xml",
				"application/xml", new byte[0]);
		
		Exception exception = assertThrows(
				EmptyResourceException.class, () -> productService.updateFile(file, XML_FILE_NAME)
		);
		
		String expectedMessage = "File is empty";
		String actualMessage = exception.getMessage();
		
		// Assert the exception message
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void testUpdateFileWrongContentType() {
		MockMultipartFile file = new MockMultipartFile("file", "test.txt",
				"text/plain", "Test content".getBytes());
		
		Exception exception = assertThrows(
				InvalidParameterException.class, () -> productService.updateFile(file, XML_FILE_NAME)
		);
		
		String expectedMessage = "Wrong file content type: 'text/plain', it should be application/xml or text/xml.";
		String actualMessage = exception.getMessage();
		
		// Assert the exception message
		assertEquals(expectedMessage, actualMessage);
	}
}
