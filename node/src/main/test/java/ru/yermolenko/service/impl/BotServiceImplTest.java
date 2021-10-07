package ru.yermolenko.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.yermolenko.dao.BotDAO;
import ru.yermolenko.model.Bot;
import ru.yermolenko.service.BotService;

@SpringBootTest
@ActiveProfiles("dev")
class BotServiceImplTest {
    @Autowired
    private BotService botService;
    @MockBean
    private BotDAO botDAO;
    private final String botname = "stirlitz_chatbot";

    @Test
    void getPersistentBot() {
        Mockito.doReturn(new Bot())
                .when(botDAO)
                .findByBotName(botname);
        botService.getPersistentBot(botname);

        Mockito.verify(botDAO, Mockito.times(1)).findByBotName(botname);
        Mockito.verify(botDAO, Mockito.times(0)).save(ArgumentMatchers.any(Bot.class));
    }

    @Test
    void saveAndGetPersistentBot() {
        Mockito.doReturn(new Bot())
                .when(botDAO)
                .save(ArgumentMatchers.any(Bot.class));
        botService.getPersistentBot(botname);

        Mockito.verify(botDAO, Mockito.times(1)).findByBotName(botname);
        Mockito.verify(botDAO, Mockito.times(1)).save(ArgumentMatchers.any(Bot.class));
    }
}
