package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.ServiceUser;
import ru.yermolenko.model.UserApiKey;

import java.util.Optional;

public interface UserApiKeyDAO extends JpaRepository<UserApiKey, Long> {
    Optional<UserApiKey> findByServiceUser(ServiceUser serviceUser);
    Optional<UserApiKey> findByApiKey(String userApiKey);
}
