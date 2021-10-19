package ru.yermolenko.service;

import ru.yermolenko.model.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;

public interface MainService {
    void saveRawData(GeneralRecord generalRecord);
    ServiceUser findOrSaveUser(org.telegram.telegrambots.meta.api.objects.User externalServiceUser);
    void saveOrModifyTextMessage(MessageRecord messageRecord);
    void saveOrModifyDocument(MessageRecord messageRecord);
    void saveOrModifyPhoto(MessageRecord messageRecord);
    MessageHistoryResponse getLastMessages(MessageHistoryRequest messageHistoryRequest);
    void sendTextMessage(TextMessageRequest request);
}
