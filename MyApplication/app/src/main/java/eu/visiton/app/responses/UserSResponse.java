package eu.visiton.app.responses;

import java.util.ArrayList;
import java.util.List;

public class UserSResponse {


    private String id;
    private String name;
    private String role;
    private String picture;
    private List<UserImageResponse> images =new ArrayList<>();
    private List<ImageInvalidResponse> invalidImages =new ArrayList<>();
    private String password;
    private String email;
    private String createAt;
    private String city;
    private String language;
    private List<String> favs = new ArrayList<>();
    private List<String> visited = new ArrayList<>();
    private List<BadgeUserResponse> badges = new ArrayList<>();
    private List<UserLikesResponse> likes  = new ArrayList<>();
    private List<String> friends = new ArrayList<>();

    public UserSResponse() {
    }

    public UserSResponse(String id, String name, String role, boolean friended, String picture,List<UserImageResponse> images,List<ImageInvalidResponse> invalidImages, String password, String email, String createAt, String city, String language, List<String> favs, List<String> visited, List<BadgeUserResponse> badges, List<UserLikesResponse> likes, List<String> friends) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.picture = picture;
        this.images = images;
        this.invalidImages = invalidImages;
        this.password = password;
        this.email = email;
        this.createAt = createAt;
        this.city = city;
        this.language = language;
        this.favs = favs;
        this.visited = visited;
        this.badges = badges;
        this.likes = likes;
        this.friends = friends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFavs() {
        return favs;
    }

    public void setFavs(List<String> favs) {
        this.favs = favs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getVisited() {
        return visited;
    }

    public void setVisited(List<String> visited) {
        this.visited = visited;
    }

    public List<BadgeUserResponse> getBadges() {
        return badges;
    }

    public void setBadges(List<BadgeUserResponse> badges) {
        this.badges = badges;
    }

    public List<UserLikesResponse> getLikes() {
        return likes;
    }

    public void setLikes(List<UserLikesResponse> likes) {
        this.likes = likes;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", picture='" + picture + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", createAt='" + createAt + '\'' +
                ", city='" + city + '\'' +
                ", language=" + language +
                ", visited=" + visited +
                ", badges=" + badges +
                ", likes=" + likes +
                '}';
    }

    public List<UserImageResponse> getImages() {
        return images;
    }

    public void setImages(List<UserImageResponse> images) {
        this.images = images;
    }

    public List<ImageInvalidResponse> getInvalidImages() {
        return invalidImages;
    }

    public void setInvalidImages(List<ImageInvalidResponse> invalidImages) {
        this.invalidImages = invalidImages;
    }
}

