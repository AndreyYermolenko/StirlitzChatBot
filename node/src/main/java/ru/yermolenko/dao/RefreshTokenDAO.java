package ru.yermolenko.dao;

import ru.yermolenko.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenDAO {
  void save(RefreshToken refreshToken);
  Optional<RefreshToken> findByToken(String token);
  void delete(String token);
}
