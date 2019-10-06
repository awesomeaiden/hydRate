package com.example.hydRate;

public class Rating {
    String fountainID;
    String ratingID;
    float tasteRating;
    float tempRating;
    float pressRating;
    boolean bottleFill;

    public String getFountainID() {
        return fountainID;
    }

    public void setFountainID(String fountainID) {
        this.fountainID = fountainID;
    }

    public String getRatingID() {
        return ratingID;
    }

    public void setRatingID(String ratingID) {
        this.ratingID = ratingID;
    }

    public float getTasteRating() {
        return tasteRating;
    }

    public void setTasteRating(float tasteRating) {
        this.tasteRating = tasteRating;
    }

    public float getTempRating() {
        return tempRating;
    }

    public void setTempRating(float tempRating) {
        this.tempRating = tempRating;
    }

    public float getPressRating() {
        return pressRating;
    }

    public void setPressRating(float pressRating) {
        this.pressRating = pressRating;
    }

    public boolean isBottleFill() {
        return bottleFill;
    }

    public void setBottleFill(boolean bottleFill) {
        this.bottleFill = bottleFill;
    }
}
