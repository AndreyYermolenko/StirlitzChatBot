package ru.yermolenko.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public interface AnswerConsumer {
    void consumeToSend(SendMessage sendMessage);
    void consumeToDelete(DeleteMessage deleteMessage);
}
