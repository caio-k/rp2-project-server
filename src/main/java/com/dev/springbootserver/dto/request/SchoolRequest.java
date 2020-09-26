package com.dev.springbootserver.dto.request;

public class SchoolRequest {

    private Long schoolId;

    private String schoolName;

    private String schoolPrincipalUsername;

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolPrincipalUsername() {
        return schoolPrincipalUsername;
    }

    public void setSchoolPrincipalUsername(String schoolPrincipalUsername) {
        this.schoolPrincipalUsername = schoolPrincipalUsername;
    }
}
