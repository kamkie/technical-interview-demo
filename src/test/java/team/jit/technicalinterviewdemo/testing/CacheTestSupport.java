package team.jit.technicalinterviewdemo.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public final class CacheTestSupport {

    private CacheTestSupport() {}

    public static void clearCaches(CacheManager cacheManager, String... cacheNames) {
        clearCaches(cacheManager, List.of(cacheNames));
    }

    public static void clearCaches(CacheManager cacheManager, Iterable<String> cacheNames) {
        for (String cacheName : cacheNames) {
            cache(cacheManager, cacheName).clear();
        }
    }

    public static Cache cache(CacheManager cacheManager, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        assertThat(cache).isNotNull();
        return cache;
    }
}
