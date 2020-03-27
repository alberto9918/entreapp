package eu.visiton.app.dto;

public class UserImageDto {
    private String poi;
    private String thumbnail;
    private String full;


    public UserImageDto(String poi, String thumbnail, String full) {
        this.poi = poi;
        this.thumbnail = thumbnail;
        this.full = full;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }
}
