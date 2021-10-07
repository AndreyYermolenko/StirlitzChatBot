package ru.yermolenko.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.yermolenko.model.GeneralRecord;
import ru.yermolenko.model.MessageRecord;
import ru.yermolenko.model.enam.RabbitQueue;
import ru.yermolenko.service.RecordProducer;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static ru.yermolenko.controller.TestUpdates.*;
import static ru.yermolenko.model.enam.RabbitQueue.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class WebHookControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int randomServerPort;
    @MockBean
    private RecordProducer recordProducer;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Cache-Control", "no-cache");
        return headers;
    }

    private URI getUri() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + randomServerPort + "/";
        return new URI(baseUrl);
    }

    @Test
    void onUpdateReceivedMessageWithText() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(MESSAGE_WITH_TEXT, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(TEXT_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedForwardedMessage() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(FORWARDED_MESSAGE, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(TEXT_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedForwardedChannelMessage() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(FORWARDED_CHANNEL_MESSAGE, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(TEXT_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedMessageWithReply() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(MESSAGE_WITH_REPLY, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(TEXT_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedEditedMessage() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(EDITED_MESSAGE, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(TEXT_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedMessageWithEntities() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(MESSAGE_WITH_ENTITIES, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(TEXT_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedMessageWithAudio() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(MESSAGE_WITH_AUDIO, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(0))
                .produce(any(RabbitQueue.class),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedVoiceMessage() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(VOICE_MESSAGE, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(0))
                .produce(any(RabbitQueue.class),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedMessageWithDocument() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(MESSAGE_WITH_DOCUMENT, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(DOC_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedInlineQuery() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(INLINE_QUERY, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(0))
                .produce(any(RabbitQueue.class),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedChosenInlineQuery() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(CHOSEN_INLINE_QUERY, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(0))
                .produce(any(RabbitQueue.class),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedCallbackQuery() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(CALLBACK_QUERY, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(0))
                .produce(any(RabbitQueue.class),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedMessageWithPhoto() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(MESSAGE_WITH_PHOTO, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(PHOTO_MESSAGE_RECORD),
                        any(MessageRecord.class));
    }

    @Test
    void onUpdateReceivedNakedUpdate() throws URISyntaxException {
        HttpEntity<String> request = new HttpEntity<>(NAKED_UPDATE, getHeaders());
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUri(), request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(eq(GENERAL_RECORD),
                        any(GeneralRecord.class));
        Mockito.verify(recordProducer, Mockito.times(0))
                .produce(any(RabbitQueue.class),
                        any(MessageRecord.class));
    }
}
