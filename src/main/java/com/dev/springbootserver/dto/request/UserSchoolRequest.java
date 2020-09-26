package com.dev.springbootserver.dto.request;

public class UserSchoolRequest {

    private Long schoolId;

    private String username;

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
