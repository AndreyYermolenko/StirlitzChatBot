package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.yermolenko.model.GeneralRecord;
import ru.yermolenko.model.MessageRecord;
import ru.yermolenko.model.enam.RabbitQueue;
import ru.yermolenko.service.RecordProducer;

@Component
@Log4j
public class MainController {
    private final RecordProducer recordProducer;
    private DispatcherBot dispatcherBot;

    public MainController(RecordProducer messageRecordProducer) {
        this.recordProducer = messageRecordProducer;
    }

    public void mainHandler(Update update) {
        handleUpdate(update);
        Message externalMessage;
        if (update.getMessage() != null) {
            externalMessage = update.getMessage();
        } else if (update.getEditedMessage() != null) {
            externalMessage = update.getEditedMessage();
        } else {
            dispatcherBot.sendMessage(handleUnsupportedMessageType(update));
            return;
        }

        if (externalMessage.getText() != null) {
            handleText(externalMessage);
        } else if (externalMessage.getDocument() != null) {
            handleDoc(externalMessage);
        } else if (externalMessage.getPhoto() != null) {
            handlePhoto(externalMessage);
        } else {
            dispatcherBot.sendMessage(handleUnsupportedMessageType(update));
        }
    }

    private void handleUpdate(Update update) {
        GeneralRecord generalRecord = GeneralRecord.builder()
                .update(update)
                .botname(dispatcherBot.getBotUsername())
                .build();
        recordProducer.produce(RabbitQueue.GENERAL_RECORD, generalRecord);
    }

    private void handleText(Message externalMessage) {
        MessageRecord messageRecord = MessageRecord.builder()
                .message(externalMessage)
                .botname(dispatcherBot.getBotUsername())
                .build();
        recordProducer.produce(RabbitQueue.TEXT_MESSAGE_RECORD, messageRecord);
    }

    private void handleDoc(Message externalMessage) {
        MessageRecord messageRecord = MessageRecord.builder()
                .message(externalMessage)
                .botname(dispatcherBot.getBotUsername())
                .build();
        recordProducer.produce(RabbitQueue.DOC_MESSAGE_RECORD, messageRecord);
        fileIsReceivedMessage(externalMessage);
    }

    private void handlePhoto(Message externalMessage) {
        MessageRecord messageRecord = MessageRecord.builder()
                .message(externalMessage)
                .botname(dispatcherBot.getBotUsername())
                .build();
        fileIsReceivedMessage(externalMessage);
        recordProducer.produce(RabbitQueue.PHOTO_MESSAGE_RECORD, messageRecord);
    }

    public void registerBot(DispatcherBot dispatcherBot) {
        this.dispatcherBot = dispatcherBot;
    }

    public void setView(SendMessage sendMessage) {
        dispatcherBot.sendMessage(sendMessage);
    }

    public void setView(DeleteMessage deleteMessage) {
        dispatcherBot.deleteMessage(deleteMessage);
    }

    private SendMessage handleUnsupportedMessageType(Update update) {
        try {
            Message message = update.getMessage() != null ? update.getMessage() : update.getEditedMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText("Unsupported message type!");
            return sendMessage;
        } catch (NullPointerException e) {
            log.debug(e);
        }
        return null;
    }

    private void fileIsReceivedMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Файл получен! Обрабатывается...");
        setView(sendMessage);
    }
}
