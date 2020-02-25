package eu.visiton.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateRatingResponse {

    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("poi")
    @Expose
    private String poi;

    /**
     * No args constructor for use in serialization
     *
     */
    public CreateRatingResponse() {

    }

    /**
     *
     * @param rating
     * @param poi
     */
    public CreateRatingResponse(Float rating, String poi) {
        super();
        this.rating = rating;
        this.poi = poi;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

}
