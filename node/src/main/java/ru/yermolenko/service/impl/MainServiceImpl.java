package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.yermolenko.dao.DataMessageDAO;
import ru.yermolenko.dao.RawDataDAO;
import ru.yermolenko.dao.ServiceUserDAO;
import ru.yermolenko.dao.UserApiKeyDAO;
import ru.yermolenko.model.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.service.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final ServiceUserDAO serviceUserDAO;
    private final DataMessageDAO dataMessageDAO;
    private final RawDataDAO rawDataDAO;
    private final BotService botService;
    private final ProducerService producerService;
    private final CollatzService collatzService;
    private final FileService fileService;
    private final UserApiKeyDAO userApiKeyDAO;

    public MainServiceImpl(ServiceUserDAO serviceUserDAO, DataMessageDAO dataMessageDAO, RawDataDAO rawDataDAO,
                           BotService botService, ProducerService producerService,
                           CollatzService collatzService, FileService fileService, UserApiKeyDAO userApiKeyDAO) {
        this.serviceUserDAO = serviceUserDAO;
        this.dataMessageDAO = dataMessageDAO;
        this.rawDataDAO = rawDataDAO;
        this.botService = botService;
        this.producerService = producerService;
        this.collatzService = collatzService;
        this.fileService = fileService;
        this.userApiKeyDAO = userApiKeyDAO;
    }

    @Override
    public ServiceUser findOrSaveUser(org.telegram.telegrambots.meta.api.objects.User externalServiceUser) {
        ServiceUser persistentServiceUser = serviceUserDAO.findUserByExternalServiceId(externalServiceUser.getId());
        if (persistentServiceUser == null) {
            ServiceUser transientServiceUser = ServiceUser.builder()
                    .externalServiceId(externalServiceUser.getId())
                    .username(externalServiceUser.getUserName())
                    .firstName(externalServiceUser.getFirstName())
                    .lastName(externalServiceUser.getLastName())
                    .build();
            return serviceUserDAO.save(transientServiceUser);
        }
        return persistentServiceUser;
    }

    private DataMessage prepareDataMessage(MessageRecord messageRecord) {
        Message message = messageRecord.getMessage();
        DataMessage persistentDataMessage = dataMessageDAO.findMessageByExternalServiceId(
                message.getMessageId());
        if (persistentDataMessage == null) {
            ServiceUser persistentServiceUser = findOrSaveUser(messageRecord.getMessage().getFrom());
            return DataMessage.builder()
                    .externalServiceId(message.getMessageId())
                    .serviceUser(persistentServiceUser)
                    .chatId(message.getChatId())
                    .chatType(ChatType.valueOf(message.getChat().getType().toUpperCase()))
                    .unixTimeFromExternalService(message.getDate())
                    .bot(botService.getPersistentBot(messageRecord.getBotname()))
                    .build();
        } else {
            return persistentDataMessage;
        }
    }

    @Override
    public void saveOrModifyTextMessage(MessageRecord messageRecord) {
        DataMessage dataMessage = prepareDataMessage(messageRecord);
        dataMessage.setMessageText(messageRecord.getMessage().getText());
        log.debug("\n Message: " + dataMessage);
        dataMessageDAO.save(dataMessage);
        String output;
        if (dataMessage.getMessageText() != null
                && dataMessage.getMessageText().startsWith("/")) {
            output = processServiceCommand(dataMessage);
        } else {
            output = collatzService.processInput(dataMessage.getMessageText());
        }
        sendAnswer(output, messageRecord.getMessage().getChatId().toString());
    }

    private String processServiceCommand(DataMessage dataMessage) {
        if ("/get_api_key".equals(dataMessage.getMessageText())) {
            return getOrGenerateApiKey(dataMessage);
        } else {
            return "Unknown command!";
        }
    }

    private String getOrGenerateApiKey(DataMessage dataMessage) {
        Optional<UserApiKey> userApiKey = userApiKeyDAO.findByServiceUser(dataMessage.getServiceUser());
        String apiKey;
        if (userApiKey.isPresent()) {
            apiKey = userApiKey.get().getApiKey();
        } else {
            apiKey = UUID.randomUUID().toString();
            UserApiKey newUserApiKey = UserApiKey.builder()
                    .serviceUser(dataMessage.getServiceUser())
                    .apiKey(apiKey)
                    .build();
            userApiKeyDAO.save(newUserApiKey);
        }
        return "Your api key: " + apiKey;
    }

    @Override
    public void saveOrModifyDocument(MessageRecord messageRecord) {
        Document doc = fileService.processDoc(messageRecord.getMessage());
        DataMessage dataMessage = prepareDataMessage(messageRecord);
        dataMessage.setDoc(doc);
        if (doc != null) {
            dataMessageDAO.save(dataMessage);
            String link = "File link! " + fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            sendAnswer(link, messageRecord.getMessage().getChatId().toString());
        } else {
            String error = "Sorry. This operation is failed. Try later.";
            sendAnswer(error, messageRecord.getMessage().getChatId().toString());
        }
    }

    @Override
    public void saveOrModifyPhoto(MessageRecord messageRecord) {
        Photo photo = fileService.processPhoto(messageRecord.getMessage());
        DataMessage dataMessage = prepareDataMessage(messageRecord);
        dataMessage.setPhoto(photo);
        if (photo != null) {
            dataMessageDAO.save(dataMessage);
            String link = "File link! " + fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            sendAnswer(link, messageRecord.getMessage().getChatId().toString());
        } else {
            String error = "Sorry. This operation is failed. Try later.";
            sendAnswer(error, messageRecord.getMessage().getChatId().toString());
        }
    }

    @Override
    public MessageHistoryResponse getLastMessages(MessageHistoryRequest messageHistoryRequest) {
        Optional<UserApiKey> userApiKey = userApiKeyDAO.findByApiKey(
                messageHistoryRequest.getUserApiKey());
        if (userApiKey.isEmpty()) {
            return MessageHistoryResponse.builder()
                    .error(true)
                    .errorMessage("API key isn't found!")
                    .build();
        }
        Optional<List<DataMessage>> dataMessages = dataMessageDAO.findLastMessagesByChatId(
                messageHistoryRequest.getChatId(),
                messageHistoryRequest.getLimit());
        if (dataMessages.isEmpty()) {
            return MessageHistoryResponse.builder()
                    .error(true)
                    .errorMessage("Messages aren't found!")
                    .build();
        } else {
            return MessageHistoryResponse.builder().messages(dataMessages.get()).build();
        }
    }

    public void saveRawData(GeneralRecord generalRecord) {
        RawData rawData = RawData.builder()
                .event(generalRecord.getUpdate().toString())
                .bot(botService.getPersistentBot(generalRecord.getBotname()))
                .build();
        rawDataDAO.save(rawData);
    }

    private void sendAnswer(String answer, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        producerService.produceAnswer(sendMessage);
    }
}
