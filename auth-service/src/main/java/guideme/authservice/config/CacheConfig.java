package guideme.authservice.config;

import guideme.authservice.service.auth.StatePayload;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, StatePayload> oauthStateCache(JCacheCacheManager cm) {
        CacheManager jcm = cm.getCacheManager();
        assert jcm != null;
        Cache<String, StatePayload> cache = jcm.getCache("oauthState");
        if (cache == null) {
            cache = jcm.createCache("oauthState",
                    new MutableConfiguration<String, StatePayload>().setExpiryPolicyFactory(
                                    CreatedExpiryPolicy.factoryOf(new javax.cache.expiry.Duration(TimeUnit.MINUTES, 5)))
                            .setStoreByValue(false).setStatisticsEnabled(true));
        }
        return cache;
    }

    @Bean
    public JCacheCacheManager jCacheCacheManager() {
        return new JCacheCacheManager();
    }
}
