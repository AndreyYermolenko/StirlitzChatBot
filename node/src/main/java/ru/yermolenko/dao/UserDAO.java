package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.User;

public interface UserDAO extends JpaRepository<User, Long> {
    User findUserByExternalServiceId(Integer externalServiceId);
}
