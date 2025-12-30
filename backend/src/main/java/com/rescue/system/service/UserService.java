package com.rescue.system.service;

import com.rescue.system.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getCurrentUserProfile(String username);
}