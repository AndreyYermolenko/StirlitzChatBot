package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.Bot;

public interface BotDAO extends JpaRepository<Bot, Long> {
    Bot findByBotName(String botName);
}
