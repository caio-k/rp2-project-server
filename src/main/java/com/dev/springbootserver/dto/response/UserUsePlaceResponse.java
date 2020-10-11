package com.dev.springbootserver.dto.response;

public class UserUsePlaceResponse {

    private Long placeId;
    private Long userId;
    private int counter;
    private Long lastUpdate;

    public UserUsePlaceResponse(Long placeId, Long userId, int counter, Long lastUpdate) {
        this.placeId = placeId;
        this.userId = userId;
        this.counter = counter;
        this.lastUpdate = lastUpdate;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
