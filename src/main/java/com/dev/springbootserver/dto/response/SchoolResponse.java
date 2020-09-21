package com.dev.springbootserver.dto.response;

public class SchoolResponse {

    private Long id;
    private String name;
    private String schoolPrincipalUsername;

    public SchoolResponse(Long id, String name, String schoolPrincipalUsername) {
        this.id = id;
        this.name = name;
        this.schoolPrincipalUsername = schoolPrincipalUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolPrincipalUsername() {
        return schoolPrincipalUsername;
    }

    public void setSchoolPrincipalUsername(String schoolPrincipalUsername) {
        this.schoolPrincipalUsername = schoolPrincipalUsername;
    }
}
