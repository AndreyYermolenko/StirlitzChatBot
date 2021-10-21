package ru.yermolenko.dao.impl;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.yermolenko.dao.RefreshTokenDAO;
import ru.yermolenko.model.RefreshToken;

import java.util.Optional;

@Repository
public class RefreshTokenDAOImpl implements RefreshTokenDAO {
    private final HashOperations<String, String, RefreshToken> hashOperations;
    private final String key = "refresh_tokens";

    public RefreshTokenDAOImpl(RedisTemplate<String, RefreshToken> redisTemplate) {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(RefreshToken refreshToken) {
        hashOperations.put(key, refreshToken.getToken(), refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(hashOperations.get(key, token));
    }

    @Override
    public void delete(String token) {
        hashOperations.delete(key, token);
    }
}
