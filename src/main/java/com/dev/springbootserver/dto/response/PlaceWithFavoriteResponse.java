package com.dev.springbootserver.dto.response;

public class PlaceWithFavoriteResponse {

    private Long placeId;
    private String name;
    private String type;
    private int maxPeople;
    private int limitTimeSeconds;
    private boolean isFavorite;

    public PlaceWithFavoriteResponse(Long placeId, String name, String type, int maxPeople, int limitTimeSeconds, boolean isFavorite) {
        this.placeId = placeId;
        this.name = name;
        this.type = type;
        this.maxPeople = maxPeople;
        this.limitTimeSeconds = limitTimeSeconds;
        this.isFavorite = isFavorite;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
