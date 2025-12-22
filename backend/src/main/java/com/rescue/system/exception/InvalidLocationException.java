package com.rescue.system.exception;

import org.springframework.http.HttpStatus;

public class InvalidLocationException extends RuntimeException {
    private final HttpStatus status;

    public InvalidLocationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public InvalidLocationException() {
        super("Tọa độ GPS không hợp lệ. Vui lòng kiểm tra lại latitude và longitude.");
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
