package eu.visiton.app.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eu.visiton.app.responses.UserImageResponse;
import eu.visiton.app.responses.UserLikesResponse;

public class UserEditDto {
/*email, name, city, language, likes, favs, friends*/
    private String email;
    private String name;
    private String city;
    private String language;
    private String picture;
    private List<UserLikesResponse> likes = new ArrayList<>();
    private List<String> favs = new ArrayList<>();
    private List<String> friends = new ArrayList<>();
    private List<UserImageResponse> images = new ArrayList<>();



    public UserEditDto() {
    }

    ;


    public UserEditDto(String email, String name, String city, String language, String picture, List<UserLikesResponse> likes, List<String> favs, List<String> friends, List<UserImageResponse> images) {
        this.email = email;
        this.name = name;
        this.city = city;
        this.language = language;
        this.picture = picture;
        this.likes = likes;
        this.favs = favs;
        this.friends = friends;
        this.images = images;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<UserLikesResponse> getLikes() {
        return likes;
    }

    public void setLikes(List<UserLikesResponse> likes) {
        this.likes = likes;
    }

    public List<String> getFavs() {
        return favs;
    }

    public void setFavs(List<String> favs) {
        this.favs = favs;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<UserImageResponse> getImages() {
        return images;
    }

    public void setImages(List<UserImageResponse> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEditDto that = (UserEditDto) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(name, that.name) &&
                Objects.equals(city, that.city) &&
                Objects.equals(language, that.language) &&
                Objects.equals(picture, that.picture) &&
                Objects.equals(likes, that.likes) &&
                Objects.equals(favs, that.favs) &&
                Objects.equals(friends, that.friends) &&
                Objects.equals(images, that.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, city, language, picture, likes, favs, friends, images);
    }

    @Override
    public String toString() {
        return "UserEditDto{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", language='" + language + '\'' +
                ", picture='" + picture + '\'' +
                ", likes=" + likes +
                ", favs=" + favs +
                ", friends=" + friends +
                ", images=" + images +
                '}';
    }
}
