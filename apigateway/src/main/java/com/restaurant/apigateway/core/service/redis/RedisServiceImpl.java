package com.restaurant.apigateway.core.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("redisService")
@Slf4j
public class RedisServiceImpl implements IRedisService{

    private final Logger _log = LoggerFactory.getLogger(RedisServiceImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisServiceImpl(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper
    ){
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public <T> void setWithTTL(String key, T value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object raw = redisTemplate.opsForValue().get(key);
        if(raw == null) return null;

        return objectMapper.convertValue(raw, type);
    }

    @Override
    public <T> T get(String key, ParameterizedTypeReference<T> typeRef) {
        Object raw = redisTemplate.opsForValue().get(key);
        if(raw == null) return null;

        return objectMapper.convertValue(raw, objectMapper.getTypeFactory().constructType(typeRef.getType()));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void deleteByPattern(String pattern) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();

        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    String key = cursor.next();
                    redisTemplate.delete(key);
                }
            } catch (Exception e) {
                _log.error(e.getMessage(), e.getCause());
            }
            return null;
        });
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
