package org.codesdream.asr.repository.task;

import org.codesdream.asr.model.task.PlanPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanPoolRepository extends JpaRepository<PlanPool, Integer> {
    Optional<PlanPool> findByUserId(Integer userId);
}
