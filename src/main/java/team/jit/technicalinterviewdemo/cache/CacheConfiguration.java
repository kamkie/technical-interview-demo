package team.jit.technicalinterviewdemo.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                CacheNames.CATEGORIES,
                CacheNames.CATEGORY_DIRECTORY,
                CacheNames.LOCALIZATION_LOOKUPS,
                CacheNames.LOCALIZATION_LISTS,
                CacheNames.LOCALIZATION_MESSAGE_MAPS
        );
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
