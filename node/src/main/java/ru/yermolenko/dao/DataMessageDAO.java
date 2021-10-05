package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.DataMessage;

public interface DataMessageDAO extends JpaRepository<DataMessage, Long> {
    DataMessage findMessageByExternalServiceId(Integer externalServiceId);
}
