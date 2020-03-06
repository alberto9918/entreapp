package eu.visiton.app.data;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import eu.visiton.app.common.MyApp;
import eu.visiton.app.responses.PeopleResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.ui.people.MyPeopleRecyclerViewAdapter;
import eu.visiton.app.util.UtilToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleRepository {
    UserService userService;
    String jwt;

    PeopleRepository(){
        jwt = UtilToken.getToken(MyApp.getContext());
        userService = ServiceGenerator.createService(UserService.class, jwt, AuthType.JWT);
    }

    public LiveData<List<PeopleResponse>> getUsersAndFriended(){
        final MutableLiveData<List<PeopleResponse>> data = new MutableLiveData<>();

        Call<List<PeopleResponse>> callList = userService.listUsersAndFriended();

        callList.enqueue(new Callback<List<PeopleResponse>>() {
            @Override
            public void onResponse(Call<List<PeopleResponse>> call, Response<List<PeopleResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "Error in request", Toast.LENGTH_SHORT).show();
                } else {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<PeopleResponse>> call, Throwable t) {
                Log.e("NetworkFailure", t.getMessage());
                Toast.makeText(MyApp.getContext(), "Network Failure", Toast.LENGTH_SHORT).show();
            }
        });

        return data;
    }
}
