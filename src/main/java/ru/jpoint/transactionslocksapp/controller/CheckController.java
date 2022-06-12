package ru.jpoint.transactionslocksapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CheckController {

    @GetMapping("/test")
    public ResponseEntity<String> testTransaction() throws InterruptedException {
        log.warn("Thread {} started", Thread.currentThread().getId());
        Thread.sleep(5000);
        log.warn("Thread {} finished the work", Thread.currentThread().getId());
        return new ResponseEntity<>("Test passed!", HttpStatus.OK);
    }
}
