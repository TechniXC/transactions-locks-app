package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.ScheduledTask;
import ru.jpoint.transactionslocksapp.entities.SpeakerEntity;
import ru.jpoint.transactionslocksapp.repository.ScheduledTasksRepository;
import ru.jpoint.transactionslocksapp.repository.SpeakersRepository;
import ru.jpoint.transactionslocksapp.utils.RedisLockProvider;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakerService {

    private final SpeakersRepository speakersRepository;
    private final ScheduledTasksRepository scheduledTasksRepository;
    private final RedisLockProvider redisLockProvider;

    //    @Transactional
    public void addLikesToSpeaker(Likes likes) {
        if (Objects.nonNull(likes.getSpeakerId())) {
            if (redisLockProvider.tryToAcquireLockForScheduling(likes.getSpeakerId().toString(), Duration.ofSeconds(2))) {
                addLikesById(likes.getSpeakerId(), likes.getLikes());
            } else {
                createTask(likes);
            }
        } else if (Objects.nonNull(likes.getTalkName())) {
            if (redisLockProvider.tryToAcquireLockForScheduling(likes.getTalkName(), Duration.ofSeconds(2))) {
                addLikesByTalkName(likes.getTalkName(), likes.getLikes());
            } else {
                createTask(likes);
            }
        } else {
            log.error("Error during adding likes, no IDs");
        }
    }

    public SpeakerEntity saveSpeaker(SpeakerEntity speaker) {
        return speakersRepository.save(speaker);
    }

    private void createTask(Likes likes) {
        var task = ScheduledTask.builder()
                .taskType("Likes")
                .taskData(likes)
                .build();
        scheduledTasksRepository.save(task);
    }

    private void addLikesById(Long id, int likesAmount) {
//        redisLockProvider.tryToAcquireBadLock(id.toString(), Duration.ofSeconds(2));
        speakersRepository.findById(id).ifPresentOrElse(speaker -> {
            speaker.setLikes(speaker.getLikes() + likesAmount);
            speakersRepository.saveAndFlush(speaker);
            log.info("{} likes added to {}", likesAmount, speaker.getFirstName() + " " + speaker.getLastName());
        }, () -> log.warn("Speaker with id {} not found", id));
        redisLockProvider.releaseLock(id.toString());
    }

    private void addLikesByTalkName(String talkName, int likesAmount) {
//        redisLockProvider.tryToAcquireBadLock(talkName, Duration.ofSeconds(2));
        speakersRepository.findByTalkName(talkName).ifPresentOrElse(speaker -> {
            speaker.setLikes(speaker.getLikes() + likesAmount);
            speakersRepository.saveAndFlush(speaker);
            log.info("{} likes added to {}", likesAmount, speaker.getFirstName() + " " + speaker.getLastName());
        }, () -> log.warn("Speaker with talk {} not found", talkName));
        redisLockProvider.releaseLock(talkName);
    }
}
