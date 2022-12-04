package ru.jpoint.transactionslocksapp.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.service.SpeakerService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerMessageProcessor {

    private final SpeakerService speakerService;

    public void processOneMessage(Likes likes) {
        speakerService.addLikesToSpeaker(likes);
    }

    //<editor-fold desc="Batch Processing">
    public void processBatchOfMessages(List<Likes> likes) {

        var accumulatedLikes = likes.stream()
                .filter(Objects::nonNull)
                .filter(x -> x.getTalkName() != null)
                .filter(x -> !x.getTalkName().isEmpty())
                .collect(Collectors.groupingBy(Likes::getTalkName))
                .values().stream()
                .map(likesListTalkName -> likesListTalkName.stream().reduce(new Likes(), (x, y) -> Likes.builder()
                        .talkName(y.getTalkName())
                        .likes(x.getLikes() + y.getLikes())
                        .build()))
                .collect(Collectors.toList());
        log.info("Aggregated Likes: {}", accumulatedLikes);

        try {
            var futures = accumulatedLikes.stream()
                    .map(like -> CompletableFuture.runAsync(() -> speakerService.addLikesToSpeaker(like)))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futures).join();
        } catch (CompletionException ex) {
            log.error("Something went wrong during batch processing.:", ex);
        }
    }
    //</editor-fold>
}
