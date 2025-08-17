package com.sayem.trackfit.user.service;

import com.sayem.trackfit.user.dto.RegisterRequest;
import com.sayem.trackfit.user.dto.UserResponse;

public interface UserService {
	
	UserResponse getUserprofile(String userId);

	UserResponse register(RegisterRequest request);
	
	Boolean existByUserId(String userId);
}
