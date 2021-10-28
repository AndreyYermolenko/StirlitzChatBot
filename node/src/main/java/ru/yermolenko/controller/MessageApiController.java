package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
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
	public ResponseEntity<?> getAllMessages(@RequestParam @NotBlank Long chatId) {
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
	public ResponseEntity<?> getLastMessages(@RequestParam @NotBlank Long chatId,
											 @RequestParam @NotBlank Integer limit) {
		log.debug("chatId " + chatId);
		log.debug("limit " + limit);
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
		log.debug(request);
		mainService.sendTextMessage(request);
		return ResponseEntity.ok().build();
	}
}
