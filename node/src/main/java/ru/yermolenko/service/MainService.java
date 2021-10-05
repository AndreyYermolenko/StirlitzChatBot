package ru.yermolenko.service;

import ru.yermolenko.model.*;

public interface MainService {
    void saveRawData(GeneralRecord generalRecord);
    User findOrSaveUser(org.telegram.telegrambots.meta.api.objects.User externalServiceUser);
    void saveOrModifyTextMessage(MessageRecord messageRecord);
    void saveOrModifyDocument(MessageRecord messageRecord);
    void saveOrModifyPhoto(MessageRecord messageRecord);
}
