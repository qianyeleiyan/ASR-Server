package org.codesdream.asr.repository.file;

import org.codesdream.asr.model.file.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    Iterable<File> findAllByHash(String hash);
}
