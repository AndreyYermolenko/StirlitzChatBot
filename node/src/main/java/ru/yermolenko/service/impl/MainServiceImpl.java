package ru.yermolenko.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.yermolenko.dao.AppUserDAO;
import ru.yermolenko.dao.DataMessageDAO;
import ru.yermolenko.dao.RawDataDAO;
import ru.yermolenko.dao.RoleDAO;
import ru.yermolenko.model.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.service.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.yermolenko.model.ServiceCommand.*;
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
    private final RoleDAO roleDAO;
    private final AppUserService appUserService;

    public MainServiceImpl(AppUserDAO appUserDAO, DataMessageDAO dataMessageDAO, RawDataDAO rawDataDAO,
                           BotService botService, ProducerService producerService,
                           CollatzService collatzService, FileService fileService, RoleDAO roleDAO, AppUserService appUserService) {
        this.appUserDAO = appUserDAO;
        this.dataMessageDAO = dataMessageDAO;
        this.rawDataDAO = rawDataDAO;
        this.botService = botService;
        this.producerService = producerService;
        this.collatzService = collatzService;
        this.fileService = fileService;
        this.roleDAO = roleDAO;
        this.appUserService = appUserService;
    }

    @Override
    public AppUser findOrSaveUser(org.telegram.telegrambots.meta.api.objects.User externalServiceUser) {
        AppUser persistentServiceUser = appUserDAO.findUserByExternalServiceId(externalServiceUser.getId());
        if (persistentServiceUser == null) {
            Role userRole = roleDAO.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role is not found."));
            AppUser transientAppUser = AppUser.builder()
                    .externalServiceId(externalServiceUser.getId())
                    .username(externalServiceUser.getUserName())
                    .firstName(externalServiceUser.getFirstName())
                    .lastName(externalServiceUser.getLastName())
                    .roles(Set.of(userRole))
                    .isActive(false)
                    .state(BASIC_STATE)
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
        UserState state = dataMessage.getAppUser().getState();
        String text = dataMessage.getMessageText();
        String output;

        if (ABORT.equals(text)) {
            output = abortProcess(dataMessage).getMessage();
        } else if (BASIC_STATE.equals(state)) {
            output = processServiceCommand(dataMessage);
        } else if (WAIT_FOR_EMAIL_STATE.equals(state)) {
            output = appUserService.setEmail(dataMessage).getMessage();
        } else if (WAIT_FOR_PASSWORD_STATE.equals(state)) {
            output = appUserService.setPassword(dataMessage).getMessage();
        } else if (COLLATZ_STATE.equals(state)) {
            output = collatzService.processInput(text);
        } else {
            output = "Неизвестная ошибка! Введите /abort и попробуйте снова!";
        }

        sendAnswer(output, messageRecord.getMessage().getChatId().toString());
    }

    private DataMessage saveOrModifyTextMessage(MessageRecord messageRecord) {
        DataMessage dataMessage = findOrCreateDataMessage(messageRecord);
        dataMessage.setMessageText(messageRecord.getMessage().getText());
        log.debug("\n Message: " + dataMessage);
        return dataMessageDAO.save(dataMessage);
    }

    private String processServiceCommand(DataMessage dataMessage) {
        AppUser appUser = dataMessage.getAppUser();
        String cmd = dataMessage.getMessageText();
        if (GET_CHAT_ID.equals(cmd)) {
            return getChatId(dataMessage);
        } else if (REGISTRATION.equals(cmd)){
            MessageResponse response = appUserService.registerUser(dataMessage.getAppUser());
            if (!response.hasError()) {
                changeUserState(appUser, WAIT_FOR_EMAIL_STATE);
            }
            return response.getMessage();
        } else if (COLLATZ.equals(cmd)) {
            changeUserState(appUser, COLLATZ_STATE);
            return "Начнём... Введите, пожалуйста, число!";
        } else if (HELP.equals(cmd)) {
            return help();
        } else {
            return "Неизвестная команда! Чтобы узнать список доступных комманд введите /help";
        }
    }

//    private String getOrGenerateApiKey(DataMessage dataMessage) {
//        AppUser appUser = dataMessage.getAppUser();
//        if (appUser.getIsActive()) {
//            Optional<ApiKey> userApiKey = apiKeyDAO.findByAppUser(dataMessage.getAppUser());
//            String apiKey;
//            if (userApiKey.isPresent()) {
//                apiKey = userApiKey.get().getApiKey();
//            } else {
//                apiKey = UUID.randomUUID().toString();
//                ApiKey newApiKey = ApiKey.builder()
//                        .appUser(dataMessage.getAppUser())
//                        .apiKey(apiKey)
//                        .build();
//                apiKeyDAO.save(newApiKey);
//            }
//            return "Ваш api key: " + apiKey;
//        } else {
//            return "Команда доступна только для зарегистрированных пользователей!\n" +
//                    "Введите /registration и пройдите простую регистрацию.";
//        }
//    }

    private String getChatId(DataMessage dataMessage) {
        return "Chat id : " + dataMessage.getChatId();
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
    public MessageHistoryResponse getLastMessages(MessageHistoryRequest request) {
        Optional<List<DataMessage>> optDataMessages = dataMessageDAO.findLastMessagesByChatId(
                request.getChatId(),
                request.getLimit());
        return getFilterMessages(optDataMessages, request.getUserId());
    }

    @Override
    public MessageHistoryResponse getAllMessages(MessageHistoryRequest request) {
        Long chatId = request.getChatId();
        Optional<List<DataMessage>> optDataMessages = dataMessageDAO.findAllByChatId(chatId);
        return getFilterMessages(optDataMessages, request.getUserId());
    }

    private MessageHistoryResponse getFilterMessages(Optional<List<DataMessage>> optDataMessages,
                                                     Long currentServiceUserId) {
        if (optDataMessages.isEmpty()) {
            return MessageHistoryResponse.builder()
                    .error(true)
                    .errorMessage("Messages aren't found!")
                    .build();
        } else {
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
    public void sendTextMessage(TextMessageRequest request, Long userId) {
        AppUser appUser = appUserDAO.findById(userId).get();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(request.getChatId().toString());
        sendMessage.setText(appUser.getFirstName() + " " + appUser.getLastName() + ": " + request.getMessage());
        sendAnswer(sendMessage);
    }

    @Override
    public MessageResponse deleteTextMessage(Long chatId, Integer messageId, Long userId) {
        DataMessage message = dataMessageDAO.findMessageByExternalServiceId(messageId);
        if (message != null && userId.equals(message.getAppUser().getId())) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId.toString());
            deleteMessage.setMessageId(messageId);
            sendAnswer(deleteMessage);
            return MessageResponse.builder()
                    .error(false)
                    .message("Message was deleted!")
                    .build();
        } else {
            return MessageResponse.builder()
                    .error(true)
                    .message("Message not found!")
                    .build();
        }
    }

    public void saveRawData(GeneralRecord generalRecord) {
        RawData rawData = RawData.builder()
                .event(generalRecord.getUpdate().toString())
                .bot(botService.getPersistentBot(generalRecord.getBotname()))
                .build();
        rawDataDAO.save(rawData);
    }

    private void sendAnswer(SendMessage message) {
        producerService.produceAnswer(message);
    }

    private void sendAnswer(String answer, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        producerService.produceAnswer(sendMessage);
    }

    private void sendAnswer(DeleteMessage message) {
        producerService.produceAnswer(message);
    }

    private void changeUserState(AppUser appUser, UserState newState) {
        appUser.setState(newState);
        appUserDAO.save(appUser);
    }

    private MessageResponse abortProcess(DataMessage dataMessage) {
        AppUser appUser = dataMessage.getAppUser();
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return MessageResponse.builder()
                .error(false)
                .message("Команда отменена!")
                .build();
    }

    private String help() {
        return "Список доступных команд:\n" +
                "/abort - отмена выполнения текущей команды;\n" +
                "/registration - регистрация пользователя для работы с API;\n" +
                "/get_chat_id - получение идентификатора текущего чата (нужно для работы с некоторым API)\n" +
                "/collatz - запуск collatz сервиса.";
    }
}
