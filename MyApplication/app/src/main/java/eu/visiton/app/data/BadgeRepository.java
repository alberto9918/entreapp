package eu.visiton.app.data;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import eu.visiton.app.R;
import eu.visiton.app.common.MyApp;
import eu.visiton.app.responses.BadgeResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.BadgeService;
import eu.visiton.app.ui.badges.detail.PoisAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BadgeRepository {
    BadgeService badgeService;
    String jwt;

    BadgeRepository(){
        badgeService = ServiceGenerator.createService(BadgeService.class, jwt, AuthType.JWT);
    }

    public LiveData<List<BadgeResponse>> getBadgesAndEarnedFiltered(){
        final MutableLiveData<List<BadgeResponse>> data = new MutableLiveData<>();

        Call<List<BadgeResponse>> call = badgeService.listBadgesAndEarnedFiltered();
        call.enqueue(new Callback<List<BadgeResponse>>() {
            @Override
            public void onResponse(Call<List<BadgeResponse>> call, Response<List<BadgeResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<BadgeResponse>> call, Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(MyApp.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
        return data;
    }

    public LiveData<List<BadgeResponse>> getBadgesAndEarned(){
        final MutableLiveData<List<BadgeResponse>> data = new MutableLiveData<>();

        Call<List<BadgeResponse>> call = badgeService.listBadgesAndEarned();
        call.enqueue(new Callback<List<BadgeResponse>>() {
            @Override
            public void onResponse(Call<List<BadgeResponse>> call, Response<List<BadgeResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<BadgeResponse>> call, Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(MyApp.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
        return data;
    }

    public LiveData<List<BadgeResponse>> getBadgesAndEarnedSort(boolean asc){
        final MutableLiveData<List<BadgeResponse>> data = new MutableLiveData<>();

        Call<List<BadgeResponse>> call = null;
        if (asc) {
            call = badgeService.listBadgesAndEarned("-points");
        } else {
            call = badgeService.listBadgesAndEarned("points");
        }
        call.enqueue(new Callback<List<BadgeResponse>>() {
            @Override
            public void onResponse(Call<List<BadgeResponse>> call, Response<List<BadgeResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<BadgeResponse>> call, Throwable t) {
                Log.e("Network Error", t.getMessage());
                Toast.makeText(MyApp.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
        return data;
    }

    public LiveData<BadgeResponse> getBadgeDetails(String badgeId){

        final MutableLiveData<BadgeResponse> data = new MutableLiveData<>();

        Call<BadgeResponse> call = badgeService.getBadge(badgeId);
        call.enqueue(new Callback<BadgeResponse>() {
            @Override
            public void onResponse(Call<BadgeResponse> call, Response<BadgeResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<BadgeResponse> call, Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(MyApp.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

        return data;

    }

}
