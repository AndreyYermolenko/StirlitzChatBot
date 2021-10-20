package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.ServiceUser;
import ru.yermolenko.model.ApiKey;

import java.util.Optional;

public interface ApiKeyDAO extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByServiceUser(ServiceUser serviceUser);
    Optional<ApiKey> findByApiKey(String userApiKey);
}
