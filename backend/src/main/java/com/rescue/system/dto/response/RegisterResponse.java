package com.rescue.system.dto.response;

public class RegisterResponse {

    private Long account_id;

    public RegisterResponse() {
    }

    public RegisterResponse(Long accountId) {
        this.account_id = accountId;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }
}
