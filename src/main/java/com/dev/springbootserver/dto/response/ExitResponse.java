package com.dev.springbootserver.dto.response;

public class ExitResponse {

    private Long exitId;
    private String exitName;

    public ExitResponse(Long exitId, String exitName) {
        this.exitId = exitId;
        this.exitName = exitName;
    }

    public Long getExitId() {
        return exitId;
    }

    public void setExitId(Long exitId) {
        this.exitId = exitId;
    }

    public String getExitName() {
        return exitName;
    }

    public void setExitName(String exitName) {
        this.exitName = exitName;
    }
}
