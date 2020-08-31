package org.codesdream.asr.repository.auth;

import com.google.gson.stream.JsonToken;
import org.codesdream.asr.model.auth.JSONToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JSONTokenRepository extends CrudRepository<JSONToken, Integer> {
    Optional<JSONToken> findByUsername(String username);

    Optional<JsonToken> findByToken(String token);
}
