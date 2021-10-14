package ru.yermolenko.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
	@Getter
	@Setter
	private String message;
	@JsonIgnore
	private Boolean error;

	public boolean hasError() {
		return this.error;
	}
}
