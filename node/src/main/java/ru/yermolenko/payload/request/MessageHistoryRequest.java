package ru.yermolenko.payload.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class MessageHistoryRequest {
    @NotBlank
    private Long userId;
    @NotNull
    private Long chatId;
    @NotNull
    private Integer limit;
}
