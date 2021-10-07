package ru.yermolenko.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yermolenko.service.CollatzService;

@SpringBootTest
@ActiveProfiles("dev")
class CollatzServiceImplTest {
    @Autowired
    private CollatzService collatzService;

    @Test
    void processInput() {
        Assertions.assertEquals(
                "{maxValue=9232, countOfSteps=111}",
                collatzService.processInput("27"));
        Assertions.assertEquals(
                "Пожалуйста, введите число!",
                collatzService.processInput("aaa"));
        Assertions.assertEquals(
                "{maxValue=0, countOfSteps=0}",
                collatzService.processInput("-1"));
    }
}
