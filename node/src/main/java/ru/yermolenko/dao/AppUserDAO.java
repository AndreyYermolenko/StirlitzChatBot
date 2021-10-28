package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.AppUser;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findUserByExternalServiceId(Integer externalServiceId);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
}
