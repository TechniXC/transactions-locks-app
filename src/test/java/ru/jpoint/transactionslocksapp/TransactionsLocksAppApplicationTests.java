package ru.jpoint.transactionslocksapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.jpoint.transactionslocksapp.utils.RedisLockProvider;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionsLocksAppApplicationTests extends AbstractIntegrationTest {

    @Autowired
    RedisLockProvider lockService;

    @Test
    public void redisLockTest() {
        String idForLock = "5456432523";

        lockService.setLock(idForLock, Duration.ofSeconds(2));
        assertTrue(lockService.isLocked(idForLock));
    }

}
