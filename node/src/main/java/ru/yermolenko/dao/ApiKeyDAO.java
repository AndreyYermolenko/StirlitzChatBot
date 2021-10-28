package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.AppUser;
import ru.yermolenko.model.ApiKey;

import java.util.Optional;

public interface ApiKeyDAO extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByAppUser(AppUser appUser);
    Optional<ApiKey> findByApiKey(String userApiKey);
}
