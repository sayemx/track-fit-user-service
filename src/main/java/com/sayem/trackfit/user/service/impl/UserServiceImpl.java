package com.sayem.trackfit.user.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sayem.trackfit.user.dto.RegisterRequest;
import com.sayem.trackfit.user.dto.UserResponse;
import com.sayem.trackfit.user.entity.User;
import com.sayem.trackfit.user.exception.UserAlreadyExistsException;
import com.sayem.trackfit.user.exception.UserNotFoundException;
import com.sayem.trackfit.user.repository.UserRepository;
import com.sayem.trackfit.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

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
			throw new UserAlreadyExistsException(registerRequest.getEmail());
		}
		
		User user = new User();
		user.setEmail(registerRequest.getEmail());
		user.setPassword(registerRequest.getPassword());
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());

		User savedUser = userRepository.save(user);

		UserResponse userResponse = new UserResponse();
		userResponse.setId(savedUser.getId());
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

}
