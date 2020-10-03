package com.dev.springbootserver.dto.response;

public class ExitLogResponse {

    private String message;

    public ExitLogResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String exitName) {
        this.message = message;
    }
}
