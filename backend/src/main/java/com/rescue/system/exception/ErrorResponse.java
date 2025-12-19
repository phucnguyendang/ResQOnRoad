package com.rescue.system.exception;

import java.util.List;

public class ErrorResponse {

    private ErrorBody error;

    public ErrorResponse() {
    }

    public ErrorResponse(ErrorBody error) {
        this.error = error;
    }

    public static ErrorResponse of(int code, String message, List<String> details) {
        return new ErrorResponse(new ErrorBody(code, message, details));
    }

    public ErrorBody getError() {
        return error;
    }

    public void setError(ErrorBody error) {
        this.error = error;
    }

    public static class ErrorBody {
        private int code;
        private String message;
        private List<String> details;

        public ErrorBody() {
        }

        public ErrorBody(int code, String message, List<String> details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getDetails() {
            return details;
        }

        public void setDetails(List<String> details) {
            this.details = details;
        }
    }
}
