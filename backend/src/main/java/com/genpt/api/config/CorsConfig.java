package com.genpt.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * Configure Cross-Origin Resource Sharing (CORS) policy to enable communication
     * between the backend Spring Boot application and a frontend web application.
     * This method allows specifying origins, HTTP methods, headers, and whether
     * credentials (e.g., cookies, HTTP authentication) should be allowed.
     * <p>
     * This method configures CORS for all paths (/**), allowing requests from a
     * specific origin (http://localhost:3000), and specifying the allowed HTTP methods
     * (GET, POST, PUT, DELETE, OPTIONS), headers (all), and whether credentials
     * (e.g., cookies, HTTP authentication) should be allowed.
     * <p>
     * Note: It's important to understand the security implications of allowing
     * cross-origin requests and ensure that the configured CORS policy meets the
     * security requirements of the application.
     *
     * @param registry the {@link CorsRegistry} containing the CORS configuration.
     * @see CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}