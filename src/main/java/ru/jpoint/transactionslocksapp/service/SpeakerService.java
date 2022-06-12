package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.HistoryEntity;
import ru.jpoint.transactionslocksapp.repository.HistoryRepository;
import ru.jpoint.transactionslocksapp.repository.SpeakersRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final SpeakersRepository speakersRepository;
    private final HistoryRepository historyRepository;
    private final StreamBridge streamBridge;

    /**
     * Method for adding likes to speaker by ID or TalkName.
     *
     * @param likes DTO with information about likes to be added.
     */
    public void addLikesToSpeaker(Likes likes) {
        log.warn("No JDBC connection now!");
        if (likes.getTalkName() != null) {
            speakersRepository.findByTalkName(likes.getTalkName()).ifPresentOrElse(speaker -> {
                saveMessageToHistory(likes, "RECEIVED");
                speaker.setLikes(speaker.getLikes() + likes.getLikes());
                log.info("{} likes added to {}", likes.getLikes(), speaker.getFirstName() + " " + speaker.getLastName());
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

    /**
     * Method for saving message to history.
     * Produces the message with DTO to kafka, for future processing.
     *
     * @param likes DTO with information about likes to be added.
     */
    private void saveMessageToHistory(Likes likes, String status) {
        try {
            historyRepository.save(HistoryEntity.builder()
                    .talkName(likes.getTalkName())
                    .likes(likes.getLikes())
                    .status(status)
                    .build());
        } catch (RuntimeException ex) {
            log.warn("Failed to save message to history.", ex);
        }
    }
}
