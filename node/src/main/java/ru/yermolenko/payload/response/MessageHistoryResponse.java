package ru.yermolenko.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yermolenko.model.DataMessage;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MessageHistoryResponse {
    private List<DataMessage> messages;
    @JsonIgnore
    private boolean error;
    private String errorMessage;

    public boolean hasError() {
        return this.error;
    }
}
