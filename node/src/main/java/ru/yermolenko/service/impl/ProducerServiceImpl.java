package ru.yermolenko.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.yermolenko.service.ProducerService;

@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend("messageToSend", sendMessage);
    }

    public void produceAnswer(DeleteMessage deleteMessage) {
        rabbitTemplate.convertAndSend("messageToDelete", deleteMessage);
    }

}
