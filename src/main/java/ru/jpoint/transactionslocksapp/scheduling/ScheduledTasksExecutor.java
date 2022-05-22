package ru.jpoint.transactionslocksapp.scheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.jpoint.transactionslocksapp.dto.Likes;
import ru.jpoint.transactionslocksapp.repository.ScheduledTasksRepository;
import ru.jpoint.transactionslocksapp.service.SpeakerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasksExecutor {

    private final ObjectMapper objectMapper;
    private final SpeakerService speakerService;
    private final ScheduledTasksRepository tasksRepository;

    @Scheduled(cron = "* * * ? * *")
    @SchedulerLock(name = "ScheduledTasksExecutor")
    public void executeScheduledLikeTask() {
        tasksRepository.findAllByTaskTypeAndCompleted("Likes", false).forEach(task -> {
            try {
                var likes = objectMapper.convertValue(task.getTaskData(), Likes.class);
                speakerService.addLikesToSpeaker(likes);
                task.setCompleted(true);
                tasksRepository.save(task);
            } catch (Exception e) {
                log.error("Shit happens..", e);
            }
        });
    }

}
