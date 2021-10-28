package ru.yermolenko.service;

import ru.yermolenko.model.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;

public interface MainService {
    void saveRawData(GeneralRecord generalRecord);
    AppUser findOrSaveUser(org.telegram.telegrambots.meta.api.objects.User externalServiceUser);
    void processTextMessage(MessageRecord messageRecord);
    void saveOrModifyDocument(MessageRecord messageRecord);
    void saveOrModifyPhoto(MessageRecord messageRecord);
    MessageHistoryResponse getLastMessages(MessageHistoryRequest messageHistoryRequest);
    MessageHistoryResponse getAllMessages(MessageHistoryRequest messageHistoryRequest);
    void sendTextMessage(TextMessageRequest request, Long userId);
}
