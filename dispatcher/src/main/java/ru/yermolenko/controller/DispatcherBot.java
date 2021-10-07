package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
public class DispatcherBot extends TelegramWebhookBot {
    @Value("${botname}")
    private String botname;
    @Value("${token}")
    private String token;
    @Value("${botpath}")
    private String botpath;
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

    public void setView(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.debug(update);
        mainController.mainHandler(update);
        return null;
    }

    @Override
    public String getBotPath() {
        return botpath;
    }
}
