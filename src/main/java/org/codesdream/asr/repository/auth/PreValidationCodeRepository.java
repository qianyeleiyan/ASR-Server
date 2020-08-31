package org.codesdream.asr.repository.auth;

import org.codesdream.asr.model.auth.PreValidationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreValidationCodeRepository extends CrudRepository<PreValidationCode, Integer> {
    Optional<PreValidationCode> findByValue(String value);
}
