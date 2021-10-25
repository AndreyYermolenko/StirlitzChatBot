package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;


@RestController
@Log4j
public class WebHookController {
    private final DispatcherBot dispatcherBot;
    private final Environment env;

    public WebHookController(DispatcherBot dispatcherBot, Environment env) {
        this.dispatcherBot = dispatcherBot;
        this.env = env;

        boolean devProfile = Arrays.asList(env.getActiveProfiles()).contains("dev");
        log.debug("dev profile is activated: " + devProfile);
//        if (devProfile) {
        SetWebhook setWebhook = SetWebhook.builder()
                    .url(dispatcherBot.getBotPath())
                    .build();
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
