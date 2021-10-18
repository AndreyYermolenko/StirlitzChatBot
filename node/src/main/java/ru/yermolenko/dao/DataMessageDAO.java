package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yermolenko.model.DataMessage;

import java.util.List;
import java.util.Optional;

public interface DataMessageDAO extends JpaRepository<DataMessage, Long> {
    DataMessage findMessageByExternalServiceId(Integer externalServiceId);
    @Query(value = "SELECT * FROM data_message dm WHERE dm.chat_id = :chatId ORDER BY dm.create_date LIMIT :limit",
            nativeQuery = true)
    Optional<List<DataMessage>> findLastMessagesByChatId(@Param("chatId") Long chatId, @Param("limit") Integer limit);
}
