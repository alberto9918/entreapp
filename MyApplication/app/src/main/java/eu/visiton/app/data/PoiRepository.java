package eu.visiton.app.data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eu.visiton.app.common.MyApp;
import eu.visiton.app.model.Poi;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.ui.pois.list.PoiListAdapter;
import eu.visiton.app.util.UtilToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoiRepository {
    PoiService poiService;
    String jwt;

    PoiRepository(){
        jwt = UtilToken.getToken(Objects.requireNonNull(MyApp.getContext()));
        poiService = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
    }

    public LiveData<List<PoiResponse>> getAllPois(double latitude, double longitude){
        final MutableLiveData<List<PoiResponse>> data = new MutableLiveData<>();

        String coords = longitude + "," + latitude;
        Call<ResponseContainer<PoiResponse>> call = poiService.listPois(coords, 5000);
        call.enqueue(new Callback<ResponseContainer<PoiResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Response<ResponseContainer<PoiResponse>> response) {
                if (response.code() != 200) {
                    Toast.makeText(MyApp.getContext(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    //items = Objects.requireNonNull(response.body()).getRows();
                    data.setValue(response.body().getRows());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseContainer<PoiResponse>> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(MyApp.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

        return data;
    }
}
