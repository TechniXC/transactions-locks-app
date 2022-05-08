package ru.jpoint.transactionslocksapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jpoint.transactionslocksapp.entities.ScheduledTask;

import java.util.List;

@Repository
public interface ScheduledTasksRepository extends JpaRepository<ScheduledTask, Long> {

    List<ScheduledTask> findAllByTaskTypeAndCompleted (String taskType, boolean completed);
}
