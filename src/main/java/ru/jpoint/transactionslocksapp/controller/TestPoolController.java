package ru.jpoint.transactionslocksapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestPoolController {

    @GetMapping("/test")
    @Transactional
    public ResponseEntity<String> testTransaction() throws InterruptedException {
        log.warn("Thread {} started", Thread.currentThread().getId());
        Thread.sleep(10000);
        log.warn("Thread {} finished the work", Thread.currentThread().getId());
        return new ResponseEntity<>("Test passed!", HttpStatus.OK);
    }
}
