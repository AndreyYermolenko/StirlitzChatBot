package ru.yermolenko.model;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
@Builder
public class MessageRecord {
    Message message;
    String botname;
}
