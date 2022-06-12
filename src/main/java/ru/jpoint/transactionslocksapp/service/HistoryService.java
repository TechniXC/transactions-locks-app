package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.HistoryEntity;
import ru.jpoint.transactionslocksapp.repository.HistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    /**
     * Method for saving message to history.
     * Produces the message with DTO to kafka, for future processing.
     *
     * @param likes DTO with information about likes to be added.
     */
    synchronized public void saveMessageToHistory(Likes likes, String status) {
        historyRepository.save(HistoryEntity.builder()
                .talkName(likes.getTalkName())
                .likes(likes.getLikes())
                .status(status)
                .build());
    }
}
