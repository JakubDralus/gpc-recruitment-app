package com.genpt.api.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that enables caching for repeatable requests in the application.
 * Caching is enabled specifically for the {@link com.genpt.api.service.ProductService} class,
 * allowing for improved performance by caching the results of repeated method calls.
 * <p>
 *
 * In this configuration, a {@link org.springframework.cache.concurrent.ConcurrentMapCacheManager}
 * bean named "products" is defined using the {@link org.springframework.context.annotation.Bean}
 * annotation. This cache manager creates concurrent hash map-based caches for storing
 * cached data in memory. The "products" cache is specifically configured to cache data
 * related to products retrieved by the {@link com.genpt.api.service.ProductService}.
 * <p>
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.cache.annotation.EnableCaching
 * @see org.springframework.cache.CacheManager
 * @see org.springframework.cache.concurrent.ConcurrentMapCacheManager
 * @see com.genpt.api.service.ProductService
 */
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products");
    }
}