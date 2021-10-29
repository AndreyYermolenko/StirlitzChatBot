package ru.yermolenko.service;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.yermolenko.model.AppUser;
import ru.yermolenko.model.GeneralRecord;
import ru.yermolenko.model.MessageRecord;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.payload.response.MessageResponse;

public interface MainService {
    void saveRawData(GeneralRecord generalRecord);
    AppUser findOrSaveUser(User user);
    void processTextMessage(MessageRecord messageRecord);
    void saveOrModifyDocument(MessageRecord messageRecord);
    void saveOrModifyPhoto(MessageRecord messageRecord);
    MessageHistoryResponse getLastMessages(MessageHistoryRequest messageHistoryRequest);
    MessageHistoryResponse getAllMessages(MessageHistoryRequest messageHistoryRequest);
    void sendTextMessage(TextMessageRequest request, Long userId);
    MessageResponse deleteTextMessage(Long chatId, Integer messageId, Long userId);
}
