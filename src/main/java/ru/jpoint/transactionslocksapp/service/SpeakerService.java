package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.ScheduledTask;
import ru.jpoint.transactionslocksapp.entities.SpeakerEntity;
import ru.jpoint.transactionslocksapp.repository.ScheduledTasksRepository;
import ru.jpoint.transactionslocksapp.repository.SpeakersRepository;
import ru.jpoint.transactionslocksapp.utils.RedisLockProvider;

import javax.persistence.OptimisticLockException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeakerService {

    private final SpeakersRepository speakersRepository;
    private final RedisLockProvider redisLockProvider;
    private final SchedulingService schedulingService;

//    @Transactional
    public void addLikesToSpeaker(Likes likes) {
        if (Objects.nonNull(likes.getSpeakerId())) {
                addLikesById(likes.getSpeakerId(), likes.getLikes());
        } else if (Objects.nonNull(likes.getTalkName())) {
                addLikesByTalkName(likes.getTalkName(), likes.getLikes());
        } else {
            log.error("Error during adding likes, no IDs given");
        }
    }

    @Transactional
    public void addLikesToSpeakerIsolated(Likes likes) {
            if (Objects.nonNull(likes.getSpeakerId())) {
                addLikesById(likes.getSpeakerId(), likes.getLikes());
            } else if (Objects.nonNull(likes.getTalkName())) {
                addLikesByTalkName(likes.getTalkName(), likes.getLikes());
            } else {
                log.error("Error during adding likes, no IDs given");
            }
    }

    public void addLikesToSpeakerWithBadRedis(Likes likes) {
        if (Objects.nonNull(likes.getSpeakerId())) {
            redisLockProvider.tryToAcquireBadLock(likes.getSpeakerId().toString(), Duration.ofSeconds(2));
            addLikesById(likes.getSpeakerId(), likes.getLikes());
            redisLockProvider.releaseLock(likes.getSpeakerId().toString());
        } else if (Objects.nonNull(likes.getTalkName())) {
            redisLockProvider.tryToAcquireBadLock(likes.getTalkName(), Duration.ofSeconds(2));
            addLikesByTalkName(likes.getTalkName(), likes.getLikes());
            redisLockProvider.releaseLock(likes.getTalkName());
        } else {
            log.error("Error during adding likes, no IDs given");
        }
    }

    public void addLikesToSpeakerWithRedis(Likes likes) {
        if (Objects.nonNull(likes.getSpeakerId())) {
            if (redisLockProvider.tryToAcquireLockForScheduling(likes.getSpeakerId().toString(), Duration.ofSeconds(2))) {
                addLikesById(likes.getSpeakerId(), likes.getLikes());
                redisLockProvider.releaseLock(likes.getSpeakerId().toString());
            } else {
                schedulingService.createTask(likes);
            }
        } else if (Objects.nonNull(likes.getTalkName())) {
            if (redisLockProvider.tryToAcquireLockForScheduling(likes.getTalkName(), Duration.ofSeconds(2))) {
                addLikesByTalkName(likes.getTalkName(), likes.getLikes());
                redisLockProvider.releaseLock(likes.getTalkName());
            } else {
                schedulingService.createTask(likes);
            }
        } else {
            log.error("Error during adding likes, no IDs");
        }
    }

    public SpeakerEntity getSpeaker(Long id, String talkName) {
        if (Objects.nonNull(id)) {
            return speakersRepository.findById(id).orElse(null);
        } else if (Objects.nonNull(talkName)) {
            return speakersRepository.findByTalkName(talkName).orElse(null);
        }
        throw new IllegalArgumentException("No parameters to find speaker given!");
    }

    public SpeakerEntity saveSpeaker(SpeakerEntity speaker) {
        return speakersRepository.save(speaker);
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
}
