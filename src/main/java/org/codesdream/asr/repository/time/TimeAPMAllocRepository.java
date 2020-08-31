package org.codesdream.asr.repository.time;

import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeAPMAllocRepository extends JpaRepository<TimeAPMAlloc, Integer> {

    Optional<TimeAPMAlloc> findByApmId(String apmId);

    Iterable<TimeAPMAlloc> findAllByRequestId(String requestId);

}
