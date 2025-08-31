package com.sayem.trackfit.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sayem.trackfit.user.dto.RegisterRequest;
import com.sayem.trackfit.user.dto.UserResponse;
import com.sayem.trackfit.user.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(userService.getUserprofile(userId));
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(userService.register(request));
	}
	
	@GetMapping("/validateByuserId/{userId}")
	public ResponseEntity<Boolean> validateUserByUserId(@PathVariable String userId) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(userService.existByUserId(userId));
	}
	
	@GetMapping("/validateByKeycloakId/{keycloakId}")
	public ResponseEntity<Boolean> validateUserByKeycloakId(@PathVariable String keycloakId) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(userService.existByKeycloakId(keycloakId));
	}
	
	@PostMapping("/register/keyCloak")
	public ResponseEntity<Void> registerOnKeyCloak(@Valid @RequestBody RegisterRequest request) {
		userService.registerOnKeyCloak(request);
		return ResponseEntity
				.status(HttpStatus.OK)
				.build();
	}
	
	
}
