package ru.yermolenko.service;

import ru.yermolenko.model.Bot;

public interface BotService {
    Bot getPersistentBot(String botname);
}
