package ru.yermolenko.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yermolenko.dao.DataMessageDAO;
import ru.yermolenko.model.DataMessage;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class MainApiController {
	private final DataMessageDAO dataMessageDAO;

	public MainApiController(DataMessageDAO dataMessageDAO) {
		this.dataMessageDAO = dataMessageDAO;
	}
//	@GetMapping("/all")
//	public String allAccess() {
//		return "Public Content.";
//	}
//
//	@GetMapping("/user")
//	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//	public String userAccess() {
//		return "User Content.";
//	}

	@GetMapping("/get_all_messages")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> getAllMessages() {
		List<DataMessage> dataMessages = dataMessageDAO.findAll();
		return ResponseEntity.ok().body(dataMessages.toString());
	}
}
