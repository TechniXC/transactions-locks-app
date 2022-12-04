package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.HistoryEntity;
import ru.jpoint.transactionslocksapp.repository.HistoryRepository;
import ru.jpoint.transactionslocksapp.repository.SpeakersRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final SpeakersRepository speakersRepository;

    /**
     * Method for saving message to history.
     * Produces the message with DTO to kafka, for future processing.
     *
     * @param likes DTO with information about likes to be added.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 2)
    public void saveMessageToHistory(Likes likes, String status) {
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
