package com.rescue.system.service;

import com.rescue.system.dto.request.LoginRequest;
import com.rescue.system.dto.request.RegisterRequest;
import com.rescue.system.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    Long register(RegisterRequest request);
}
