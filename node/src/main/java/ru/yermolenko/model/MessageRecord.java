package ru.yermolenko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRecord {
    Message message;
    String botname;
}