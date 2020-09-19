package com.dev.springbootserver.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id")
    )
    private Set<Place> favoritePlaces = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "teacher",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "school_id")
    )
    private Set<School> schoolsTeacher = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserUsePlace> userUsePlaces = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ExitLog> exitLogs = new HashSet<>();

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Place> getFavoritePlaces() {
        return favoritePlaces;
    }

    public void setFavoritePlaces(Set<Place> favoritePlaces) {
        this.favoritePlaces = favoritePlaces;
    }

    public Set<School> getSchoolsTeacher() {
        return schoolsTeacher;
    }

    public void setSchoolsTeacher(Set<School> schoolsTeacher) {
        this.schoolsTeacher = schoolsTeacher;
    }

    public Set<UserUsePlace> getUserUsePlaces() {
        return userUsePlaces;
    }

    public void setUserUsePlaces(Set<UserUsePlace> userUsePlaces) {
        this.userUsePlaces = userUsePlaces;
    }

    public Set<ExitLog> getExitLogs() {
        return exitLogs;
    }

    public void setExitLogs(Set<ExitLog> exitLogs) {
        this.exitLogs = exitLogs;
    }
}
