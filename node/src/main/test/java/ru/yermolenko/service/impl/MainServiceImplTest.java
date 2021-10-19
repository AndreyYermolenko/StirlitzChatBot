package ru.yermolenko.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.yermolenko.dao.DataMessageDAO;
import ru.yermolenko.dao.ServiceUserDAO;
import ru.yermolenko.dao.UserApiKeyDAO;
import ru.yermolenko.model.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.service.BotService;
import ru.yermolenko.service.CollatzService;
import ru.yermolenko.service.MainService;
import ru.yermolenko.service.ProducerService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
class MainServiceImplTest {
    @Autowired
    private MainService mainService;
    @MockBean
    private DataMessageDAO dataMessageDAO;
    @MockBean
    private BotService botService;
    @MockBean
    private CollatzService collatzService;
    @MockBean
    private UserApiKeyDAO userApiKeyDAO;
    @MockBean
    private ServiceUserDAO serviceUserDAO;
    @MockBean
    private ProducerService producerService;

    /**
     * Test case for CollatzService.
     */
    @Test
    void saveOrModifyTextMessagePart1() {
        User user = new User();
        user.setId(45678);
        user.setUserName("vano");
        user.setFirstName("Ivan");
        user.setLastName("Susanin");

        Chat chat = new Chat();
        chat.setId(11111L);
        chat.setType("PRIVATE");

        Message message = new Message();
        message.setFrom(user);
        message.setMessageId(123456);
        message.setChat(chat);
        message.setDate(1441645532);
        message.setText("Surprize!!!");

        MessageRecord messageRecord = MessageRecord.builder()
                .message(message)
                .build();

        Mockito.doReturn("Пожалуйста, введите число!")
                .when(collatzService)
                .processInput(anyString());

        mainService.saveOrModifyTextMessage(messageRecord);

        Mockito.verify(dataMessageDAO, Mockito.times(1))
                .save(any(DataMessage.class));
        Mockito.verify(botService, Mockito.times(1))
                .getPersistentBot(eq(messageRecord.getBotname()));
        Mockito.verify(serviceUserDAO, Mockito.times(1))
                .findUserByExternalServiceId(eq(user.getId()));
        Mockito.verify(collatzService, Mockito.times(1))
                .processInput(anyString());
        Mockito.verify(producerService, Mockito.times(1))
                .produceAnswer(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Пожалуйста, введите число!")
                        .build());
    }

    /**
     * Test case for "get_api_key" cmd when api_key exist.
     */
    @Test
    void saveOrModifyTextMessagePart2() {
        String api_key = "some_api_key";

        User user = new User();
        user.setId(45678);
        user.setUserName("vano");
        user.setFirstName("Ivan");
        user.setLastName("Susanin");

        Chat chat = new Chat();
        chat.setId(11111L);
        chat.setType("PRIVATE");

        Message message = new Message();
        message.setFrom(user);
        message.setMessageId(123456);
        message.setChat(chat);
        message.setDate(1441645532);
        message.setText("/get_api_key");

        MessageRecord messageRecord = MessageRecord.builder()
                .message(message)
                .build();

        ServiceUser transientServiceUser = ServiceUser.builder()
                .externalServiceId(user.getId())
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        ServiceUser persistentServiceUser = ServiceUser.builder()
                .id(1L)
                .externalServiceId(transientServiceUser.getExternalServiceId())
                .username(transientServiceUser.getUsername())
                .firstName(transientServiceUser.getFirstName())
                .lastName(transientServiceUser.getLastName())
                .build();

        Mockito.doReturn(persistentServiceUser)
                .when(serviceUserDAO)
                .save(transientServiceUser);

        Mockito.doReturn(Optional.of(
                UserApiKey.builder()
                        .id(1L)
                        .apiKey(api_key)
                        .serviceUser(persistentServiceUser)
                        .build())
                )
                .when(userApiKeyDAO)
                .findByServiceUser(persistentServiceUser);

        mainService.saveOrModifyTextMessage(messageRecord);

        Mockito.verify(dataMessageDAO, Mockito.times(1))
                .save(any(DataMessage.class));
        Mockito.verify(botService, Mockito.times(1))
                .getPersistentBot(eq(messageRecord.getBotname()));
        Mockito.verify(serviceUserDAO, Mockito.times(1))
                .save(eq(transientServiceUser));
        Mockito.verify(collatzService, Mockito.times(0))
                .processInput(anyString());
        Mockito.verify(userApiKeyDAO, Mockito.times(1))
                .findByServiceUser(eq(persistentServiceUser));
        Mockito.verify(userApiKeyDAO, Mockito.times(0))
                .save(any(UserApiKey.class));
        Mockito.verify(producerService, Mockito.times(1))
                .produceAnswer(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Your api key: " + api_key)
                        .build());
    }

    /**
     * Test case for "get_api_key" cmd when api_key absence.
     */
    @Test
    void saveOrModifyTextMessagePart3() {
        User user = new User();
        user.setId(45678);
        user.setUserName("vano");
        user.setFirstName("Ivan");
        user.setLastName("Susanin");

        Chat chat = new Chat();
        chat.setId(11111L);
        chat.setType("PRIVATE");

        Message message = new Message();
        message.setFrom(user);
        message.setMessageId(123456);
        message.setChat(chat);
        message.setDate(1441645532);
        message.setText("/get_api_key");

        MessageRecord messageRecord = MessageRecord.builder()
                .message(message)
                .build();

        ServiceUser transientServiceUser = ServiceUser.builder()
                .externalServiceId(user.getId())
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        ServiceUser persistentServiceUser = ServiceUser.builder()
                .id(1L)
                .externalServiceId(transientServiceUser.getExternalServiceId())
                .username(transientServiceUser.getUsername())
                .firstName(transientServiceUser.getFirstName())
                .lastName(transientServiceUser.getLastName())
                .build();

        Mockito.doReturn(persistentServiceUser)
                .when(serviceUserDAO)
                .save(transientServiceUser);

        Mockito.doReturn(null)
                .when(userApiKeyDAO)
                .save(UserApiKey.builder()
                        .serviceUser(persistentServiceUser)
                        .build());

        mainService.saveOrModifyTextMessage(messageRecord);

        Mockito.verify(dataMessageDAO, Mockito.times(1))
                .save(any(DataMessage.class));
        Mockito.verify(botService, Mockito.times(1))
                .getPersistentBot(eq(messageRecord.getBotname()));
        Mockito.verify(serviceUserDAO, Mockito.times(1))
                .save(eq(transientServiceUser));
        Mockito.verify(collatzService, Mockito.times(0))
                .processInput(anyString());
        Mockito.verify(userApiKeyDAO, Mockito.times(1))
                .findByServiceUser(eq(persistentServiceUser));
        Mockito.verify(userApiKeyDAO, Mockito.times(1))
                .save(any(UserApiKey.class));
        Mockito.verify(producerService, Mockito.times(1))
                .produceAnswer(any(SendMessage.class));
    }

    /**
     * Test case for unknown command.
     */
    @Test
    void saveOrModifyTextMessagePart4() {
        User user = new User();
        user.setId(45678);
        user.setUserName("vano");
        user.setFirstName("Ivan");
        user.setLastName("Susanin");

        Chat chat = new Chat();
        chat.setId(11111L);
        chat.setType("PRIVATE");

        Message message = new Message();
        message.setFrom(user);
        message.setMessageId(123456);
        message.setChat(chat);
        message.setDate(1441645532);
        message.setText("/abra_kadabra");

        MessageRecord messageRecord = MessageRecord.builder()
                .message(message)
                .build();

        ServiceUser transientServiceUser = ServiceUser.builder()
                .externalServiceId(user.getId())
                .username(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

        mainService.saveOrModifyTextMessage(messageRecord);

        Mockito.verify(dataMessageDAO, Mockito.times(1))
                .save(any(DataMessage.class));
        Mockito.verify(botService, Mockito.times(1))
                .getPersistentBot(eq(messageRecord.getBotname()));
        Mockito.verify(serviceUserDAO, Mockito.times(1))
                .save(eq(transientServiceUser));
        Mockito.verify(collatzService, Mockito.times(0))
                .processInput(anyString());
        Mockito.verify(userApiKeyDAO, Mockito.times(0))
                .findByServiceUser(any(ServiceUser.class));
        Mockito.verify(userApiKeyDAO, Mockito.times(0))
                .save(any(UserApiKey.class));
        Mockito.verify(producerService, Mockito.times(1))
                .produceAnswer(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Unknown command!")
                        .build());
    }

    /**
     * API key isn't found.
     */
    @Test
    void getLastMessagesPart1() {
        String api_key = "some_api_key";
        MessageHistoryRequest request = MessageHistoryRequest.builder()
                .userApiKey(api_key)
                .chatId(11111L)
                .limit(5)
                .build();

        MessageHistoryResponse expectedResponse = MessageHistoryResponse.builder()
                .error(true)
                .errorMessage("API key isn't found!")
                .build();

        Mockito.doReturn(Optional.empty())
                .when(userApiKeyDAO)
                .findByApiKey(api_key);

        MessageHistoryResponse response = mainService.getLastMessages(request);
        assertEquals(expectedResponse, response);

        Mockito.verify(userApiKeyDAO, Mockito.times(1))
                .findByApiKey(eq(api_key));
    }

    /**
     * Messages aren't found.
     */
    @Test
    void getLastMessagesPart2() {
        String api_key = "some_api_key";
        MessageHistoryRequest request = MessageHistoryRequest.builder()
                .userApiKey(api_key)
                .chatId(11111L)
                .limit(5)
                .build();

        MessageHistoryResponse expectedResponse = MessageHistoryResponse.builder()
                .error(true)
                .errorMessage("Messages aren't found!")
                .build();

        Mockito.doReturn(Optional.of(new UserApiKey()))
                .when(userApiKeyDAO)
                .findByApiKey(api_key);
        Mockito.doReturn(Optional.empty())
                .when(dataMessageDAO)
                .findLastMessagesByChatId(request.getChatId(), request.getLimit());

        MessageHistoryResponse response = mainService.getLastMessages(request);
        assertEquals(expectedResponse, response);

        Mockito.verify(userApiKeyDAO, Mockito.times(1))
                .findByApiKey(eq(api_key));
        Mockito.verify(dataMessageDAO, Mockito.times(1))
                .findLastMessagesByChatId(request.getChatId(), request.getLimit());
    }

    /**
     * Wrong chatId.
     */
    @Test
    void getLastMessagesPart3() {
        String api_key = "some_api_key";
        MessageHistoryRequest request = MessageHistoryRequest.builder()
                .userApiKey(api_key)
                .chatId(11111L)
                .limit(5)
                .build();

        ServiceUser persistentServiceUser = ServiceUser.builder()
                .id(1L)
                .externalServiceId(45678)
                .username("vano")
                .firstName("Ivan")
                .lastName("Susanin")
                .build();

        MessageHistoryResponse expectedResponse = MessageHistoryResponse.builder()
                .error(true)
                .errorMessage("Wrong chatId!")
                .build();

        DataMessage message1 = DataMessage.builder()
                .serviceUser(ServiceUser.builder().id(2L).build())
                .build();
        DataMessage message2 = DataMessage.builder()
                .serviceUser(ServiceUser.builder().id(1L).build())
                .build();
        List<DataMessage> dataMessages = Arrays.asList(message1, message2);

        Mockito.doReturn(Optional.of(UserApiKey.builder()
                        .id(1L)
                        .apiKey(api_key)
                        .serviceUser(persistentServiceUser)
                        .build()))
                .when(userApiKeyDAO)
                .findByApiKey(api_key);
        Mockito.doReturn(Optional.of(dataMessages))
                .when(dataMessageDAO)
                .findLastMessagesByChatId(request.getChatId(), request.getLimit());

        MessageHistoryResponse response = mainService.getLastMessages(request);
        assertEquals(expectedResponse, response);

        Mockito.verify(userApiKeyDAO, Mockito.times(1))
                .findByApiKey(eq(api_key));
        Mockito.verify(dataMessageDAO, Mockito.times(1))
                .findLastMessagesByChatId(request.getChatId(), request.getLimit());
    }

    /**
     * Success.
     */
    @Test
    void getLastMessagesPart4() {
        String api_key = "some_api_key";
        MessageHistoryRequest request = MessageHistoryRequest.builder()
                .userApiKey(api_key)
                .chatId(11111L)
                .limit(5)
                .build();

        ServiceUser persistentServiceUser = ServiceUser.builder()
                .id(1L)
                .externalServiceId(45678)
                .username("vano")
                .firstName("Ivan")
                .lastName("Susanin")
                .build();

        DataMessage message1 = DataMessage.builder()
                .serviceUser(ServiceUser.builder().id(1L).build())
                .build();
        DataMessage message2 = DataMessage.builder()
                .serviceUser(ServiceUser.builder().id(1L).build())
                .build();
        List<DataMessage> dataMessages = Arrays.asList(message1, message2);
        MessageHistoryResponse expectedResponse = MessageHistoryResponse.builder()
                .error(false)
                .messages(dataMessages)
                .build();

        Mockito.doReturn(Optional.of(UserApiKey.builder()
                        .id(1L)
                        .apiKey(api_key)
                        .serviceUser(persistentServiceUser)
                        .build()))
                .when(userApiKeyDAO)
                .findByApiKey(api_key);
        Mockito.doReturn(Optional.of(dataMessages))
                .when(dataMessageDAO)
                .findLastMessagesByChatId(request.getChatId(), request.getLimit());

        MessageHistoryResponse response = mainService.getLastMessages(request);
        assertEquals(expectedResponse, response);

        Mockito.verify(userApiKeyDAO, Mockito.times(1))
                .findByApiKey(eq(api_key));
        Mockito.verify(dataMessageDAO, Mockito.times(1))
                .findLastMessagesByChatId(request.getChatId(), request.getLimit());
    }
}
