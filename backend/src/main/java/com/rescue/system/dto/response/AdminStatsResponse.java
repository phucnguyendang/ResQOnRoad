package com.rescue.system.dto.response;

public class AdminStatsResponse {

    private long totalUsers;
    private long totalPosts;
    private long totalComments;
    private long lockedAccounts;

    public AdminStatsResponse() {
    }

    public AdminStatsResponse(long totalUsers, long totalPosts, long totalComments, long lockedAccounts) {
        this.totalUsers = totalUsers;
        this.totalPosts = totalPosts;
        this.totalComments = totalComments;
        this.lockedAccounts = lockedAccounts;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public long getLockedAccounts() {
        return lockedAccounts;
    }

    public void setLockedAccounts(long lockedAccounts) {
        this.lockedAccounts = lockedAccounts;
    }
}
