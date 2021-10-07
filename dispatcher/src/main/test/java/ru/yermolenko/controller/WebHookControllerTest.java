package ru.yermolenko.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
import ru.yermolenko.model.MessageRecord;
import ru.yermolenko.model.enam.RabbitQueue;
import ru.yermolenko.service.RecordProducer;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class WebHookControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int randomServerPort;
    @MockBean
    private RecordProducer recordProducer;

    @Test
    void onUpdateReceived() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + randomServerPort + "/";
        URI uri = new URI(baseUrl);
        String data = "{\n" +
                "\"update_id\":10000,\n" +
                "\"message\":{\n" +
                "  \"date\":1441645532,\n" +
                "  \"chat\":{\n" +
                "     \"last_name\":\"Test Lastname\",\n" +
                "     \"id\":1111111,\n" +
                "     \"type\": \"private\",\n" +
                "     \"first_name\":\"Test Firstname\",\n" +
                "     \"username\":\"Testusername\"\n" +
                "  },\n" +
                "  \"message_id\":1365,\n" +
                "  \"from\":{\n" +
                "     \"last_name\":\"Test Lastname\",\n" +
                "     \"id\":1111111,\n" +
                "     \"first_name\":\"Test Firstname\",\n" +
                "     \"username\":\"Testusername\"\n" +
                "  },\n" +
                "  \"text\":\"27\"\n" +
                "}\n" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Cache-Control", "no-cache");

        HttpEntity<String> request = new HttpEntity<>(data, headers);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);
        assertEquals(200, result.getStatusCodeValue());

        Mockito.verify(recordProducer, Mockito.times(1))
                .produce(ArgumentMatchers.any(RabbitQueue.class),
                        ArgumentMatchers.any(MessageRecord.class));
    }
}
