package team.jit.technicalinterviewdemo.technical.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CachingConfiguration {

    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(false);
        cacheManager.registerCustomCache(
            CacheNames.CATEGORIES, Caffeine.newBuilder().maximumSize(50).expireAfterWrite(Duration.ofMinutes(30)).build()
        );
        cacheManager.registerCustomCache(
            CacheNames.CATEGORY_DIRECTORY, Caffeine.newBuilder().maximumSize(10).expireAfterWrite(Duration.ofMinutes(30)).build()
        );
        cacheManager.registerCustomCache(
            CacheNames.LOCALIZATION_LOOKUPS, Caffeine.newBuilder().maximumSize(1_000).expireAfterWrite(Duration.ofMinutes(15)).build()
        );
        cacheManager.registerCustomCache(
            CacheNames.LOCALIZATION_LISTS, Caffeine.newBuilder().maximumSize(100).expireAfterWrite(Duration.ofMinutes(15)).build()
        );
        cacheManager.registerCustomCache(
            CacheNames.LOCALIZATION_MESSAGE_MAPS, Caffeine.newBuilder().maximumSize(100).expireAfterWrite(Duration.ofMinutes(15)).build()
        );
        return cacheManager;
    }
}
