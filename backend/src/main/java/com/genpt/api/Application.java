package com.genpt.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.api.exception.XmlParsingException;
import com.genpt.api.model.Product;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@AllArgsConstructor
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	
	@PostConstruct
	public void onStart() throws IOException {
		try {
			FileInputStream inputStream = new FileInputStream("./resources/products.xml");
			XmlMapper xmlMapper = new XmlMapper();
			TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
			var products = xmlMapper.readValue(inputStream, productsTypeRef);
			
			for (Product p : products) {
				System.out.println(p);
			}
			inputStream.close();
		}
		catch (IOException e) {
			String errorMessage = "Error while reading XML file: ";
			throw new XmlParsingException(errorMessage, e);
		}
		
	}
}
