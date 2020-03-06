package eu.visiton.app.responses;

import java.util.Date;

public class ImageInvalidResponse {

    private String _id;
    private String poi;
    private String thumbnail;
    private String full;
    private Date dateToBeRemoved;

    public ImageInvalidResponse(String id, String poi, String thumbnail, String full, Date dateToBeRemoved) {
        _id = id;
        this.poi = poi;
        this.thumbnail = thumbnail;
        this.full = full;
        this.dateToBeRemoved = dateToBeRemoved;
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

    public Date getDateToBeRemoved() {
        return dateToBeRemoved;
    }

    public void setDateToBeRemoved(Date dateToBeRemoved) {
        this.dateToBeRemoved = dateToBeRemoved;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPoi() {
        return poi;
    }
    public void setPoi(String poi) {
        this.poi = poi;
    }
}
