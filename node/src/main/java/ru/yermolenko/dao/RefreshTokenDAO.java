package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.yermolenko.model.AppUser;
import ru.yermolenko.model.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenDAO extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Modifying
  int deleteByUser(AppUser client);
}
