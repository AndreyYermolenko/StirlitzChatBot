package ru.yermolenko.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yermolenko.dao.DataMessageDAO;
import ru.yermolenko.model.DataMessage;
import ru.yermolenko.payload.request.MessageHistoryRequest;
import ru.yermolenko.payload.response.MessageHistoryResponse;
import ru.yermolenko.service.MainService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/data")
@Log4j
public class MainApiController {
	private final DataMessageDAO dataMessageDAO;
	private final MainService mainService;

	public MainApiController(DataMessageDAO dataMessageDAO, MainService mainService) {
		this.dataMessageDAO = dataMessageDAO;
		this.mainService = mainService;
	}

	@GetMapping("/get_all_messages")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> getAllMessages() {
		List<DataMessage> dataMessages = dataMessageDAO.findAll();
		return ResponseEntity.ok().body(dataMessages.toString());
	}

	@PostMapping("/get_last_messages")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> getLastMessages(@Valid @RequestBody MessageHistoryRequest request) {
		log.debug(request);
		MessageHistoryResponse result = mainService.getLastMessages(request);
		if (result.hasError()) {
			return ResponseEntity.badRequest().body(result.getErrorMessage());
		} else {
			return ResponseEntity.ok().body(result);
		}
	}
}
