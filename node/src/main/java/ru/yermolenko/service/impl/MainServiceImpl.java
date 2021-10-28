package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.yermolenko.dao.DataMessageDAO;
import ru.yermolenko.dao.RawDataDAO;
import ru.yermolenko.dao.AppUserDAO;
import ru.yermolenko.dao.ApiKeyDAO;
import ru.yermolenko.model.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.service.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.yermolenko.model.ServiceCommand.GET_API_KEY;
import static ru.yermolenko.model.ServiceCommand.GET_CHAT_ID;
import static ru.yermolenko.model.UserState.*;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final AppUserDAO appUserDAO;
    private final DataMessageDAO dataMessageDAO;
    private final RawDataDAO rawDataDAO;
    private final BotService botService;
    private final ProducerService producerService;
    private final CollatzService collatzService;
    private final FileService fileService;
    private final ApiKeyDAO apiKeyDAO;

    public MainServiceImpl(AppUserDAO appUserDAO, DataMessageDAO dataMessageDAO, RawDataDAO rawDataDAO,
                           BotService botService, ProducerService producerService,
                           CollatzService collatzService, FileService fileService, ApiKeyDAO apiKeyDAO) {
        this.appUserDAO = appUserDAO;
        this.dataMessageDAO = dataMessageDAO;
        this.rawDataDAO = rawDataDAO;
        this.botService = botService;
        this.producerService = producerService;
        this.collatzService = collatzService;
        this.fileService = fileService;
        this.apiKeyDAO = apiKeyDAO;
    }

    @Override
    public AppUser findOrSaveUser(org.telegram.telegrambots.meta.api.objects.User externalServiceUser) {
        AppUser persistentServiceUser = appUserDAO.findUserByExternalServiceId(externalServiceUser.getId());
        if (persistentServiceUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .externalServiceId(externalServiceUser.getId())
                    .username(externalServiceUser.getUserName())
                    .firstName(externalServiceUser.getFirstName())
                    .lastName(externalServiceUser.getLastName())
                    .isActive(false)
                    .state(BASIC)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentServiceUser;
    }

    private DataMessage findOrCreateDataMessage(MessageRecord messageRecord) {
        Message message = messageRecord.getMessage();
        DataMessage persistentDataMessage = dataMessageDAO.findMessageByExternalServiceId(
                message.getMessageId());
        if (persistentDataMessage == null) {
            AppUser persistentAppUser = findOrSaveUser(messageRecord.getMessage().getFrom());
            return DataMessage.builder()
                    .externalServiceId(message.getMessageId())
                    .appUser(persistentAppUser)
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
    public void processTextMessage(MessageRecord messageRecord) {
        DataMessage dataMessage = saveOrModifyTextMessage(messageRecord);
        String output;
        UserState state = dataMessage.getAppUser().getState();

        log.debug("State : " + state);
        log.debug("isActive : " + dataMessage.getAppUser().getIsActive());

        if (WAIT_FOR_EMAIL.equals(state)) {
            output = handleWaitForEmailState(dataMessage);
        } else if (WAIT_FOR_PASS.equals(state)) {
            output = handleWaitForPassState(dataMessage);
        } else {
            output = handleBasicState(dataMessage);
        }

        sendAnswer(output, messageRecord.getMessage().getChatId().toString());
    }

    private String handleBasicState(DataMessage dataMessage) {
        String output;
        String text = dataMessage.getMessageText();

        if (text != null && text.startsWith("/")) {
            output = processServiceCommand(dataMessage);
        } else {
            output = collatzService.processInput(dataMessage.getMessageText());
        }
        return output;
    }

    private String handleWaitForEmailState(DataMessage dataMessage) {
        return "";
    }

    private String handleWaitForPassState(DataMessage dataMessage) {
        return "";
    }

    private DataMessage saveOrModifyTextMessage(MessageRecord messageRecord) {
        DataMessage dataMessage = findOrCreateDataMessage(messageRecord);
        dataMessage.setMessageText(messageRecord.getMessage().getText());
        log.debug("\n Message: " + dataMessage);
        return dataMessageDAO.save(dataMessage);
    }

    private String processServiceCommand(DataMessage dataMessage) {
        if (GET_API_KEY.equals(dataMessage.getMessageText())) {
            return getOrGenerateApiKey(dataMessage);
        } else if (GET_CHAT_ID.equals(dataMessage.getMessageText())) {
            return getChatId(dataMessage);
        } else {
            return "Unknown command!";
        }
    }

    private String getOrGenerateApiKey(DataMessage dataMessage) {
        Optional<ApiKey> userApiKey = apiKeyDAO.findByAppUser(dataMessage.getAppUser());
        String apiKey;
        if (userApiKey.isPresent()) {
            apiKey = userApiKey.get().getApiKey();
        } else {
            apiKey = UUID.randomUUID().toString();
            ApiKey newApiKey = ApiKey.builder()
                    .appUser(dataMessage.getAppUser())
                    .apiKey(apiKey)
                    .build();
            apiKeyDAO.save(newApiKey);
        }
        return "Your api key: " + apiKey;
    }

    private String getChatId(DataMessage dataMessage) {
        return "This chat id : " + dataMessage.getChatId();
    }

    @Override
    public void saveOrModifyDocument(MessageRecord messageRecord) {
        Document doc = fileService.processDoc(messageRecord.getMessage());
        DataMessage dataMessage = findOrCreateDataMessage(messageRecord);
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
        DataMessage dataMessage = findOrCreateDataMessage(messageRecord);
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
        Optional<ApiKey> optUserApiKey = apiKeyDAO.findByApiKey(
                messageHistoryRequest.getUserApiKey());
        if (optUserApiKey.isEmpty()) {
            return MessageHistoryResponse.builder()
                    .error(true)
                    .errorMessage("API key isn't found!")
                    .build();
        }
        Optional<List<DataMessage>> optDataMessages = dataMessageDAO.findLastMessagesByChatId(
                messageHistoryRequest.getChatId(),
                messageHistoryRequest.getLimit());
        if (optDataMessages.isEmpty()) {
            return MessageHistoryResponse.builder()
                    .error(true)
                    .errorMessage("Messages aren't found!")
                    .build();
        } else {
            Long currentServiceUserId = optUserApiKey.get().getAppUser().getId();
            boolean isCurrentServiceUserMessages = optDataMessages.get().stream().allMatch(x ->
                    x.getAppUser().getId().equals(currentServiceUserId));
            if (isCurrentServiceUserMessages) {
                return MessageHistoryResponse.builder().messages(optDataMessages.get()).build();
            } else {
                return MessageHistoryResponse.builder()
                        .error(true)
                        .errorMessage("Wrong chatId!")
                        .build();
            }
        }
    }

    @Override
    public void sendTextMessage(TextMessageRequest request) {
        sendAnswer(request.getMessage(), request.getChatId().toString());
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
