package com.mario.myapplication.responses;

public class VisitPoiResponse {
    private PoiResponse poi;
    private ResponseContainer<BadgeResponse> newBadges;

    public VisitPoiResponse() {
    }

    public VisitPoiResponse(PoiResponse poi, ResponseContainer<BadgeResponse> newBadges) {
        this.poi = poi;
        this.newBadges = newBadges;
    }

    public PoiResponse getPoi() {
        return poi;
    }

    public void setPoi(PoiResponse poi) {
        this.poi = poi;
    }

    public ResponseContainer<BadgeResponse> getNewBadges() {
        return newBadges;
    }

    public void setNewBadges(ResponseContainer<BadgeResponse> newBadges) {
        this.newBadges = newBadges;
    }
}
