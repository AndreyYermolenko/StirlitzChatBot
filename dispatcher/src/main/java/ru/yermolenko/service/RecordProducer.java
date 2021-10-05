package ru.yermolenko.service;

import ru.yermolenko.model.GeneralRecord;
import ru.yermolenko.model.MessageRecord;
import ru.yermolenko.model.enam.RabbitQueue;

public interface RecordProducer {
    void produce(RabbitQueue rabbitQueue, MessageRecord messageRecord);
    void produce(RabbitQueue rabbitQueue, GeneralRecord generalRecord);
}
