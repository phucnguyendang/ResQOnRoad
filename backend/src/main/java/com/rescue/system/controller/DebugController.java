package com.rescue.system.controller;

import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.entity.Account;
import com.rescue.system.repository.AccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Debug controller - for development/testing only
 * Should be removed or secured in production
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final AccountRepository accountRepository;

    public DebugController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/accounts")
    public ApiResponse<List<Map<String, Object>>> listAccounts() {
        List<Map<String, Object>> accounts = accountRepository.findAll().stream()
                .map(account -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", account.getId());
                    map.put("username", account.getUsername());
                    map.put("role", account.getRole().name());
                    map.put("companyId", account.getCompanyId());
                    map.put("fullName", account.getFullName());
                    return map;
                })
                .collect(Collectors.toList());

        return ApiResponse.of("Success", accounts);
    }
}
