package com.restaurant.apigateway.core.service.redis;

import com.restaurant.commons.core.interfaces.ICacheService;
import org.springframework.core.ParameterizedTypeReference;

public interface IRedisService extends ICacheService {
    <T> T get(String key, ParameterizedTypeReference<T> typeRef);
}
