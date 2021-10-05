package ru.yermolenko.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.yermolenko.model.GeneralRecord;
import ru.yermolenko.model.MessageRecord;
import ru.yermolenko.model.enam.RabbitQueue;
import ru.yermolenko.service.RecordProducer;

@Service
public class RecordProducerImpl implements RecordProducer {
    private final RabbitTemplate rabbitTemplate;

    public RecordProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(RabbitQueue rabbitQueue, MessageRecord messageRecord) {
        rabbitTemplate.convertAndSend(rabbitQueue.toString(), messageRecord);
    }

    @Override
    public void produce(RabbitQueue rabbitQueue, GeneralRecord generalRecord) {
        rabbitTemplate.convertAndSend(rabbitQueue.toString(), generalRecord);
    }
}
