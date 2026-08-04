package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.LoginRequest;
import com.shipcore.business.api.dto.request.RegisterRequest;
import com.shipcore.business.api.dto.response.AuthResponse;
import com.shipcore.business.api.dto.response.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser(String email);

}
