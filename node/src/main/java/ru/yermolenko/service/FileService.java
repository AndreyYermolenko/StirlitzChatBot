package ru.yermolenko.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.yermolenko.model.Document;
import ru.yermolenko.model.LinkType;
import ru.yermolenko.model.Photo;

public interface FileService {
    Document processDoc(Message externalMessage);
    Photo processPhoto(Message externalMessage);
    Document getDocument(String id);
    Photo getPhoto(String id);
    String generateLink(Long persistenceDocId, LinkType linkType);
}
