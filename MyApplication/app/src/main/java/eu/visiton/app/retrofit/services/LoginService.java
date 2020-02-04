package eu.visiton.app.retrofit.services;

import eu.visiton.app.responses.LoginResponse;
import eu.visiton.app.responses.Register;
import eu.visiton.app.responses.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginService {

    @POST("auth")
    Call<LoginResponse> doLogin(@Header("Authorization") String authorization);


    @POST("users")
    Call<SignUpResponse> doSignUp(@Body Register register);


}

