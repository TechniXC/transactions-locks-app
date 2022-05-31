package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.SpeakerEntity;
import ru.jpoint.transactionslocksapp.repository.SpeakersRepository;
import ru.jpoint.transactionslocksapp.utils.RedisLockProvider;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final SpeakersRepository speakersRepository;
    private final StreamBridge streamBridge;

    /**
     * Method for adding likes to speaker by ID or TalkName.
     *
     * @param likes DTO with information about likes to be added.
     */
//    @Transactional
    public void addLikesToSpeaker(Likes likes) {
//        try {
        if (likes.getSpeakerId() != null) {
            addLikesById(likes.getSpeakerId(), likes.getLikes());
        } else if (likes.getTalkName() != null) {
            addLikesByTalkName(likes.getTalkName(), likes.getLikes());
        } else {
            log.error("Error during adding likes, no IDs given");
        }
//        } catch (Exception ex) {
//            log.error("Possibly concurrent updates, task will be created!", ex);
//            createTaskToAddLikes(likes);
//        }

    }

    /**
     * Method for creating task to add likes to speaker.
     * Produces the message with DTO to kafka, for future processing.
     *
     * @param likes DTO with information about likes to be added.
     */
    public void createTaskToAddLikes(Likes likes) {
        streamBridge.send("likesProducer-out-0", likes);
    }

    private void addLikesById(Long id, int likesAmount) {
        speakersRepository.findById(id).ifPresentOrElse(speaker -> {
            speaker.setLikes(speaker.getLikes() + likesAmount);
            speakersRepository.save(speaker);
            log.info("{} likes added to {}", likesAmount, speaker.getFirstName() + " " + speaker.getLastName());
        }, () -> log.warn("Speaker with id {} not found", id));
    }

    private void addLikesByTalkName(String talkName, int likesAmount) {
        speakersRepository.findByTalkName(talkName).ifPresentOrElse(speaker -> {
            speaker.setLikes(speaker.getLikes() + likesAmount);
            speakersRepository.save(speaker);
            log.info("{} likes added to {}", likesAmount, speaker.getFirstName() + " " + speaker.getLastName());
        }, () -> log.warn("Speaker with talk {} not found", talkName));

    }

    //<editor-fold desc="Initialize Speaker database.">
    @PostConstruct
    private void initSpeakers() {
        speakersRepository.deleteAll();
        var speaker = SpeakerEntity.builder()
                .id(1L)
                .FirstName("John")
                .LastName("Doe")
                .likes(0)
                .talkName("Spring best practice")
                .build();
        speakersRepository.save(speaker);
    }
    //</editor-fold>

    //<editor-fold desc="Adding likes with redis Lock">
    private final RedisLockProvider redisLockProvider;

    public void addLikesToSpeakerWithRedis(Likes likes) {
        if (Objects.nonNull(likes.getSpeakerId())) {
            if (redisLockProvider.tryToAcquireLock(likes.getSpeakerId().toString(), Duration.ofSeconds(2))) {
                addLikesById(likes.getSpeakerId(), likes.getLikes());
                redisLockProvider.releaseLock(likes.getSpeakerId().toString());
            } else {
                createTaskToAddLikes(likes);
            }
        } else if (Objects.nonNull(likes.getTalkName())) {
            if (redisLockProvider.tryToAcquireLock(likes.getTalkName(), Duration.ofSeconds(2))) {
                addLikesByTalkName(likes.getTalkName(), likes.getLikes());
                redisLockProvider.releaseLock(likes.getTalkName());
            } else {
                createTaskToAddLikes(likes);
            }
        } else {
            log.error("Error during adding likes, no IDs");
        }
    }
    //</editor-fold>

}
