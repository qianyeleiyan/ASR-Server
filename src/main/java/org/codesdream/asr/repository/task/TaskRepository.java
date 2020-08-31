package org.codesdream.asr.repository.task;

import io.swagger.models.auth.In;
import org.codesdream.asr.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Iterable<Task> findAllByUserId(Integer userId);
    void deleteAllByIdIn(List<Integer> taskIds);
}
