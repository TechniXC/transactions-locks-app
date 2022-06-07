package ru.jpoint.transactionslocksapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * Redis lock provider for distributed locks - theme for Discussion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisLockProvider {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean tryToAcquireLock(String id, Duration ttl) {
        return redisTemplate.opsForValue().setIfAbsent(id, "locked", ttl);
    }

    public void releaseLock(String id) {
        redisTemplate.opsForValue().getAndDelete(id);
    }
}
