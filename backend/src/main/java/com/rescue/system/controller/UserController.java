package com.rescue.system.controller;

import com.rescue.system.dto.request.UpdateUserProfileRequest;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.dto.response.UserProfileResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AccountRepository accountRepository;

    public UserController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        Account account = accountRepository
                .findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse response = UserProfileResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .avatarBase64(account.getAvatarBase64())
                .build();

        return ApiResponse.of("Lấy profile thành công", response);
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }

        Account account = accountRepository
                .findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update có điều kiện (PATCH-like)
        if (request.getFullName() != null) {
            account.setFullName(request.getFullName());
        }

        if (request.getPhoneNumber() != null) {
            account.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getEmail() != null) {
            account.setEmail(request.getEmail());
        }

        if (request.getAvatarBase64() != null) {
            account.setAvatarBase64(request.getAvatarBase64());
        }

        accountRepository.save(account);

        UserProfileResponse response = UserProfileResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .email(account.getEmail())
                .avatarBase64(account.getAvatarBase64())
                .build();

        return ApiResponse.of("Cập nhật profile thành công", response);
    }
}
