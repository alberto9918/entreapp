package eu.visiton.app.responses;

import java.util.ArrayList;
import java.util.List;

public class BadgeUserResponse {
    private String id;
    private String name;
    private int points;
    private String description;
    private String icon;
    private List<String> pois = new ArrayList<>();
    private boolean earned;

    public BadgeUserResponse() {

    }

    public BadgeUserResponse(String id, String name, int points, String description, String icon, List<String> pois, boolean earned) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.description = description;
        this.icon = icon;
        this.pois = pois;
        this.earned = earned;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getPois() {
        return pois;
    }

    public void setPois(List<String> pois) {
        this.pois = pois;
    }

    public boolean isEarned() {
        return earned;
    }

    public void setEarned(boolean earned) {
        this.earned = earned;
    }

    @Override
    public String toString() {
        return "BadgeResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", pois=" + pois +
                '}';
    }
}
