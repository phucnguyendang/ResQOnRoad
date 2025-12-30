package com.rescue.system.service.impl;

import com.rescue.system.dto.response.UserProfileResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;

    public UserServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserProfileResponse getCurrentUserProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .avatarBase64(account.getAvatarBase64())
                .build();
    }
}