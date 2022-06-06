package ru.jpoint.transactionslocksapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class TransactionsLocksAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionsLocksAppApplication.class, args);
    }

}
