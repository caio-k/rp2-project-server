package com.dev.springbootserver.dto.request;

public class ExitRequest {

    private Long exitId;
    private String exitName;
    private Long schoolId;

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

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
}
