package org.codesdream.asr.repository.task;

import io.swagger.models.auth.In;
import org.codesdream.asr.model.task.Plan;
import org.codesdream.asr.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    Iterable<Plan> findAllByUserId(Integer userId);

    void deleteAllByIdIn(List<Integer> planIds);

    Plan save(Plan plan);
}
