package org.codesdream.asr.repository.record;

import org.codesdream.asr.model.record.TATRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TATRecordRepository extends JpaRepository<TATRecord, Integer> {

    Iterable<TATRecord> findAllByFinished(Boolean finished);

    Optional<TATRecord> findByUserIdAndRequestId(Integer userId, String reuqestId);

}
