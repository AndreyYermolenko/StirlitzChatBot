package ru.yermolenko.model;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Builder
public class GeneralRecord {
    private Update update;
    private String botname;
}
