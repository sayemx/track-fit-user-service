package com.sayem.trackfit.user.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
	
	private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    
}
