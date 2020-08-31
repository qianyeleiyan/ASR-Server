package org.codesdream.asr.repository.time;

import org.codesdream.asr.model.time.TimeBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.Optional;

@Repository
public interface TimeBlockRepository extends JpaRepository<TimeBlock, Integer> {
    Iterable<TimeBlock> findAllByUserId(Integer userId);

    @Transactional
    void deleteByCodeAndUserId(Integer code, Integer userId);

    Optional<TimeBlock> findByCodeAndUserId(Integer code, Integer userId);

}
