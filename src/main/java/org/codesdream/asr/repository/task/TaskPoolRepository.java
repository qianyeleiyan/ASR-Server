package org.codesdream.asr.repository.task;

import org.codesdream.asr.model.task.TaskPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskPoolRepository extends JpaRepository<TaskPool, Integer> {
    Optional<TaskPool> findByUserId(Integer userId);
}
