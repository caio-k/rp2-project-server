package com.dev.springbootserver.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "user_use_place")
public class UserUsePlace implements Serializable {

    @EmbeddedId
    private UserUsePlaceKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("placeId")
    @JoinColumn(name = "place_id")
    private Place place;

    @Min(0)
    @NotBlank
    private int counter;

    @NotBlank
    @Column(name = "last_update")
    private Long lastUpdate;

    public UserUsePlace() {
    }

    public UserUsePlace(User user, Place place, int counter, Long lastUpdate) {
        this.id = new UserUsePlaceKey(user.getId(), place.getId());
        this.user = user;
        this.place = place;
        this.counter = counter;
        this.lastUpdate = lastUpdate;
    }

    public UserUsePlaceKey getId() {
        return id;
    }

    public void setId(UserUsePlaceKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
