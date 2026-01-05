package com.rescue.system.controller;

import com.rescue.system.dto.response.AdminUserResponse;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> users = userService.getAllUsers();
        return ApiResponse.of("Lấy danh sách người dùng thành công", users);
    }

    @PutMapping("/{id}/lock")
    public ApiResponse<AdminUserResponse> lockOrUnlockUser(
        @PathVariable Long id,
        @RequestParam boolean locked
    ) {
        AdminUserResponse result = userService.updateUserLockStatus(id, locked);
        String message = locked ? "Khóa tài khoản thành công" : "Mở khóa tài khoản thành công";
        return ApiResponse.of(message, result);
    }
}
