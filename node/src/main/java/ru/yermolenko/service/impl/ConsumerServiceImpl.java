package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.yermolenko.model.GeneralRecord;
import ru.yermolenko.model.MessageRecord;
import ru.yermolenko.service.MainService;

@Service
@Log4j
public class ConsumerServiceImpl {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @RabbitListener(queues = "general_record")
    public void consumeGeneralRecords(GeneralRecord generalRecord) {
        log.debug("Consuming the message: " + generalRecord);
        mainService.saveRawData(generalRecord);
    }

    @RabbitListener(queues = "text_message_record")
    public void consumeTextMessageRecords(MessageRecord messageRecord) {
        mainService.processTextMessage(messageRecord);
    }

    @RabbitListener(queues = "doc_message_record")
    public void consumeDocMessageRecords(MessageRecord messageRecord) {
        mainService.saveOrModifyDocument(messageRecord);
    }

    @RabbitListener(queues = "photo_message_record")
    public void consumePhotoMessageRecords(MessageRecord messageRecord) {
        mainService.saveOrModifyPhoto(messageRecord);
    }
}
