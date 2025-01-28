package com.dream.six.config;//package com.dpone.app.core.config;
//
//import org.ehcache.config.CacheConfiguration;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.ExpiryPolicyBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.config.units.MemoryUnit;
//import org.ehcache.jsr107.Eh107Configuration;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.cache.CacheManager;
//import javax.cache.Caching;
//import javax.cache.spi.CachingProvider;
//import java.time.Duration;
//
//@Configuration
//@EnableCaching
//public class EHConfiguration {
//
//    @Bean
//    public CacheManager getCacheManager() {
//        CachingProvider provider = Caching.getCachingProvider();
//        CacheManager cacheManager = provider.getCacheManager();
//
//        CacheConfiguration<Object, Object> configuration = CacheConfigurationBuilder
//                .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(100)
//                        .offheap(10, MemoryUnit.MB))
//                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
//                .build();
//
//        javax.cache.configuration.Configuration<Object, Object> cacheConfiguration = Eh107Configuration
//                .fromEhcacheCacheConfiguration(configuration);
//
//        cacheManager.createCache("ROLE-PERMISSIONS", cacheConfiguration);
//
//        return cacheManager;
//    }
//}
//
