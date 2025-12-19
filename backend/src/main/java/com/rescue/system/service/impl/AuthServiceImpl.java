package com.rescue.system.service.impl;

import com.rescue.system.dto.request.LoginRequest;
import com.rescue.system.dto.request.RegisterRequest;
import com.rescue.system.dto.response.JwtResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.entity.Role;
import com.rescue.system.exception.ApiException;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.security.JwtTokenProvider;
import com.rescue.system.service.AuthService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Sai username hoặc password"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Sai username hoặc password");
        }

        String token = jwtTokenProvider.generateToken(account.getUsername(), account.getId(), account.getRole().name());
        JwtResponse.Profile profile = new JwtResponse.Profile(account.getFullName(), account.getAvatarBase64());
        return new JwtResponse(token, account.getId(), account.getRole().name(), profile);
    }

    @Override
    public Long register(RegisterRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào không hợp lệ", List.of("username đã tồn tại"));
        }

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.USER);
        account.setFullName(request.getFullName());
        account.setPhoneNumber(request.getPhoneNumber());
        account.setEmail(request.getEmail());
        account.setAvatarBase64(request.getAvatarBase64());

        Account saved = accountRepository.save(account);
        return saved.getId();
    }
}
