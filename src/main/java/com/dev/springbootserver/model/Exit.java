package com.dev.springbootserver.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exit", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "school_id"})
})
public class Exit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "exit")
    private Set<ExitLog> exitLogs = new HashSet<>();

    public Exit() {
    }

    public Exit(String name, School school) {
        this.name = name;
        this.school = school;
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

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Set<ExitLog> getExitLogs() {
        return exitLogs;
    }

    public void setExitLogs(Set<ExitLog> exitLogs) {
        this.exitLogs = exitLogs;
    }
}
