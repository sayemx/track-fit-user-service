package com.sayem.trackfit.user.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.sayem.trackfit.user.dto.RegisterRequest;
import com.sayem.trackfit.user.dto.UserResponse;
import com.sayem.trackfit.user.entity.User;
import com.sayem.trackfit.user.exception.UserNotFoundException;
import com.sayem.trackfit.user.repository.UserRepository;
import com.sayem.trackfit.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	private final WebClient webClient;
    private final String keycloakBaseUrl = "http://localhost:8181"; // adjust
    private final String realm = "trackfit-oauth2";
    private final String clientId = "trackfit-admin-client";
    private final String clientSecret = "HfcZ3xITIWI5AQLdXqReo4ekO1NKtkJW";
    
    
    public UserServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.baseUrl(keycloakBaseUrl).build();
    }

	@Override
	public UserResponse getUserprofile(String userId) {
		
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
		
		UserResponse userResponse = new UserResponse();
		userResponse.setId(user.getId());
		userResponse.setPassword(user.getPassword());
		userResponse.setEmail(user.getEmail());
		userResponse.setFirstName(user.getFirstName());
		userResponse.setLastName(user.getLastName());
		userResponse.setCreatedAt(user.getCreatedAt());
		userResponse.setUpdatedAt(user.getUpdatedAt());
		userResponse.setEmail(user.getEmail());

		return userResponse;
	}

	@Override
	public UserResponse register(RegisterRequest registerRequest) {

		Optional<User> userFound = userRepository.findByEmail(registerRequest.getEmail());
		
		if(userFound.isPresent()) {
			// throw new UserAlreadyExistsException(registerRequest.getEmail());
			// keycloak integration or synchronization
			
			User existingUser = userFound.get();

			UserResponse userResponse = new UserResponse();
			userResponse.setId(existingUser.getId());
			userResponse.setKeycloakId(existingUser.getKeycloakId());
			userResponse.setPassword(existingUser.getPassword());
			userResponse.setEmail(existingUser.getEmail());
			userResponse.setFirstName(existingUser.getFirstName());
			userResponse.setLastName(existingUser.getLastName());
			userResponse.setCreatedAt(existingUser.getCreatedAt());
			userResponse.setUpdatedAt(existingUser.getUpdatedAt());
			userResponse.setEmail(existingUser.getEmail());

			return userResponse;
			
		}
		
		User user = new User();
		user.setEmail(registerRequest.getEmail());
		user.setPassword(registerRequest.getPassword());
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());
		user.setKeycloakId(registerRequest.getKeycloakId());

		User savedUser = userRepository.save(user);

		UserResponse userResponse = new UserResponse();
		userResponse.setId(savedUser.getId());
		userResponse.setKeycloakId(savedUser.getKeycloakId());
		userResponse.setPassword(savedUser.getPassword());
		userResponse.setEmail(savedUser.getEmail());
		userResponse.setFirstName(savedUser.getFirstName());
		userResponse.setLastName(savedUser.getLastName());
		userResponse.setCreatedAt(savedUser.getCreatedAt());
		userResponse.setUpdatedAt(savedUser.getUpdatedAt());
		userResponse.setEmail(savedUser.getEmail());

		return userResponse;
	}

	@Override
	public Boolean existByUserId(String userId) {
		return userRepository.existsById(userId);
	}

	@Override
	public Boolean existByKeycloakId(String keycloakId) {
		return userRepository.existsByKeycloakId(keycloakId);
	}

	@Override
	public void registerOnKeyCloak(RegisterRequest request) {
		createUserWithPassword(request);
	}
	
	 public String getAccessToken() {
	        Map<String, String> form = new HashMap<>();
	        form.put("client_id", clientId);
	        form.put("client_secret", clientSecret);
	        form.put("grant_type", "client_credentials");

	        return webClient.post()
	                .uri("/realms/" + realm + "/protocol/openid-connect/token")
	                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	                .body(BodyInserters.fromFormData("client_id", clientId)
	                        .with("client_secret", clientSecret)
	                        .with("grant_type", "client_credentials"))
	                .retrieve()
	                .bodyToMono(Map.class)
	                .map(resp -> (String) resp.get("access_token"))
	                .block(); // ⚠️ blocking for simplicity (can be reactive if needed)
	 }
	 
	 public void createUserWithPassword(RegisterRequest request) {
		    String token = getAccessToken();

		    Map<String, Object> user = new HashMap<>();
		    user.put("username", request.getEmail());
		    user.put("enabled", true);
		    user.put("email", request.getEmail());
		    user.put("firstName", request.getFirstName());
		    user.put("lastName", request.getLastName());

		    Map<String, Object> credentials = new HashMap<>();
		    credentials.put("type", "password");
		    credentials.put("value", request.getPassword());
		    credentials.put("temporary", false);

		    user.put("credentials", java.util.List.of(credentials));

		    webClient.post()
		            .uri("/admin/realms/" + realm + "/users")
		            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
		            .contentType(MediaType.APPLICATION_JSON)
		            .bodyValue(user)
		            .retrieve()
		            .toBodilessEntity()
		            .doOnSuccess(resp -> System.out.println("✅ User created with password, status: " + resp.getStatusCode()))
		            .doOnError(err -> System.err.println("❌ Error creating user: " + err.getMessage()))
		            .block();
		}

}
