package eu.visiton.app.retrofit.services;

import eu.visiton.app.dto.UserEditDto;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.responses.PeopleResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.responses.UserEditResponse;
import eu.visiton.app.responses.UserResponse;

import java.util.List;

import eu.visiton.app.responses.UserSResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface UserService {

    String BASE_URL = "users";

    @GET(BASE_URL)
    Call<ResponseContainer<UserResponse>> listUsers();

    /**
     * Call that invokes the whole list of users, but with an additional field which contains
     * a boolean attribute about if every single user is a friend of us or not
     *
     * @return The list of users
     */

    @GET(BASE_URL + "/friended")
    Call<List<PeopleResponse>> listUsersAndFriended();

    @GET(BASE_URL + "/{id}")
    Call<MyProfileResponse> getUser(@Path("id") String id);

    @GET(BASE_URL + "/{id}")
    Call<UserResponse> getUserResponse(@Path("id") String id);

    @GET(BASE_URL + "/me")
    Call<UserSResponse> getMe();

    @GET(BASE_URL + "/me")
    Call<UserResponse> getMe2();

    @PUT(BASE_URL + "/{id}")
    Call<UserEditResponse> editUser(@Path("id") String id, @Body UserEditDto user);

    @PUT(BASE_URL + "/{id}/password")
    Call<UserResponse> editPassword(@Path("id") String id, @Body String password);

    // It should be /me, must do in api first
//    @DELETE("/users/{id}")
//    Call<User> deleteUser(@Path("id") Long id);
    @Multipart
    @POST(BASE_URL + "/uploadProfilePicture")
    Call<MyProfileResponse> uploadPictureProfile(@Part MultipartBody.Part avatar,
                                   @Part("id") RequestBody id);

    @PUT(BASE_URL + "/addPoiLike/{id}")
    Call<UserResponse> ChangePoiFav(@Path("id") String id);
}
