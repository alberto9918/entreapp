package eu.visiton.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import eu.visiton.app.model.Language;

public class UserRatingResponse {

    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("poi")
    @Expose
    private String poi;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserRatingResponse() {

    }

    /**
     *
     * @param rating
     * @param poi
     * @param user
     * @param id
     */
    public UserRatingResponse(Float rating, String poi, String user, String id) {
        super();
        this.rating = rating;
        this.poi = poi;
        this.user = user;
        this.id = id;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
