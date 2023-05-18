package com.posco.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Document {

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("place_name")
    private String placeName;
    @JsonProperty("y")
    private double latitude;

    @JsonProperty("category_group_name")
    private String categoryName;
    @JsonProperty("x")
    private double longitude;


    public String getAddressName() {
        return addressName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
