package ru.yermolenko.service.impl;

import org.springframework.stereotype.Service;
import ru.yermolenko.dao.BotDAO;
import ru.yermolenko.model.Bot;
import ru.yermolenko.service.BotService;

@Service
public class BotServiceImpl implements BotService {
    private final BotDAO botDAO;

    public BotServiceImpl(BotDAO botDAO) {
        this.botDAO = botDAO;
    }

    @Override
    public Bot getPersistentBot(String botname) {
        Bot persistentBot = botDAO.findByBotName(botname);
        if (persistentBot == null) {
            Bot transientBot = Bot.builder().botName(botname).build();
            persistentBot = botDAO.save(transientBot);
        }
        return persistentBot;
    }
}
