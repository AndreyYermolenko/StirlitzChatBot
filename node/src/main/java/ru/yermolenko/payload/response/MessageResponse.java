package ru.yermolenko.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@Data
public class MessageResponse {
	private String message;
	private String linkForConfirmation;
	@JsonIgnore
	private Boolean error;

	public boolean hasError() {
		return this.error;
	}
}
