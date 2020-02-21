package eu.visiton.app.responses;

public class UserImageResponse {

    private String _id;
    private PoiResponse poi;
    private String thumbnail;
    private String full;


    public UserImageResponse(String id, PoiResponse poi, String thumbnail, String full) {
        _id = id;
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

    public PoiResponse getPoi() {
        return poi;
    }

    public void setPoi(PoiResponse poi) {
        this.poi = poi;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
