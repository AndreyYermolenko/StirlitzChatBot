package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yermolenko.dao.DataMessageDAO;
import ru.yermolenko.model.DataMessage;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.request.TextMessageRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.service.MainService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/data/message")
@Log4j
public class MessageApiController {
	private final DataMessageDAO dataMessageDAO;
	private final MainService mainService;

	public MessageApiController(DataMessageDAO dataMessageDAO, MainService mainService) {
		this.dataMessageDAO = dataMessageDAO;
		this.mainService = mainService;
	}

	@GetMapping("/all")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> getAllMessages() {
		List<DataMessage> dataMessages = dataMessageDAO.findAll();
		return ResponseEntity.ok().body(dataMessages.toString());
	}

	@PostMapping("/last_few")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getLastMessages(@Valid @RequestBody MessageHistoryRequest request) {
		log.debug(request);
		MessageHistoryResponse result = mainService.getLastMessages(request);
		if (result.hasError()) {
			return ResponseEntity.badRequest().body(result);
		} else {
			return ResponseEntity.ok().body(result);
		}
	}

	@PostMapping("/send")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> sendMessage(@Valid @RequestBody TextMessageRequest request) {
		log.debug(request);
		mainService.sendTextMessage(request);
		return ResponseEntity.ok().build();
	}
}
