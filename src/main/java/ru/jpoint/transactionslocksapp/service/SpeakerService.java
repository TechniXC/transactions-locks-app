package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.HistoryEntity;
import ru.jpoint.transactionslocksapp.repository.HistoryRepository;
import ru.jpoint.transactionslocksapp.repository.SpeakersRepository;

import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final SpeakersRepository speakersRepository;
    private final HistoryService historyService;
    private final StreamBridge streamBridge;

    /**
     * Method for adding likes to speaker by ID or TalkName.
     *
     * @param likes DTO with information about likes to be added.
     */
    @Retryable
    @Transactional(timeout = 10)
    public void addLikesToSpeaker(Likes likes) {
        if (likes.getTalkName() != null) {
            speakersRepository.findByTalkName(likes.getTalkName()).ifPresentOrElse(speaker -> {
                saveMessageToHistory(likes, "RECEIVED");
                log.info("Adding {} likes to {}", likes.getLikes(), speaker.getFirstName() + " " + speaker.getLastName());
                speaker.setLikes(speaker.getLikes() + likes.getLikes());
                speakersRepository.save(speaker);
            }, () -> {
                log.warn("Speaker with talk {} not found", likes.getTalkName());
                saveMessageToHistory(likes, "ORPHANED");
            });
        } else {
            log.error("Error during adding likes, no IDs given");
            saveMessageToHistory(likes, "CORRUPTED");
        }
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


    public void saveMessageToHistory(Likes likes, String status) {
        historyService.saveMessageToHistory(likes, status);
    }
}
