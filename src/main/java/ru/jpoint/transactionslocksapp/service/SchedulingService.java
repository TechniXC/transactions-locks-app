package ru.jpoint.transactionslocksapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.entities.ScheduledTask;
import ru.jpoint.transactionslocksapp.repository.ScheduledTasksRepository;

@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final ScheduledTasksRepository scheduledTasksRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createTask(Likes likes) {
        var task = ScheduledTask.builder()
                .taskType("Likes")
                .taskData(likes)
                .build();
        scheduledTasksRepository.save(task);
    }
}
