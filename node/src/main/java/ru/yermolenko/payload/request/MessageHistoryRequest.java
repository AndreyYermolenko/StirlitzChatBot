package ru.yermolenko.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageHistoryRequest {
    @NotBlank
    private String userApiKey;
    @NotNull
    private Long chatId;
    @NotNull
    private Integer limit;
}
