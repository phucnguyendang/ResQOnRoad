package com.rescue.system.controller;

import com.rescue.system.dto.response.AdminStatsResponse;
import com.rescue.system.dto.response.ApiResponse;
import com.rescue.system.repository.AccountRepository;
import com.rescue.system.repository.CommunityCommentRepository;
import com.rescue.system.repository.CommunityPostRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AccountRepository accountRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;

    public AdminStatsController(
            AccountRepository accountRepository,
            CommunityPostRepository communityPostRepository,
            CommunityCommentRepository communityCommentRepository
    ) {
        this.accountRepository = accountRepository;
        this.communityPostRepository = communityPostRepository;
        this.communityCommentRepository = communityCommentRepository;
    }

    @GetMapping
    public ApiResponse<AdminStatsResponse> getAdminStats() {
        long totalUsers = accountRepository.count();
        long lockedAccounts = accountRepository.countByLockedTrue();
        long totalPosts = communityPostRepository.count();
        long totalComments = communityCommentRepository.count();

        AdminStatsResponse stats = new AdminStatsResponse(totalUsers, totalPosts, totalComments, lockedAccounts);
        return ApiResponse.of("Lấy thống kê thành công", stats);
    }
}
