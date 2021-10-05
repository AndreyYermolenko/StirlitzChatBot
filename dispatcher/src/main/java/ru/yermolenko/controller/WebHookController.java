package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
@Log4j
public class WebHookController {
    private final DispatcherBot dispatcherBot;

    public WebHookController(DispatcherBot dispatcherBot) {
        this.dispatcherBot = dispatcherBot;
        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl(dispatcherBot.getBotPath());
        try {
            dispatcherBot.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return dispatcherBot.onWebhookUpdateReceived(update);
    }
}
