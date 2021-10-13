package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.ServiceUser;

public interface ServiceUserDAO extends JpaRepository<ServiceUser, Long> {
    ServiceUser findUserByExternalServiceId(Integer externalServiceId);
}
