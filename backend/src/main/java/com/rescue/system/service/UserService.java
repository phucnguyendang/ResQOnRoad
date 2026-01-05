package com.rescue.system.service;

import com.rescue.system.dto.response.UserProfileResponse;
import com.rescue.system.dto.response.AdminUserResponse;
import java.util.List;

public interface UserService {
    UserProfileResponse getCurrentUserProfile(String username);
    List<AdminUserResponse> getAllUsers();
    AdminUserResponse updateUserLockStatus(Long userId, boolean locked);
}