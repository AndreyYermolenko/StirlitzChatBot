package ru.yermolenko.payload.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class TextMessageRequest {
    @NotNull
    private Long chatId;
    @NotNull
    private String message;
}
