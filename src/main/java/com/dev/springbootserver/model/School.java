package com.dev.springbootserver.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "school", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToOne
    @JoinColumn(name = "representative_user", referencedColumnName = "id")
    private User representativeUser;

    @ManyToMany(mappedBy = "schoolsTeacher", fetch = FetchType.EAGER)
    private Set<User> schoolTeachers = new HashSet<>();

    public School() {
    }

    public School(String name, User representativeUser) {
        this.name = name;
        this.representativeUser = representativeUser;
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

    public User getRepresentativeUser() {
        return representativeUser;
    }

    public void setRepresentativeUser(User representativeUser) {
        this.representativeUser = representativeUser;
    }

    public Set<User> getSchoolTeachers() {
        return schoolTeachers;
    }

    public void setSchoolTeachers(Set<User> schoolTeachers) {
        this.schoolTeachers = schoolTeachers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return name.equals(school.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
