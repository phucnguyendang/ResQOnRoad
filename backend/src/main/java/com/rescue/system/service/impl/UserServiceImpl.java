package com.rescue.system.service.impl;

import com.rescue.system.dto.response.AdminUserResponse;
import com.rescue.system.dto.response.UserProfileResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.Role;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;

    public UserServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserProfileResponse getCurrentUserProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        return UserProfileResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .avatarBase64(account.getAvatarBase64())
                .build();
    }

    @Override
    public List<AdminUserResponse> getAllUsers() {
        List<Account> accounts = accountRepository.findAll();
        List<AdminUserResponse> result = new ArrayList<>();
        for (Account account : accounts) {
            AdminUserResponse dto = new AdminUserResponse();
            dto.setId(account.getId());
            dto.setUsername(account.getUsername());
            dto.setEmail(account.getEmail());
            dto.setRole(account.getRole());
            dto.setLocked(account.isLocked());
            result.add(dto);
        }
        return result;
    }

    @Override
    public AdminUserResponse updateUserLockStatus(Long userId, boolean locked) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (account.getRole() == Role.ADMIN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot lock admin account");
        }

        account.setLocked(locked);
        accountRepository.save(account);
        AdminUserResponse dto = new AdminUserResponse();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setEmail(account.getEmail());
        dto.setRole(account.getRole());
        dto.setLocked(account.isLocked());
        return dto;
    }
}