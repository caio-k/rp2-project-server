package com.dev.springbootserver.dto.request;

public class PlaceRequest {

    private Long placeId;

    private String placeName;

    private int placeLimitTimeSeconds;

    private int placeMaxPeople;

    private String placeType;

    private Long placeSchoolId;

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getPlaceLimitTimeSeconds() {
        return placeLimitTimeSeconds;
    }

    public void setPlaceLimitTimeSeconds(int placeLimitTimeSeconds) {
        this.placeLimitTimeSeconds = placeLimitTimeSeconds;
    }

    public int getPlaceMaxPeople() {
        return placeMaxPeople;
    }

    public void setPlaceMaxPeople(int placeMaxPeople) {
        this.placeMaxPeople = placeMaxPeople;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public Long getPlaceSchoolId() {
        return placeSchoolId;
    }

    public void setPlaceSchoolId(Long placeSchoolId) {
        this.placeSchoolId = placeSchoolId;
    }
}
