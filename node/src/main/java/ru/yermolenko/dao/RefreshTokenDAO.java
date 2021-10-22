package ru.yermolenko.dao;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import ru.yermolenko.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenDAO extends KeyValueRepository<RefreshToken, String> {
    Optional<RefreshToken> findById(String id);
    void deleteById(String id);
}
