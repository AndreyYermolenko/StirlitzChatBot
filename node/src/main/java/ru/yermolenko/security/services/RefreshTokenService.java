package ru.yermolenko.security.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yermolenko.dao.RefreshTokenDAO;
import ru.yermolenko.dao.UserDAO;
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
    private final UserDAO userDAO;

    public RefreshTokenService(UserDAO userDAO, RefreshTokenDAO refreshTokenDAO) {
        this.userDAO = userDAO;
        this.refreshTokenDAO = refreshTokenDAO;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenDAO.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userDAO.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenDAO.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenDAO.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenDAO.deleteByUser(userDAO.findById(userId).get());
    }
}
