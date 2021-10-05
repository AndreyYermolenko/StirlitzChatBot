package ru.yermolenko.service;

import ru.yermolenko.model.GeneralRecord;
import ru.yermolenko.model.MessageRecord;

public interface ConsumerService {
    void consumeGeneralRecords(GeneralRecord generalRecord);
    void consumeTextMessageRecords(MessageRecord messageRecord);
    void consumeDocMessageRecords(MessageRecord messageRecord);
    void consumePhotoMessageRecords(MessageRecord messageRecord);
}
