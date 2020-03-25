package eu.visiton.app.data;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;

import eu.visiton.app.common.MyApp;
import eu.visiton.app.dto.UserEditDto;
import eu.visiton.app.model.User;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.responses.UserEditResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.ui.profile.ProfileDarkActivity;
import eu.visiton.app.util.UtilToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    UserService userService;
    String jwt, userId;
    MutableLiveData<MyProfileResponse> userProfile;
    /*MutableLiveData<UserEditResponse> editedProfile;*/
    /*ProfileDarkActivity profileDarkActivity;*/

    ProfileRepository(){
        jwt = UtilToken.getToken(Objects.requireNonNull(MyApp.getContext()));
        userId = UtilToken.getId(MyApp.getContext());
        userService = ServiceGenerator.createService(UserService.class, jwt, AuthType.JWT);
        userProfile = getProfile();
    }

    public MutableLiveData<MyProfileResponse> getProfile(){
        if(userProfile == null){
            userProfile = new MutableLiveData<>();
        }

        Call<MyProfileResponse> getOneUser = userService.getUser(userId);
        getOneUser.enqueue(new Callback<MyProfileResponse>() {
            @Override
            public void onResponse(Call<MyProfileResponse> call, Response<MyProfileResponse> response) {
                //Resources res = getResources();
                String points = "";
                if (response.code() != 200) {
                    Log.d("Fail", "user can't be obtain successfully");
                    Toast.makeText(MyApp.getContext(), "You have to log in!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("Success", "user obtain successfully");
                    userProfile.setValue(response.body());
                    //profileDarkActivity.setItemsInfo(response);
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                Log.d("Conexion failure", "FALLITO BUENO");
                Toast.makeText(MyApp.getContext(), "Fail in the request!", Toast.LENGTH_LONG).show();
            }
        });

        return userProfile;
    }

    public MutableLiveData<UserEditResponse> updateProfile(String id, UserEditDto userEditDto) {

        final MutableLiveData<UserEditResponse> data = new MutableLiveData<>();

        Call<UserEditResponse> editUser = userService.editUser(id, userEditDto);
        editUser.enqueue(new Callback<UserEditResponse>() {
            @Override
            public void onResponse(Call<UserEditResponse> call, Response<UserEditResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "You have to log in!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("success editing user", "userUpdated");
                    Log.i("response body",response.body().toString());
                    data.setValue(response.body());
                    //Si dejo el editProdile.setValue, a la hora de editar el usuario, da un error de referencia a objeto nulo,
                    //pero el objeto que me trae el response.body no es nulo.

                }
            }

            @Override
            public void onFailure(Call<UserEditResponse> call, Throwable t) {
                Log.d("onFailure", "Fail in the request");
                Toast.makeText(MyApp.getContext(), "Fail in the request!", Toast.LENGTH_LONG).show();
            }
        });
        return data;
    }


}
