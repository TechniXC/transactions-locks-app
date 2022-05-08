package ru.jpoint.transactionslocksapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jpoint.transactionslocksapp.entities.SpeakerEntity;
import ru.jpoint.transactionslocksapp.service.SpeakerService;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class SpeakerController {

    private final SpeakerService service;

    @PostMapping("/create/")
    public ResponseEntity<SpeakerEntity> createSpeaker() {
        var speaker = SpeakerEntity.builder()
                .FirstName(randomString())
                .LastName(randomString())
                .talkName(randomString())
                .likes(0)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
        var result = service.saveSpeaker(speaker);
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
