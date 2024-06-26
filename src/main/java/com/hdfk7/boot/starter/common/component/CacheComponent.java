package com.hdfk7.boot.starter.common.component;

import com.hdfk7.boot.starter.common.cache.RedisCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@ConditionalOnClass({CacheManager.class, RedisConnectionFactory.class, org.springframework.data.redis.cache.RedisCacheManager.class})
public class CacheComponent {

    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(name -> name + ":")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .entryTtl(Duration.ofDays(1));
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory), defaultCacheConfig);
    }

}
