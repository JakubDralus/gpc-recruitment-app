package com.genpt.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.genpt.api.model.Product;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@AllArgsConstructor
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	private final ResourceLoader resourceLoader;
	
	@PostConstruct
	public void dupa() throws IOException {
		Resource resource = resourceLoader.getResource("classpath:products.xml");
		XmlMapper xmlMapper = new XmlMapper();
		TypeReference<List<Product>> productsTypeRef = new TypeReference<>() {};
		List<Product> products = xmlMapper.readValue(resource.getFile(), productsTypeRef);
		for (Product p : products) {
			System.out.println(p);
		}
	}
}
