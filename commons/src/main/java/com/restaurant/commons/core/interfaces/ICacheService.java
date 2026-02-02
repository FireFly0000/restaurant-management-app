package com.restaurant.commons.core.interfaces;

import java.util.concurrent.TimeUnit;

public interface ICacheService {
    <T> void set(String key, T value);
    <T> void setWithTTL(String key, T value, long timeout, TimeUnit unit);
    <T> T get(String key, Class<T> type);
    void delete(String key);
    void deleteByPattern(String pattern);
    boolean hasKey(String key);
}