package ru.yermolenko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralRecord {
    private Update update;
    private String botname;
}
