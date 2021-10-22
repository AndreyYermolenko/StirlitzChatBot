package ru.yermolenko.security.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yermolenko.dao.RefreshTokenDAO;
import ru.yermolenko.exception.TokenRefreshException;
import ru.yermolenko.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenDAO refreshTokenDAO;

    public RefreshTokenService(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenDAO.findById(token);
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenDAO.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenDAO.deleteById(token.getToken());
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenDAO.deleteById(token);
    }

    public Optional<RefreshToken> findByUsername(String username) {
        return refreshTokenDAO.findByUsername(username);
    }
}
