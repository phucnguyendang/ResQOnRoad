package com.rescue.system.exception;

import org.springframework.http.HttpStatus;

public class CompanyNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public CompanyNotFoundException(Long companyId) {
        super("Không tìm thấy công ty với ID: " + companyId);
        this.status = HttpStatus.NOT_FOUND;
    }

    public CompanyNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
