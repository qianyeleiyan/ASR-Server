package org.codesdream.asr.repository.time;

import org.codesdream.asr.model.time.TimeDisableLaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeDisableLawRepository extends JpaRepository<TimeDisableLaw, Integer> {

    Iterable<TimeDisableLaw> findAllByUserId(Integer user);

    Optional<TimeDisableLaw> findAllByUserIdAndDayOfWeek(Integer user, Integer dayOfWeek);

}
