package eu.visiton.app.retrofit.services;

import eu.visiton.app.model.Comment;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.responses.UserRatingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RatingService {

    String BASE_URL = "ratings";

    @GET(BASE_URL)
    Call<ResponseContainer<UserRatingResponse>> listRatings();

    @GET(BASE_URL + "/{id}")
    Call<UserRatingResponse> getRating(@Path("id") String id);

    @PUT(BASE_URL + "/fav/add/{id}")
    Call<UserRatingResponse> editRating(@Path("id") String id);

    @POST(BASE_URL)
    Call<UserRatingResponse> createRating(@Body Comment c);
}
