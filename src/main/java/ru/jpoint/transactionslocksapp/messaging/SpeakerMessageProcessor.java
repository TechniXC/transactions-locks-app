package ru.jpoint.transactionslocksapp.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.service.SpeakerService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerMessageProcessor {

    private final SpeakerService speakerService;

    //    Helpful??
    //    @Transactional
    public void processMessage(List<Likes> likes) {
//        Sequential execution:
//        likes.forEach(speakerService::addLikesToSpeaker);

//        Little Grouping

        var accumulatedLikes = Stream.concat(
                        likes.stream()
                                .filter(Objects::nonNull)
                                .filter(x -> Objects.nonNull(x.getSpeakerId()))
                                .collect(Collectors.groupingBy(Likes::getSpeakerId))
                                .values().stream()
                                .map(likesList -> likesList.stream().reduce(new Likes(), (x, y) -> Likes.builder()
                                        .speakerId(y.getSpeakerId())
                                        .likes(x.getLikes() + y.getLikes())
                                        .build())),
                        likes.stream()
                                .filter(Objects::nonNull)
                                .filter(x -> !x.getTalkName().isEmpty())
                                .collect(Collectors.groupingBy(Likes::getTalkName))
                                .values().stream()
                                .map(likesListTalkName -> likesListTalkName.stream().reduce(new Likes(), (x, y) -> Likes.builder()
                                        .talkName(y.getTalkName())
                                        .likes(x.getLikes() + y.getLikes())
                                        .build())))
                .collect(Collectors.toList());

        log.info("Aggregated {}", accumulatedLikes);

        try {
            CompletableFuture<?>[] futures = accumulatedLikes.stream()
//            CompletableFuture<?>[] futures = likes.stream()
                    .map(like -> CompletableFuture.runAsync(() -> speakerService.addLikesToSpeaker(like)))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futures).join();
        } catch (CompletionException ex) {
            log.error("Something went wrong", ex);
        }


    }

}