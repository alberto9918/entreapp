
package eu.visiton.app.responses;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import eu.visiton.app.util.Constantes;

public class PoiResponse {

    @SerializedName("loc")
    @Expose
    private LocResponse loc;
    @SerializedName("audioguides")
    @Expose
    private AudioguidesResponse audioguides;
    @SerializedName("description")
    @Expose
    private DescriptionResponse description;
    @SerializedName("categories")
    @Expose
    private List<CategoryResponse> categories = null;
    @SerializedName("images")
    @Expose
    private List<String> images = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("uniqueName")
    @Expose
    private String uniqueName;
    @SerializedName("qrCode")
    @Expose
    private String qrCode;
    @SerializedName("year")
    @Expose
    private Integer year;
    @SerializedName("creator")
    @Expose
    private String creator;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("coverImage")
    @Expose
    private String coverImage;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("fav")
    @Expose
    private Boolean fav;
    @SerializedName("visited")
    @Expose
    private Boolean visited;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * No args constructor for use in serialization
     *
     */
    public PoiResponse() {
    }

    /**
     *
     * @param loc
     * @param images
     * @param creator
     * @param distance
     * @param audioguides
     * @param year
     * @param description
     * @param schedule
     * @param createdAt
     * @param uniqueName
     * @param qrCode
     * @param price
     * @param v
     * @param coverImage
     * @param name
     * @param visited
     * @param fav
     * @param categories
     * @param id
     * @param status
     * @param updatedAt
     */
    public PoiResponse(LocResponse loc, AudioguidesResponse audioguides, DescriptionResponse description, List<CategoryResponse> categories, List<String> images, String name, String uniqueName, String qrCode, Integer year, String creator, String status, String schedule, Integer price, String createdAt, String updatedAt, Integer v, String coverImage, Double distance, Boolean fav, Boolean visited, String id) {
        super();
        this.loc = loc;
        this.audioguides = audioguides;
        this.description = description;
        this.categories = categories;
        this.images = images;
        this.name = name;
        this.uniqueName = uniqueName;
        this.qrCode = qrCode;
        this.year = year;
        this.creator = creator;
        this.status = status;
        this.schedule = schedule;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.v = v;
        this.coverImage = coverImage;
        this.distance = distance;
        this.fav = fav;
        this.visited = visited;
        this.id = id;
    }

    public LocResponse getLoc() {
        return loc;
    }

    public void setLoc(LocResponse loc) {
        this.loc = loc;
    }

    public AudioguidesResponse getAudioguides() {
        return audioguides;
    }

    public void setAudioguides(AudioguidesResponse audioguides) {
        this.audioguides = audioguides;
    }

    public DescriptionResponse getDescription() {
        return description;
    }

    public void setDescription(DescriptionResponse description) {
        this.description = description;
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponse> categories) {
        this.categories = categories;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getCoverImage() {
        return Constantes.FILES_BASE_URL + coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Boolean getFav() {
        return fav;
    }

    public void setFav(Boolean fav) {
        this.fav = fav;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
