package com.dev.springbootserver.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "place", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "school_id"})
})
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private EPlace type;

    @Min(0)
    @NotBlank
    private int counter;

    @Min(0)
    @NotBlank
    @Column(name = "max_people")
    private int maxPeople;

    @Min(0)
    @NotBlank
    @Column(name = "limit_time_seconds")
    private int limitTimeSeconds;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToMany(mappedBy = "favoritePlaces", fetch = FetchType.EAGER)
    private Set<User> favoriteUsers = new HashSet<>();

    @OneToMany(mappedBy = "place", cascade = {CascadeType.ALL})
    private Set<UserUsePlace> userUsePlaces = new HashSet<>();

    public Place() {
    }

    public Place(String name, EPlace type, int counter, int maxPeople, int limitTimeSeconds, School school) {
        this.name = name;
        this.type = type;
        this.counter = counter;
        this.maxPeople = maxPeople;
        this.limitTimeSeconds = limitTimeSeconds;
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

    public EPlace getType() {
        return type;
    }

    public void setType(EPlace type) {
        this.type = type;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public int getLimitTimeSeconds() {
        return limitTimeSeconds;
    }

    public void setLimitTimeSeconds(int limitTimeSeconds) {
        this.limitTimeSeconds = limitTimeSeconds;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Set<User> getFavoriteUsers() {
        return favoriteUsers;
    }

    public void setFavoriteUsers(Set<User> favoriteUsers) {
        this.favoriteUsers = favoriteUsers;
    }

    public Set<UserUsePlace> getUserUsePlaces() {
        return userUsePlaces;
    }

    public void setUserUsePlaces(Set<UserUsePlace> userUsePlaces) {
        this.userUsePlaces = userUsePlaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return id.equals(place.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
