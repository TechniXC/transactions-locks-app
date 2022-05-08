package ru.jpoint.transactionslocksapp.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jpoint.transactionslocksapp.entities.ScheduledTask;
import ru.jpoint.transactionslocksapp.repository.ScheduledTasksRepository;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisLockProvider {

    private final RedisTemplate<String, String> redisTemplate;

    public synchronized void tryToAcquireBadLock(String id, Duration ttl) {
        try {
            while (isLocked(id)) {
                wait(100);
            }
            setLock(id, ttl);
        } catch (InterruptedException interruptedException) {
            log.error("Shit happens...");
        }
    }

    public Boolean tryToAcquireLockForScheduling(String id, Duration ttl) {
        return redisTemplate.opsForValue().setIfAbsent(id,"locked", ttl);
    }

    public void setLock(String id, Duration ttl) {
        redisTemplate.opsForValue().set(id, "locked", ttl);
    }

    public void releaseLock(String id) {
        redisTemplate.opsForValue().getAndDelete(id);
    }

    public Boolean isLocked(String id) {
        return Objects.nonNull(redisTemplate.opsForValue().get(id));
    }
}
