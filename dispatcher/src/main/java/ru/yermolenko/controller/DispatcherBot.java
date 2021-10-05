package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
public class DispatcherBot extends TelegramLongPollingBot {
    @Value("${botname}")
    private String botname;
    @Value("${token}")
    private String token;
    private final MainController mainController;

    public DispatcherBot(MainController mainController) {
        this.mainController = mainController;
        mainController.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return botname;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        mainController.mainHandler(update);
        log.debug(update);
    }

    public void setView(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
