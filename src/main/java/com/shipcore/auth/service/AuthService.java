package com.shipcore.auth.service;

import com.shipcore.auth.dto.request.LoginRequest;
import com.shipcore.auth.dto.request.RegisterRequest;
import com.shipcore.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}