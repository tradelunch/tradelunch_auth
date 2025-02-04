package com.tradelunch.tradelunch_auth.controller;

import com.tradelunch.tradelunch_auth.model.Customer;
import com.tradelunch.tradelunch_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RequiredArgsConstructor
@RestController
@RequestMapping(name = "userController", path = "/user")
public class UserController {
	private final UserService userService;

	@GetMapping(path = "/ping")
	public String status() {
		return "Auth Service is up and running";
	}

	@PostMapping(path = {"/register"})
	public ResponseEntity<String> registerUser(@RequestBody Customer customer) {
		try {
			Customer saved = userService.registerUser(customer);
			if (saved.getId() > 0) {
				return ResponseEntity.status(HttpStatus.CREATED).body("Customer registered successfully.");
			}
		} catch (Exception e) {
			String msg = MessageFormat.format("Error: {0}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);

		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer registered failed.");
	}
}
