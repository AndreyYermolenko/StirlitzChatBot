package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.yermolenko.controller.MainController;
import ru.yermolenko.service.AnswerConsumer;

@Service
@Log4j
public class AnswerConsumerImpl implements AnswerConsumer {
    private final MainController mainController;

    public AnswerConsumerImpl(MainController mainController) {
        this.mainController = mainController;
    }

    @RabbitListener(queues = "answerFromNode")
    public void consume(SendMessage sendMessage) {
        log.debug("Consuming the message: " + sendMessage.getText());
        mainController.setView(sendMessage);
    }
}
