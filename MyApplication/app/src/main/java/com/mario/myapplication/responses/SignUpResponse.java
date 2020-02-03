package com.mario.myapplication.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SignUpResponse {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user")
    @Expose
    private SignUpUserResponse user;


    public SignUpResponse() {
    }

    /**
     * @param token
     * @param user
     */
    public SignUpResponse(String token, SignUpUserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SignUpUserResponse getUser() {
        return user;
    }

    public void setUser(SignUpUserResponse user) {
        this.user = user;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SignUpResponse that = (SignUpResponse) o;

        if (!token.equals(that.token)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}

