package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.payload.response.MessageResponse;
import ru.yermolenko.security.services.UserDetailsImpl;
import ru.yermolenko.service.MainService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/data/message")
@Log4j
public class MessageApiController {
	private final MainService mainService;

	public MessageApiController(MainService mainService) {
		this.mainService = mainService;
	}

	@GetMapping("/all")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<MessageHistoryResponse> getAllMessages(@RequestParam @NotBlank Long chatId) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		MessageHistoryResponse result = mainService.getAllMessages(
				MessageHistoryRequest.builder()
						.userId(userDetails.getId())
						.chatId(chatId)
						.build());
		if (result.hasError()) {
			return ResponseEntity.badRequest().body(result);
		} else {
			return ResponseEntity.ok().body(result);
		}
	}

	@GetMapping("/last_few")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<MessageHistoryResponse> getLastMessages(@RequestParam @NotBlank Long chatId,
											 @RequestParam @NotBlank Integer limit) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		MessageHistoryResponse result = mainService.getLastMessages(
				MessageHistoryRequest.builder()
						.userId(userDetails.getId())
						.chatId(chatId)
						.limit(limit)
						.build());
		if (result.hasError()) {
			return ResponseEntity.badRequest().body(result);
		} else {
			return ResponseEntity.ok().body(result);
		}
	}

	@PostMapping("/send")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> sendMessage(@Valid @RequestBody TextMessageRequest request) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		mainService.sendTextMessage(request, userDetails.getId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/delete")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<MessageResponse> deleteMessage(@RequestParam @NotBlank Long chatId,
										   @RequestParam @NotBlank Integer messageId) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		MessageResponse response = mainService.deleteTextMessage(chatId, messageId, userDetails.getId());
		if(!response.hasError()) {
			return ResponseEntity.ok().body(response);
		} else {
			return ResponseEntity.badRequest().body(response);
		}
	}
}
