package ru.jpoint.transactionslocksapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.jpoint.transactionslocksapp.entities.SpeakerEntity;
import ru.jpoint.transactionslocksapp.service.SpeakerService;

import java.time.LocalDateTime;
import java.util.Objects;
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

    @PostMapping("/addLike/${speakerId}")
    public ResponseEntity<SpeakerEntity> updateSpeaker(Long speakerId) {
        var currentSpeaker = service.getSpeaker(speakerId, null);
        if (Objects.nonNull(currentSpeaker)) {
            currentSpeaker.setLikes(currentSpeaker.getLikes() + 1);
            var result = service.saveSpeaker(currentSpeaker);
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
